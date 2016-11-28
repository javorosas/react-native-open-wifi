/**
 * @providesModule react-native-open-wifi
 */

var { RNOpenWifi } = require('react-native').NativeModules;

var OpenWifi = {
  connect: (ssid) => {
    return Promise.race([timeOut(), connectToSsid(ssid)]);
  }
};

const connectToSsid = (ssid) => {
  return new Promise((resolve, reject) => {
    var { NetInfo } = require('react-native');
    // Connect to WiFi network
    RNOpenWifi.connect(ssid);

    NetInfo.addEventListener('change', networkChanged);

    function networkChanged (state) {
      console.log(state);
      if (/^wi/i.test(state)) {
        NetInfo.removeEventListener('change', networkChanged);
        RNOpenWifi.getSSID(function (currentSsid) {
          console.log(currentSsid);
          if (currentSsid === ssid) {
            resolve();
          } else {
            reject(new Error('Couldn\'t find the network'));
          }
        });
      }
    }
  });
};

const ONE_SECOND = 1000;
const timeOut = (milliseconds = 20 * ONE_SECOND) => {
  return new Promise((resolve, reject) => {
    setTimeout(reject, milliseconds, new Error('Timeout!'));
  });
};

module.exports = OpenWifi;
