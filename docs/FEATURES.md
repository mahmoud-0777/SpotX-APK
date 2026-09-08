# Features

SpotX-APK includes the following patch groups:

- **Ad-blocking**: Rewrites known ad endpoints and ad package references.
- **Hide podcasts/episodes/audiobooks**: Replaces content category markers to prioritize music-only interfaces.
- **Disable auto-updates**: Disables common in-app update paths.
- **Experimental features**: Forces selected feature-flag values to enabled states.
- **Analytics blocking**: Rewrites analytics endpoints and tracking package references.

> Patch signatures can differ per Spotify version. Update `patches/patches.json` when new APK versions change smali strings/classes.

Available profiles:

- **Full**: all patch groups
- **Premium**: ad/update/analytics focus
- **Minimal**: ad-blocking only
