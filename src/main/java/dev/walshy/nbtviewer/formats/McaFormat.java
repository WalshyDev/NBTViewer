package dev.walshy.nbtviewer.formats;

import dev.walshy.nbtviewer.CompressionType;
import dev.walshy.nbtviewer.NBTViewer;
import org.projectender.nbt.tag.Tag;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class McaFormat implements Format {

    private final int SECTOR = 4096;

    @Override
    public Tag handle(File f) {
        try (RandomAccessFile file = new RandomAccessFile(f, "r")) {
            file.seek(SECTOR * 2); // Skip locations and timestamps

            final int length = file.readInt();
            final byte compressionId = file.readByte();
            final CompressionType type = CompressionType.fromId(compressionId);
            if (type == null) {
                NBTViewer.printOutput("Failed to find compression type from file! Got ID: '" + compressionId + "'");
                return null;
            }

            byte[] data = new byte[length - 1];
            file.read(data);

            try (DataInputStream dataStream = type.getStream(new ByteArrayInputStream(data))) {
                if (dataStream == null) return null;
                return Tag.readTag(dataStream);
            }
        } catch (IOException e) {
            NBTViewer.printOutput(e);
            return null;
        }
    }

    @Override
    public String fileExtension() {
        return "mca";
    }
}
