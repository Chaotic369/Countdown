# Widget Studio

Android app with a WebView-based editor (app/src/main/assets/index.html) that creates real home-screen widgets: a text widget and a live countdown widget.

## JS bridge (window.Android)

- save(type, json): store the config and refresh widgets. type is "text" or "count".
- load(type): return the saved config JSON.
- pin(type): ask the launcher to pin the widget to the home screen.

## Build

Push to GitHub. The workflow in .github/workflows/build.yml builds the debug APK and uploads it as an artifact.

## Notes

- Widgets refresh every 15 minutes via WorkManager and on every save.
- Replace the placeholder assets/index.html with the real editor file.
