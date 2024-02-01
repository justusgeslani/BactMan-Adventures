# BactMan Adventures

BactMan Adventures is an Android game popularizing biology and science.
It has been developed by the [2015 IONIS iGEM Team](http://2015.igem.org/Team:IONIS_Paris),
who were awarded a gold medal for their participation in the 2015 competition.

[![Download BactMan Adventures on the Google Play Store](/../screenshots/img/poster.png "Optional Title")](https://play.google.com/store/apps/details?id=fr.plnech.igem)

## Table of Contents
* [Installation/Setup](#Installation/Setup)
* [Contributions](#Contributions)
* [Credits](#Credits)
* [License](#License)
* [Disclaimer](#Disclaimer)

## Installation/Setup

* Ensure you have the [latest](https://developer.android.com/tools/sdk/ndk/index.html) version of Android SDK installed with the NDK Bundle.

* Clone this repository and `cd` in its folder.

* Run the following command:

```
git submodule init && git submodule update
```

For those using Android Studio: 
- Select `File`->`New`->`Import project`
- Select the `build.gradle` file at the project root.

For those *not* using Android Studio:
- Create a file called `local.properties` with the content below:

```
#!ini
sdk.dir=C\:\\Path\\To\\Your\\AndroidSDK\\sdk
ndk.dir=C\:\\Path\\To\\Your\\AndroidSDK\\sdk\\ndk-bundle   
```
## Contributions

* All contributions are welcome as pull requests from a [feature branch](https://www.atlassian.com/git/tutorials/comparing-workflows/feature-branch-workflow).
* This project has no formal coding style, yet **please do your best** to be consistent with its codebase.

## Credits

Name of original contributor(s) of this project.

## License

See [license](LICENSE.txt).

## Disclaimer

See [disclaimer](DISCLAIMER.txt).

