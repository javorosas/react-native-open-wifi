## react-native-open-wifi

[![npm version](https://badge.fury.io/js/react-native-open-wifi.svg)](https://badge.fury.io/js/react-native-open-wifi)
[![js-semistandard-style](https://img.shields.io/badge/code%20style-semistandard-brightgreen.svg?style=flat-square)](https://github.com/Flet/semistandard)

Connect to unsecured, open WiFi networks and get the status of the WiFi connection on the device.

Based on the work of [skierkowski/react-native-open-wifi-manager](https://github.com/skierkowski/react-native-wifi-manager)

**Android only**. Programatically connecting to WiFi networks on iOS is not possible. You should show instructions
telling your user to connect manually.

## Installation

First you need to install react-native-open-wifi:

```javascript
npm install react-native-open-wifi --save
```

* In `android/settings.gradle`

```gradle
...
include ':OpenWifi', ':app'
project(':OpenWifi').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-open-wifi/android')
```

* In `android/app/build.gradle`

```gradle
...
dependencies {
    ...
    compile project(':OpenWifi')
}
```

* register module (in MainActivity.java or MainApplication.java)

On newer versions of React Native (0.18+):

```java
import com.javorosas.OpenWifi.*;  // <--- import

public class MainActivity extends ReactActivity {
  ......

  /**
   * A list of packages used by the app. If the app uses additional views
   * or modules besides the default ones, add more packages here.
   */
    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
        new OpenWifiPackage(), // <------ add here
        new MainReactPackage());
    }
}
```

On older versions of React Native:

```java
import com.javorosas.OpenWifi.*;  // <--- import

public class MainActivity extends Activity implements DefaultHardwareBackBtnHandler {
  ......

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mReactRootView = new ReactRootView(this);

    mReactInstanceManager = ReactInstanceManager.builder()
      .setApplication(getApplication())
      .setBundleAssetName("index.android.bundle")
      .setJSMainModuleName("index.android")
      .addPackage(new MainReactPackage())
      .addPackage(new new OpenWifiPackage())              // <------ add here
      .setUseDeveloperSupport(BuildConfig.DEBUG)
      .setInitialLifecycleState(LifecycleState.RESUMED)
      .build();

    mReactRootView.startReactApplication(mReactInstanceManager, "ExampleRN", null);

    setContentView(mReactRootView);
  }

  ......

}
```

## Example

### Load module
```javascript
var OpenWifi = require('react-native-open-wifi');
```

### Connect to a new network (connect)
```javascript
// Attempts to connect to the network specified.
// The promise will be rejected if not connected after 20 seconds.
OpenWifi.connect(ssid, { timeout: 10000 }) // Timeout (optional) 10 seconds
  .then(() => console.log('Successfully connected to', ssid))
  .catch(err => console.log('Check message for failure info'));
```

### TODO (by priority)
```
[x] Change connect result to polling the current SSID
[x] Make timeout configurable
[x] Resolve connect promise immediately if the currentSSID equals the wanted SSID
[ ] Expose more methods, such as getting current SSID
```
