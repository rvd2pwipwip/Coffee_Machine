# Qello Fire TV
This project was created by tweeking the existing Fire TV App Builder.

Fire App Builder is a java framework that allows developers to quickly build immersive, java based Android media applications for Fire TV, without writing code.  Fire App Builder accomplishes this by using a plug and play java framework with easy configuration files.  Developers simply specify the format of their media feed in a json file and add resources for logos and colors to create a rich media TV experience quickly.  Fire App Builder supports multiple modules for Analytics, Authentication and Advertising that you can enable for your app.

References:
Full Documentation is located [here](https://developer.amazon.com/public/solutions/devices/fire-tv/docs/fire-app-builder-overview).

The Git Repository for is located [here](https://github.com/amzn/fire-app-builder)

## Needed Softwares
[Android Studio](https://developer.android.com/studio)
- Main IDE for Android Development
- Will facilitate the installation of the SDK, NDK, Emulators and what not.

[Git Bash](https://git-scm.com/downloads)
- In case you need to clone for the first time

[SDK Platform Tools](https://developer.android.com/studio/releases/platform-tools)
- Needed this mainly for the `adb.exe` in order to push build on android boxes
- I do believe that Android Studio also installs it somewhere on your computer

##  How to clone the repo
The base repo uses symlinks in their project structure. Thus, special instructions are needed to clone the project

1. Reinstall Git Bash
- Make sure you uncheck "only show new options"
- Make sure you check "Allow symbolic links" when given the option
2. Run Git bash as admin
3. cd to your workspace
4. In gitbash (admin)
- run `cd <WORKSPACE_LOCATION>`
- run `git config --global core.symlink true`
- run `export MSYS=winsymlinks:nativestrict`
- If you set up an SSH Key with Git, perform`git clone sa_gitlab@gitserver.corp.stingraydigital.com:rogue/qello-android-fire-tv.git`
- Otherwise, `git clone https://gitserver.corp.stingraydigital.com/rogue/qello-android-fire-tv`
5. With Android Studio, open the project `<WORKSPACE_LOCATION>/qello-android-fire-tv/Application`

##  How to update strings viaf  Lokalise
Lokalie is used from string management
Visit https://lokalise.com/

Project lokalise folder is located at path_to_project\qello-android-fire-tv\lokalise

To update strings: 
1. Navigate to lokalise folder
2. Run PullStringsFromLokalise.bat
3. Navigate to lokalie/values
4. Copy block of strings (excluding resource tags) and paste in strings-{lang}.xml everywhere there is a <!-- LOKALISE --> comment (replace block)
-There are strings that are not in lokalise because they are specific to amazon fire tv