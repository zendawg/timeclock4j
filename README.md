# timeclock4j

Clock-in and -out using old emacs-style .timelog files.

Reports can also be generated.

# Build Instructions: Desktop, CLI and Android

To build, run:

    gradlew clean install
    
This will NOT install documentation - see below for this.
    
To run CLI utilties, unzip one of the archives in timeclockj.tools/build/distributions into the location of your choice: <location>

change directory into `<location>/timeclockj.tools-1.2/bin` and run (on Linux/Mac):

    ./timeclockj.tools -h
    
... which will show command line options. On Windows, run:

    timeclockj.tools.bat -h
   
To start the system-docked tray, unzip one of the archives in timeclockj.ui/build/distributions into the location of your choice: <location>:
and change directory into `<location>/timeclockj-gui-1.2/bin`

On Linux, run:

    ./timeclockj-gui -h
   
To figure out what options to set:

Recommended are `-i [x]` and `-r` (these are `-i [x]` for interval to check changes to .timelog file, and `-r` to reload on change), as follows:
For synchronisation with the Android app, the user must set the timelog file to be called '.timelog' in the Apps/Timeclock4j directory in the user's Dropbox directory.

    ./timeclockj-gui -f ~/Dropbox/Apps/Timeclock4j/.timelog -i 5 -r
   
... to reload every 5 seconds if a change in the file is detected.

On Windows, run:

	timeclockj-gui.bat -has
	
or (replacing <username> appropriately):

	timeclockj-gui.bat --file C:\Users\<username>\Dropbox\Apps\Timeclock4j\.timelog -i 5 -r 

You might like to put a shortcut to a bat file containing the following line in your Startup directory

(which on Windows 10 is C:\Users\<username>\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup):

start /min timeclockj-gui.bat --file C:\Users\<username>\Dropbox\Apps\Timeclock4j\.timelog -i 5 -r 

# Generating Documentation

In order to generate documentation, the tex suite of tools is used. 

The maven (or ant) build scripts must be invoked from inside the timeclockj.docs directory

Files are generated in target/docs and produce several new directories for different formats and options of formatting:

    javahelp/  multi-page-html/  pdf/  single-page-html/

For those that do not have the time (or patience) to install `texinfo` or do not run Linux, the documentation, once built, is deployed to:

    timeclockj.ui/src/main/resources/doc/

Simply open up the `index.html` file kept within that directory with your preferred browser to take a look at the original documentation (which is now out of date).

# Android

Install the app using Android Studio or from the command line using gradlew:

	gradlew installDebug

# Synchronisation & Origins

The purpose of `timeclock4j` originally was to enable synchronisation via Dropbox both through CLI, UI (system tray) and Android versions; when a clock-in or -out was performed, the changes would get pushed to Dropbox and the changes detected by any of the applications being ran.

The Android app has to be synchronised manually (pull & push). The Android app assumes that the timelog file will be called '.timelog' and be in the following directory in the user's Dropbox:

Apps/Timeclock4j