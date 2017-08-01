# timeclock4j

Clock-in and -out using old emacs-style .timelog files.

Reports can also be generated.

# Build Instructions: Desktop and CLI

You'll need Apache Maven installed to build the software. To build, run:

    mvn clean install
    
This will NOT install documentation - see below for this.
    
To run CLI utilties, change directory into `timeclockj.tools/` and run (on Linux/Mac):

    ./target/appassembler/bin/timeclockj -h
    
... which will show command line options. On Windows, run:

    cmd target\appassembler\bin\timeclockj.bat -h
   
To start the system-docked tray on Linux, run:

    ./timeclockj.ui/target/appassembler/bin/timeclockj-gui -h
   
To figure out what options to set - recommended are `-i [x]` and `-r` (these are `-i [x]` for interval to check changes to .timelog file, and `-r` to reload on change), as follows:

    ./timeclockj.ui/target/appassembler/bin/timeclockj-gui -i 5 -r
   
... to reload every 5 seconds if a change in the file is detected.

The same is true for Windows, except use the `cmd` executable and suffix the file with `.bat` and ensure path separators are back-sashes, not (*nix-like) forward slashes.

# Generating Documentation

In order to generate documentation, the tex suite of tools is used. For Maven, these are only invoked when the `-Dgenerate.docs=true` is set.

Files are generated in target/docs and produce several new directories for different formats and options of formatting:

    javahelp/  multi-page-html/  pdf/  single-page-html/

# Android

The version of Android has not worked in a long time - there are plans to migrate this to Gradle.

# Synchronisation & Origins

The purpose of `timeclock4j` originally was to enable synchronisation via Dropbox both through CLI, UI (system tray) and Android versions; when a clock-in or -out was performed, the changes would get pushed to Dropbox and the changes detected by any of the applications being ran.

With the deprecation of the Maven Android version, this has now become defunct, although there are plans to re-implement this in the near future.
