package net.azisaba.lifefarmassist.region;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class BlockPos {
    private final World world;
    private final int x;
    private final int y;
    private final int z;

    @Contract("null, _, _, _ -> fail")
    public BlockPos(World world, int x, int y, int z) {
        this.world = Objects.requireNonNull(world, "world");
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockPos(@NotNull Location location) {
        this(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @NotNull
    public World world() {
        return world;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int z() {
        return z;
    }

    @NotNull
    public BlockPos world(@NotNull World world) {
        return new BlockPos(world, x, y, z);
    }

    @NotNull
    public BlockPos x(int x) {
        return new BlockPos(world, x, y, z);
    }

    @NotNull
    public BlockPos y(int y) {
        return new BlockPos(world, x, y, z);
    }

    @NotNull
    public BlockPos z(int z) {
        return new BlockPos(world, x, y, z);
    }

    @NotNull
    public BlockPos add(@NotNull BlockFace face) {
        return add(face.getModX(), face.getModY(), face.getModZ());
    }

    @NotNull
    public BlockPos add(int modX, int modY, int modZ) {
        return new BlockPos(world, x + modX, y + modY, z + modZ);
    }

    @NotNull
    public BlockPos subtract(@NotNull BlockFace face) {
        return subtract(face.getModX(), face.getModY(), face.getModZ());
    }

    @NotNull
    public BlockPos subtract(int modX, int modY, int modZ) {
        return new BlockPos(world, x - modX, y - modY, z - modZ);
    }

    public int distance(@NotNull BlockPos other) {
        return (int) Math.sqrt(distanceSquared(other));
    }

    public int distanceSquared(@NotNull BlockPos other) {
        return (int) (Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2) + Math.pow(z - other.z, 2));
    }

    @NotNull
    public Block getBlock() {
        return world().getBlockAt(x(), y(), z());
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockPos)) return false;
        BlockPos blockPos = (BlockPos) o;
        return x == blockPos.x && y == blockPos.y && z == blockPos.z && world.equals(blockPos.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, y, z);
    }

    @Override
    public String toString() {
        return "BlockPos{" +
                "world=" + world +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
