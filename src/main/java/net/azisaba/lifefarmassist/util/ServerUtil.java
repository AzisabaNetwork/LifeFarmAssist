package net.azisaba.lifefarmassist.util;

import org.bukkit.Bukkit;
import org.bukkit.Server;

public class ServerUtil {
    private static final Server server = Bukkit.getServer();

    public static int getCurrentTick() {
        return server.getCurrentTick();
    }
}
