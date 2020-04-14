package dev.walshy.nbtviewer;

import com.github.luben.zstd.ZstdInputStream;
import dev.walshy.nbtviewer.formats.Format;
import dev.walshy.nbtviewer.formats.mc.McaFormat;
import org.projectender.nbt.NBTUtils;
import org.projectender.nbt.exception.NBTException;
import org.projectender.nbt.tag.Tag;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class NBTViewer {

    private static final Set<Format> formats = new HashSet<>();

    private static NBTViewer instance;

    private boolean verbose;

    public static void main(String[] args) {
        formats.add(new McaFormat());

        (instance = new NBTViewer()).run(args);
    }

    private void run(String[] args) {
        if (args.length > 0) {
            String fileName = args[0];
            if (args[0].equals("-v")) {
                this.verbose = true;
                if (args.length >= 2)
                    fileName = args[1];
                else {
                    printOutput("Please specify a file!");
                    return;
                }
            }

            File f = new File(fileName);
            if (f.exists()) {
                instance.readFile(f);
            } else {
                System.err.println("File '" + fileName + "' does not exist!");
            }
            return;
        }

        System.out.println("Running GUI");
        GUI.run();
    }

    protected void readFile(File f) {
        new Thread(() -> {
            final String extension = f.getName().substring(f.getName().lastIndexOf('.') + 1);
            for (Format format : formats) {
                if (format.fileExtension().equals(extension)) {
                    printOutput("Loading...");
                    final String output = format.handle(f, verbose);
                    if (output != null)
                        printOutput(output);
                    return;
                }
            }

            final DataInputStream stream = getStream(f);
            if (stream == null) {
                printOutput("Failed to read file!!");
                return;
            }

            try {
                printOutput("Loading...");
                printOutput(NBTUtils.toString(Tag.readTag(stream), verbose));
                stream.close();
            } catch (NBTException | IOException e) {
                printOutput(e);
            }
        }).start();
    }

    private DataInputStream getStream(File f) {
        final CompressionType type = CompressionType.getType(f);
        if (type == null) return null;

        try {
            final InputStream stream = new FileInputStream(f);
            final InputStream input;
            switch (type) {
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
            printOutput(e);
            return null;
        }
    }

    public static void printOutput(String str) {
        if (GUI.getController() != null) {
            GUI.getController().setResult(str);
        } else {
            System.out.println(str);
        }
    }

    public static void printOutput(Exception e) {
        if (GUI.getController() != null) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            GUI.getController().setResult(writer.toString());
        } else {
            e.printStackTrace();
        }
    }

    public static NBTViewer getInstance() {
        return instance;
    }
}
