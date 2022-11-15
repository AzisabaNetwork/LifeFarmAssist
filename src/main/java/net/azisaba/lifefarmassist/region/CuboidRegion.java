package net.azisaba.lifefarmassist.region;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CuboidRegion implements Region {
    private final BlockPos min;
    private final BlockPos max;

    public CuboidRegion(@NotNull BlockPos min, @NotNull BlockPos max) {
        this.min = min;
        this.max = max;
    }

    @NotNull
    public BlockPos min() {
        return min;
    }

    @NotNull
    public BlockPos max() {
        return max;
    }

    @Override
    @NotNull
    public Iterator<BlockPos> iterator() {
        return new Itr(min, max);
    }

    @Contract("_, _ -> new")
    public static @NotNull CuboidRegion radius(@NotNull BlockPos center, int radius) {
        return new CuboidRegion(center.add(radius, radius, radius), center.subtract(radius, radius, radius));
    }

    @Contract("_, _ -> new")
    public static @NotNull CuboidRegion radiusIgnoreY(@NotNull BlockPos center, int radius) {
        return new CuboidRegion(center.add(radius, 0, radius), center.subtract(radius, 0, radius));
    }

    private static class Itr implements Iterator<BlockPos> {
        private final BlockPos min;
        private final int xLength;
        private final int yLength;
        private final int zLength;
        private int x;
        private int y;
        private int z;

        public Itr(BlockPos min, BlockPos max) {
            int minX = Math.min(min.x(), max.x());
            int minY = Math.min(min.y(), max.y());
            int minZ = Math.min(min.z(), max.z());
            int maxX = Math.max(min.x(), max.x());
            int maxY = Math.max(min.y(), max.y());
            int maxZ = Math.max(min.z(), max.z());
            this.min = new BlockPos(min.world(), minX, minY, minZ);
            this.xLength = maxX - minX + 1;
            this.yLength = maxY - minY + 1;
            this.zLength = maxZ - minZ + 1;
            this.x = 0;
            this.y = 0;
            this.z = 0;
        }

        @Override
        public boolean hasNext() {
            return x < xLength && y < yLength && z < zLength;
        }

        @Override
        public BlockPos next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            BlockPos pos = min.add(x, y, z);
            if (++x >= xLength) {
                x = 0;
                if (++y >= yLength) {
                    y = 0;
                    ++z;
                }
            }
            return pos;
        }
    }
}
