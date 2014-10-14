Android Accordion
=================

Android accordion emulator for tablets.

![](https://github.com/billthefarmer/billthefarmer.github.io/raw/master/images/Accordion.png)
&nbsp;
![](https://github.com/billthefarmer/billthefarmer.github.io/raw/master/images/Accordion-settings.png)

This project uses the Sonivox EAS library, libsonivox.so, which is included in Android, but not documented or accessible using the standard Android NDK toolkit. In order to use this library you need to:

  * Get and install the Android NDK toolkit.
  * Get the documentation and include files from https://github.com/android/platform_external_sonivox.
  * Get a copy of the libsonivox.so library from an Android device or an emulator (in system/lib) and put it with the other libraries in the platforms/android-14/arch-arm/usr/lib folder of the NDK.
  * Add it to your ~/jni/Android.mk file along with any other libraries.

The windows NDK toolkit doesn't appear to use anything other than standard windows shell commands and it's own tools, so you don't need Cygwin, despite what the docs say.
