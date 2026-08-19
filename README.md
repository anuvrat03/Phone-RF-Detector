# RF Presence Detector 📡

An Android app that detects nearby devices by monitoring Wi-Fi and Bluetooth signals.

## Features

- 🔍 Detects Wi-Fi networks within 5 meters
- 📡 Detects Bluetooth devices within 5 meters  
- 📊 Shows signal strength (RSSI)
- 📏 Estimates distance in meters
- 🔄 Updates every 5 seconds

## How to Use

1. Open the app on your Android phone
2. Grant all permissions when asked (Location, Bluetooth, Wi-Fi)
3. Tap "Start Scanning"
4. View devices detected within 5 meters

## Permissions Required

- Location (for Wi-Fi scanning)
- Bluetooth (for BLE scanning)
- Wi-Fi state

## How It Works

The app uses:
- **Wi-Fi scanning** - Detects Wi-Fi access points
- **BLE scanning** - Detects Bluetooth Low Energy devices
- **RSSI calculation** - Estimates distance from signal strength

## Note

⚠️ Distance estimation is approximate. Walls, people, and device orientation affect accuracy.

## License

MIT License - Free to use and modify

---

Made with ❤️ for learning RF presence detection
