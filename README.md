# FreeDub AI — Android Starter Project

## Current prototype features
- Opens Android's system video picker.
- Lets the user choose Hindi or Urdu as a target voice language.
- Lets the user paste already-translated text.
- Uses the phone's installed Android Text-to-Speech voice to synthesize speech to an app-private WAV file.

## Important limitation
This is a starter app, not an automatic full-movie dubbing app yet. It does **not**:
- extract dialogue automatically from the selected video;
- translate English dialogue automatically;
- preserve speaker identities or background music;
- synchronize long translated dialogue to the original scenes;
- mux the generated audio into a finished MP4.

Those stages require integration and testing of speech recognition (ASR), translation, text-to-speech, timing/alignment, and video muxing. Full 1–3 hour processing on a phone may be slow and memory-intensive. No paid API is included in this starter.

## Build requirements
- Android Studio on a computer is the simplest supported build path.
- JDK 17 and Android SDK Platform 35.
- Internet may be required to download Gradle/Android dependencies, but the app does not call a paid API.

## Build APK in Android Studio
1. Unzip this project.
2. Open the `FreeDubAI` folder in Android Studio.
3. Let Gradle sync finish.
4. Choose **Build > Build APK(s)**.
5. Android Studio will show the output path, normally `app/build/outputs/apk/debug/app-debug.apk`.
6. Transfer the APK to your phone and install it. You may need to allow installation from that file manager.

## Can I build it on Android only?
Android Studio is not available as a normal Android phone app. Mobile IDEs and online build services vary and may have limitations. This project is easiest to compile on a computer; do not upload private videos to an unknown online build service.

## Next development stages
1. Add audio extraction with a compatible FFmpeg build.
2. Add on-device/offline ASR, such as a mobile-compatible Whisper implementation.
3. Add offline translation if a suitable Hindi/Urdu model is available and licensed for redistribution.
4. Generate TTS in manageable chunks.
5. Add dialogue timing and audio/video muxing.
6. Test on short clips before attempting long movies.
