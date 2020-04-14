package dev.walshy.nbtviewer.formats;

import java.io.File;

public interface Format {

    String handle(File f, boolean verbose);

    String fileExtension();
}
