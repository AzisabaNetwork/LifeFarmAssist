package net.azisaba.lifefarmassist.config;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class AutoGrowArmorConfig extends BaseArmorConfig {
    public static final String TYPE = "auto-grow";
    private final int radius;
    private final double chance;

    public AutoGrowArmorConfig(@NotNull ConfigurationSection section) {
        super(section);
        this.radius = Math.max(0, section.getInt("radius", 5));
        this.chance = Math.max(0, section.getDouble("chance", 0.2));
    }

    public int getRadius() {
        return radius;
    }

    public double getChance() {
        return chance;
    }
}
