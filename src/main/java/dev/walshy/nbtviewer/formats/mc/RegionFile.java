package dev.walshy.nbtviewer.formats.mc;

import dev.walshy.nbtviewer.CompressionType;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.BitSet;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class RegionFile implements AutoCloseable {

    private static final int SECTOR_BYTES = 4096;
    private static final int SECTOR_INTS = SECTOR_BYTES / 4;

    private final int[] offsets;
    private final RandomAccessFile file;
    private final BitSet sectorsUsed;

    public RegionFile(File regionFile) throws IOException {
        offsets = new int[SECTOR_INTS];

        file = new RandomAccessFile(regionFile, "rw");

        // Setup the available sectors
        int totalSectors = (int) Math.ceil(file.length() / (double) SECTOR_BYTES);
        sectorsUsed = new BitSet(totalSectors);

        // Reserve the first 2 sectors
        sectorsUsed.set(0, 2);

        // Read the offset table.
        file.seek(0);

        ByteBuffer header = ByteBuffer.allocate(2 * SECTOR_BYTES);
        while (header.hasRemaining()) {
            if (file.getChannel().read(header) == -1) {
                throw new EOFException();
            }
        }
        header.flip();

        // Populate the offset table
        IntBuffer headerIntBuffer = header.asIntBuffer();
        for (int i = 0; i < SECTOR_INTS; ++i) {
            int offset = headerIntBuffer.get();
            offsets[i] = offset;

            int startSector = offset >> 8;
            int numSectors = offset & 255;

            if (offset != 0 && startSector >= 0 && (startSector + numSectors) <= totalSectors) {
                sectorsUsed.set(startSector, startSector + numSectors + 1);
            } else if (offset != 0) {
//                logger.warn("Region \"{}\": offsets[{}] = {} -> {}, {} does not fit",
//                    regionFile.getName(), i, offset, startSector, numSectors);
                System.err.println("error101");
            }
        }
    }

    public DataInputStream getChunkDataInputStream(int x, int z) throws IOException {
        checkBounds(x, z);

        if (!hasChunk(x, z))
            throw new IllegalArgumentException("This chunk (" + x + ", " + z + ") does not exist!");

        int offset = getOffset(x, z);
        if (offset == 0)
            return null;

        int totalSectors = sectorsUsed.length();
        int sectorNumber = offset >> 8;
        int numSectors = offset & 0xFF;
        if ((sectorNumber + numSectors) > totalSectors)
            throw new IOException("Invalid sector: " + sectorNumber + "+" + numSectors + " > " + totalSectors);

        file.seek((long) sectorNumber * SECTOR_BYTES);
        int length = file.readInt();
        if (length > (SECTOR_BYTES * numSectors))
            throw new IOException("Invalid length: " + length + " > " + (SECTOR_BYTES * numSectors));
        else if (length <= 0)
            throw new IOException("Invalid length: " + length + " <= 0");

        byte compressionId = file.readByte();
        CompressionType type = CompressionType.fromId(compressionId);

        if (type == null)
            throw new IOException("Unknown compression type, ID: " + compressionId);

        byte[] data = new byte[length - 1];
        file.read(data);
        return getZlibInputStream(data);
    }

    public boolean hasChunk(int x, int z) {
        return getOffset(x, z) != 0;
    }

    private void checkBounds(int x, int z) {
        if (x < 0 || x >= 32 || z < 0 || z >= 32)
            throw new IllegalArgumentException("Chunk out of bounds: " + x + ", " + z);
    }

    private int getOffset(int x, int z) {
        return offsets[x + (z << 5)];
    }

    private DataInputStream getZlibInputStream(byte[] data) {
        return new DataInputStream(new InflaterInputStream(new ByteArrayInputStream(data), new Inflater(), 2048));
    }

    public void close() throws IOException {
        file.getChannel().force(true);
        file.close();
    }
}
