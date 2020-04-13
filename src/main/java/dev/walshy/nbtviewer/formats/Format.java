package dev.walshy.nbtviewer.formats;

import org.projectender.nbt.tag.Tag;

import java.io.File;

public interface Format {

    Tag handle(File f);

    String fileExtension();
}
