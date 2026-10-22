# DreamStream Ultra — Android UI prototype v0.2.0

This is an Android Studio project with a GitHub Actions workflow to build a **debug APK online**. The build has not been run or tested in this environment, so this ZIP is source code, not an APK.

## Implemented in this prototype
- Dark background and cyan DreamStream Ultra branding
- Login form with editable **Puto (Server Address)** field; default `http://vpn-esxtrapro.info:80`
- Username/password fields and saved server/username preference
- Home navigation for Live TV, Movies, Series, Favourites and Settings
- Sample channel names and player-preview screen
- PIN gate for server changes using demo PIN `007`

## Not yet implemented or verified
- Xtream Codes API login, M3U parsing or real IPTV provider connection
- Actual video playback (player is currently a placeholder), EPG/XMLTV, catch-up, search, posters, series metadata
- Persisted favourites, subscription-expiry lookup, genuine VPN detection
- Secure storage of credentials; PIN is hard-coded and is not strong security
- Fire TV / Android TV remote navigation and device testing
- Production signing key or release APK

Only use media sources you are authorized to access. No channels, streams or credentials are included.

## Build an APK online with GitHub Actions
1. Download and unzip this project. Create a new repository at https://github.com/new.
2. Upload the contents of the `DreamStreamUltra` folder to the repository root (so `app/` and `settings.gradle` are at the root). Commit to the `main` branch.
3. Open the repository's **Actions** tab and run **Build DreamStream Ultra APK** (or push a commit to `main`).
4. When the workflow succeeds, open that run and download the `DreamStreamUltra-debug-apk` artifact. Extract it to get `app-debug.apk`.
5. Install the debug APK on your own Android test device. Android may ask you to allow installs from your browser or file manager.

If the build fails, copy the first Gradle error from the workflow log; it will need fixing before an APK can be produced.
