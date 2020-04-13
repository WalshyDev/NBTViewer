package dev.walshy.nbtviewer;

import com.github.luben.zstd.ZstdInputStream;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public enum CompressionType {

    NONE(0),
    GZIP(1),
    ZLIB(2),
    ZSTD(121);

    private static final CompressionType[] values = values();

    private final int id;

    CompressionType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public DataInputStream getStream(InputStream stream) {
        try {
            final InputStream input;
            switch (this) {
                case GZIP:
                    input = new GZIPInputStream(stream);
                    break;
                case ZLIB:
                    input = new InflaterInputStream(stream, new Inflater(), 2048);
                    break;
                case ZSTD:
                    input = new ZstdInputStream(stream);
                    break;
                case NONE:
                default:
                    input = stream;
                    break;
            }

            return new DataInputStream(input);
        } catch (IOException e) {
            NBTViewer.printOutput(e);
            return null;
        }
    }

    public static CompressionType fromId(int id) {
        for (CompressionType type : values) {
            if (type.id == id)
                return type;
        }

        return null;
    }

    public static CompressionType getType(File file) {
        if (!file.exists())
            return CompressionType.NONE;

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] signature = new byte[4];
            int read = fis.read(signature);
            if (read != 4)
                return NONE;
            else {
                if (signature[0] == (byte) 0x1f && signature[1] == (byte) 0x8b)
                    return GZIP;
                else if (signature[0] == (byte) 0x78
                    && (signature[1] == (byte) 0x01 || signature[1] == (byte) 0x5E || signature[1] == (byte) 0x9C
                    || signature[1] == (byte) 0xDA)
                )
                    return ZLIB;
                else if (signature[0] == (byte) 0xFD && signature[1] == (byte) 0x2F && signature[2] == (byte) 0xB5
                    && signature[3] == (byte) 0x28)
                    return ZSTD;
            }
            return NONE;
        } catch (IOException ignored) {
            return null;
        }
    }
}