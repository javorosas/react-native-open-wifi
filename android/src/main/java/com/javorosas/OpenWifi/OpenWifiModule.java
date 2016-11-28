package com.javorosas.OpenWifi;

import com.facebook.react.uimanager.*;
import com.facebook.react.bridge.*;
import com.facebook.systrace.Systrace;
import com.facebook.systrace.SystraceMessage;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;

import android.os.Bundle;
import android.content.Context;
import java.util.List;
import com.facebook.systrace.Systrace;
import com.facebook.systrace.SystraceMessage;

import com.facebook.react.LifecycleState;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class OpenWifiModule extends ReactContextBaseJavaModule {
  public OpenWifiModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  public String getName() {
    return "OpenWifi";
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