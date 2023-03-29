package net.azisaba.lifefarmassist.config;

import org.bukkit.configuration.ConfigurationSection;

// tick-rate is not used
public class AreaBreakArmorConfig extends BaseArmorConfig {
    public static final String TYPE = "area-break";
    private final int radius;
    private final int preventAutoPlantTicks;

    public AreaBreakArmorConfig(ConfigurationSection section) {
        super(section);
        this.radius = Math.max(0, section.getInt("radius", 2));
        this.preventAutoPlantTicks = Math.max(0, section.getInt("prevent-auto-plant-ticks", 600));
    }

    public int getRadius() {
        return radius;
    }

    public int getPreventAutoPlantTicks() {
        return preventAutoPlantTicks;
    }
}
