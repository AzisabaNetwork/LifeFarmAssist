package net.azisaba.lifefarmassist.config;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class AutoPlantArmorConfig extends BaseArmorConfig {
    private final int plantRadius;
    private final int growRadius;

    public AutoPlantArmorConfig(@NotNull ConfigurationSection section) {
        super(section);
        this.plantRadius = Math.max(0, section.getInt("plant-radius", 5));
        this.growRadius = Math.max(0, section.getInt("grow-radius", 2));
        if (plantRadius < growRadius) {
            throw new IllegalArgumentException("plant-radius must be greater than or equal to grow-radius");
        }
    }

    public int getPlantRadius() {
        return plantRadius;
    }

    public int getGrowRadius() {
        return growRadius;
    }
}
