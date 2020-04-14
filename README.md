# NBTViewer

This is a simple program I made to quickly view NBT data in any file not just specific formats. Programs such as NBTExplorer could only parse specific formats which of course is pretty useless if you go outside that realm.

## CLI
You can use CLI or GUI for this, to use CLI simply do:

`java -jar NBTViewer.jar <file>`

Example: `java -jar NBTViewer.jar test.dat`

This will not show entire arrays greater than 20 entries, if you wish to see all of them also pass in the -v flag.
Example: `java -jar NBTViewer.jar -v test.dat`

## GUI
To use the GUI simply open the jar file and then File > Open File

> :warning: Be careful opening large files with the GUI! JFX doesn't handle inserting thousands of lines very well.
