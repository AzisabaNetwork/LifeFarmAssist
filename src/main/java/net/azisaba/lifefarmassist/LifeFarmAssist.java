package net.azisaba.lifefarmassist;

import net.azisaba.lifefarmassist.config.AreaCollectArmorConfig;
import net.azisaba.lifefarmassist.config.FarmAssistConfig;
import net.azisaba.lifefarmassist.listener.AreaBreakListener;
import net.azisaba.lifefarmassist.listener.AutoGrowArmorListener;
import net.azisaba.lifefarmassist.listener.AutoPlantArmorListener;
import net.azisaba.lifefarmassist.listener.FarmTicketListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class LifeFarmAssist extends JavaPlugin {
    private static LifeFarmAssist instance;
    private FarmAssistConfig config;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.config = new FarmAssistConfig(getConfig());
        this.config.getListOfType(AreaCollectArmorConfig.class).forEach(AreaCollectArmorConfig::startTask);
        Bukkit.getPluginManager().registerEvents(new FarmTicketListener(this.config.getTicket()), this);
        Bukkit.getPluginManager().registerEvents(new AutoPlantArmorListener(this), this);
        Bukkit.getPluginManager().registerEvents(new AutoGrowArmorListener(this), this);
        Bukkit.getPluginManager().registerEvents(new AreaBreakListener(this), this);
    }

    @Contract(pure = true)
    @NotNull
    public FarmAssistConfig getFarmAssistConfig() {
        return config;
    }

    @Contract(pure = true)
    @NotNull
    public static LifeFarmAssist getInstance() {
        return Objects.requireNonNull(instance, "LifeFarmAssist is not loaded");
    }
}
