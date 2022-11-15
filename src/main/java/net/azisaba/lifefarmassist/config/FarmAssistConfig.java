package net.azisaba.lifefarmassist.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class FarmAssistConfig {
    private final boolean allowedOnAllWorlds;
    private final Set<String> allowedWorlds;
    private final AutoPlantArmorConfig autoPlantArmorConfig;
    private final AutoGrowArmorConfig autoGrowArmorConfig;
    private final AreaBreakArmorConfig areaBreakArmorConfig;
    private final AreaCollectArmorConfig areaCollectArmorConfig;

    public FarmAssistConfig(@NotNull ConfigurationSection section) {
        this.allowedOnAllWorlds = section.getBoolean("allowed-on-all-worlds", false);
        this.allowedWorlds = new HashSet<>(section.getStringList("allowed-worlds"));
        this.autoPlantArmorConfig = new AutoPlantArmorConfig(getSection(section, "auto-plant"));
        this.autoGrowArmorConfig = new AutoGrowArmorConfig(getSection(section, "auto-grow"));
        this.areaBreakArmorConfig = new AreaBreakArmorConfig(getSection(section, "area-break"));
        this.areaCollectArmorConfig = new AreaCollectArmorConfig(getSection(section, "area-collect"));
    }

    public boolean isAllowedOnAllWorlds() {
        return allowedOnAllWorlds;
    }

    @NotNull
    public Set<String> getAllowedWorlds() {
        return allowedWorlds;
    }

    @NotNull
    public AutoPlantArmorConfig getAutoPlantArmorConfig() {
        return autoPlantArmorConfig;
    }

    @NotNull
    public AutoGrowArmorConfig getAutoGrowArmorConfig() {
        return autoGrowArmorConfig;
    }

    @NotNull
    public AreaBreakArmorConfig getAreaBreakArmorConfig() {
        return areaBreakArmorConfig;
    }

    @NotNull
    public AreaCollectArmorConfig getAreaCollectArmorConfig() {
        return areaCollectArmorConfig;
    }

    public boolean isAllowedWorld(@NotNull String worldName) {
        if (isAllowedOnAllWorlds()) {
            // If allowedOnAllWorlds is true, this list works like blockedWorlds.
            return !getAllowedWorlds().contains(worldName);
        } else {
            return getAllowedWorlds().contains(worldName);
        }
    }

    @NotNull
    private static ConfigurationSection getSection(@NotNull ConfigurationSection section, @NotNull String key) {
        ConfigurationSection child = section.getConfigurationSection(key);
        if (child != null) {
            return child;
        }
        return new MemoryConfiguration();
    }
}
