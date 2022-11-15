package net.azisaba.lifefarmassist.util;

import net.minecraft.server.v1_15_R1.DedicatedServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;

public class ServerUtil {
    private static final DedicatedServer server = ((CraftServer) Bukkit.getServer()).getServer();

    public static int getCurrentTick() {
        return server.ak();
    }
}
