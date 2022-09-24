# AOATE
### <u>A</u>bsolutely <u>O</u>nly <u>A</u>nother <u>T</u>erminal text <u>E</u>ditor


<small>*Name not yet set in stone, suggestions welcome.</small>


Formally named "Java-Based-File-Editor", this project is a command-line file editor built entirely in Java.

<img alt="Latest updated image of terminal usage" src="./LatestScreenshot.png" title="Screenshot of program"/>

To open the default file (./Directory/exampleFile4.txt):
```
java Main
```
To open any file:
```
java Main "<file_path_here>"
```

This program has the basic functions you can expect from a text editor including cursor wrapping in all directions, ~~scrolling~~, ~~undo/redo~~, adding new lines, and saving.

This project has been developed using OpenJDK 1.8.0_342 but should be compatible with other versions of java. Major updates are also tested with a OpenJRE 11 version. No external libraries are used so the minimum space this program should take up (excluding JRE) is simply just the .class files from ./out/* (at time of writing, only 15.1 KiB).