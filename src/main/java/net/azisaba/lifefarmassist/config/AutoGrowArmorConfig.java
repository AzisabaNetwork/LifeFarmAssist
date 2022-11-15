package net.azisaba.lifefarmassist.config;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class AutoGrowArmorConfig extends BaseArmorConfig {
    private final int radius;

    public AutoGrowArmorConfig(@NotNull ConfigurationSection section) {
        super(section);
        this.radius = Math.max(0, section.getInt("radius", 5));
    }

    public int getRadius() {
        return radius;
    }
}
