package com.javorosas.OpenWifi;

import com.facebook.react.bridge.*;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;

import java.util.List;

public class OpenWifiModule extends ReactContextBaseJavaModule {
  public OpenWifiModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  public String getName() {
    return "RNOpenWifi";
  }

  @ReactMethod
  public void connect(String ssid) {
    WifiConfiguration conf = new WifiConfiguration();
    conf.SSID = "\"" + ssid + "\"";
    conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
    
    // Remove the existing configuration for this netwrok
    WifiManager wifiManager = (WifiManager) getReactApplicationContext().getSystemService(Context.WIFI_SERVICE);
    List<WifiConfiguration> mWifiConfigList = wifiManager.getConfiguredNetworks();
    String comparableSSID = ('"' + ssid + '"'); //Add quotes because wifiConfig.SSID has them
    for (WifiConfiguration wifiConfig : mWifiConfigList) {
      if (wifiConfig.SSID.equals(comparableSSID)) {
        int networkId = wifiConfig.networkId;
        wifiManager.removeNetwork(networkId);
        wifiManager.saveConfiguration();
      }
    }
    // Add configuration to Android wifi manager settings...
    int networkId = wifiManager.addNetwork(conf);

    // Enable it so that android can connect
    wifiManager.disconnect();
    wifiManager.enableNetwork(networkId, true);
    wifiManager.reconnect();
  }

  @ReactMethod
  public void status(Callback statusResult) {
    ConnectivityManager connManager = (ConnectivityManager) getReactApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    statusResult.invoke(mWifi.getState().toString());
  }

  @ReactMethod
	public void getSSID(Callback callback) {
    WifiManager wifiManager = (WifiManager) getReactApplicationContext().getSystemService(Context.WIFI_SERVICE);
    WifiInfo info = wifiManager.getConnectionInfo();

    if (info.getSupplicantState() == SupplicantState.COMPLETED) {
      // This value should be wrapped in double quotes, so we need to unwrap it.
      String ssid = info.getSSID();
      if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
        ssid = ssid.substring(1, ssid.length() - 1);
      }
      callback.invoke(ssid);
    } else {
      callback.invoke(null);
    }
  }

  private static Integer findNetworkInExistingConfig(WifiManager wifiManager, String ssid) {
   List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
   for (WifiConfiguration existingConfig : existingConfigs) {
     if (existingConfig.SSID.equals(ssid)) {
       return existingConfig.networkId;
     }
   }
   return null;
 }
}