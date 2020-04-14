package dev.walshy.nbtviewer.utils;

import java.util.Objects;

public class IntPair {

    private final int x;
    private final int z;

    public IntPair(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IntPair)) return false;
        if (obj == this) return true;
        final IntPair o = (IntPair) obj;

        return this.x == o.x && this.z == o.z;
    }
}
