# Privacy Policy for Open DualSpace

**Last Updated: July 1, 2026**

## Introduction

Open DualSpace ("we", "our", or "us") is an open-source Android application that allows users to clone and run dual instances of their installed apps. This Privacy Policy explains how we handle your information when you use our app.

## Information We Collect

### Information We Do NOT Collect

Open DualSpace is designed with privacy as a core principle. We do **not** collect, store, or transmit:

- Personal identification information (name, email, phone number)
- Location data
- Browsing history
- Contacts or call logs
- Photos, media, or files
- Device identifiers for tracking purposes

### Information Stored Locally

The following information is stored **only on your device** and never transmitted to any server:

- **App Preferences**: Your theme choice (light/dark mode), app lock settings, and display preferences are stored locally using Android's DataStore.
- **Cloned App Registry**: The list of apps you have chosen to clone is stored locally on your device.
- **App Icons and Names**: Temporarily cached in memory for display purposes only.

### Permissions We Request

| Permission | Purpose |
|---|---|
| `QUERY_ALL_PACKAGES` | To display the list of installed apps available for cloning |
| `RECEIVE_BOOT_COMPLETED` | To restore cloned app states after device restart (if enabled) |
| `POST_NOTIFICATIONS` | To show progress notifications during clone operations |
| `USE_BIOMETRIC` | To provide biometric app lock security (if enabled) |
| `FOREGROUND_SERVICE` | To run clone operations in the background |
| `INTERNET` | Required for displaying advertisements |
| `ACCESS_NETWORK_STATE` | Required for advertisement delivery |

## Third-Party Services

### Google AdMob

Open DualSpace uses Google AdMob to display advertisements. Google AdMob may collect certain device information for ad personalization and delivery. Please refer to [Google's Privacy Policy](https://policies.google.com/privacy) for details on how Google handles your data.

You can opt out of personalized ads in your device's Google Settings under **Ads > Opt out of Ads Personalization**.

## Data Security

All app data is stored locally on your device using Android's secure storage mechanisms. We do not operate any servers or cloud services that process your data.

## Children's Privacy

Open DualSpace does not knowingly collect any personal information from children under 13 years of age. The app is intended for general audiences.

## Work Profile & Device Administration

When the app creates an Android Work Profile (on supported devices), it uses Android's built-in profile isolation. All cloned app data remains on your device within the Work Profile container. No data from the Work Profile is transmitted externally by our app.

## Changes to This Policy

We may update this Privacy Policy from time to time. Any changes will be reflected in the "Last Updated" date at the top of this page and committed to our [GitHub repository](https://github.com/Tyagism/Open-DualSpace).

## Open Source

Open DualSpace is open-source software. You can review our complete source code at:
[https://github.com/Tyagism/Open-DualSpace](https://github.com/Tyagism/Open-DualSpace)

## Contact Us

If you have any questions about this Privacy Policy, please open an issue on our GitHub repository:
[https://github.com/Tyagism/Open-DualSpace/issues](https://github.com/Tyagism/Open-DualSpace/issues)
