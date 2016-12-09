/**
 * @providesModule react-native-open-wifi
 */

var { RNOpenWifi } = require('react-native').NativeModules;

var OpenWifi = {
  connect: connectToSsid,
  isMobileDataEnabled: isMobileDataEnabled
};

const ONE_SECOND = 1000;
const POLLING_FREQUENCY = 500;

function connectToSsid (ssid, { timeout = 10 * ONE_SECOND } = {}, debug) {
  return new Promise((resolve, reject) => {
    RNOpenWifi.getSSID((currentSsid) => {
      if (currentSsid === ssid) {
        resolve();
        return;
      }
      // Connect to WiFi network
      RNOpenWifi.connect(ssid);
      // Check every 500 milliseconds if we are connected to the right SSID
      var timeElapsed = 0;
      function checkSSID () {
        setTimeout(function () {
          timeElapsed += POLLING_FREQUENCY;
          RNOpenWifi.getSSID((currentSsid) => {
            RNOpenWifi.status(status => {
              // console.log('ssid:', currentSsid, 'status:', status);
              if (currentSsid === ssid && status === 'COMPLETED') {
                resolve();
              } else if (timeElapsed >= timeout) {
                reject(new Error('Couldn\'t find the network'));
              } else {
                checkSSID();
              }
            });
          });
        }, POLLING_FREQUENCY);
      }
      checkSSID();
    });
  });
}

function isMobileDataEnabled () {
  return new Promise((resolve, reject) => {
    RNOpenWifi.isMobileDataEnabled(isEnabled => {
      resolve(isEnabled);
    });
  });
}

module.exports = OpenWifi;
