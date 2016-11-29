/**
 * @providesModule react-native-open-wifi
 */

var { RNOpenWifi } = require('react-native').NativeModules;

var OpenWifi = {
  connect: connectToSsid
};

const ONE_SECOND = 1000;
const POLLING_FREQUENCY = 500;

const connectToSsid = (ssid, { timeout = 20 * ONE_SECOND }) => {
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
      const checkSSID = () => {
        setTimeout(() => {
          timeElapsed += POLLING_FREQUENCY;
          RNOpenWifi.getSSID((currentSsid) => {
            console.log(currentSsid);
            if (currentSsid === ssid) {
              resolve();
            } else if (timeElapsed >= timeout) {
              reject(new Error('Couldn\'t find the network'));
            } else {
              checkSSID();
            }
          });
        }, POLLING_FREQUENCY);
      };
      checkSSID();
    });
  });
};

module.exports = OpenWifi;
