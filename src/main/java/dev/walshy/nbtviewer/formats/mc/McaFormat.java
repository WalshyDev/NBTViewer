package dev.walshy.nbtviewer.formats.mc;

import dev.walshy.nbtviewer.NBTViewer;
import dev.walshy.nbtviewer.formats.Format;
import dev.walshy.nbtviewer.utils.IntPair;
import org.projectender.nbt.NBTUtils;
import org.projectender.nbt.tag.Tag;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class McaFormat implements Format {

    @Override
    public String handle(File f, boolean verbose) {
        Map<IntPair, Tag> tags = new LinkedHashMap<>();
        try (RegionFile regionFile = new RegionFile(f)) {
            for (int x = 0; x < 32; x++) {
                for (int z = 0; z < 32; z++) {
                    if (regionFile.hasChunk(x, z))
                        tags.put(new IntPair(x, z), Tag.readTag(regionFile.getChunkDataInputStream(x, z)));
                }
            }
        } catch (IOException e) {
            NBTViewer.printOutput(e);
            return null;
        }

        return tags.entrySet().stream()
            .map(entry -> "-- Chunk [" + entry.getKey().getX() + "," + entry.getKey().getZ() + "]"
                + "\n" + NBTUtils.toString(entry.getValue(), verbose))
            .collect(Collectors.joining("\n\n"));
    }

    @Override
    public String fileExtension() {
        return "mca";
    }
}
