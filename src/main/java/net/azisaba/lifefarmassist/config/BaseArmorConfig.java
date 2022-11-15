package net.azisaba.lifefarmassist.config;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class BaseArmorConfig {
    private final boolean enabled;
    private final String mythicType;
    private final int tickRate;

    public BaseArmorConfig(boolean enabled, @NotNull String mythicType, int tickRate) {
        this.enabled = enabled;
        this.mythicType = mythicType;
        this.tickRate = tickRate;
    }

    public BaseArmorConfig(@NotNull ConfigurationSection section) {
        this(section.getBoolean("enabled", false),
                Objects.requireNonNull(section.getString("mythic-type", "none")),
                Math.max(1, section.getInt("tick-rate", 10)));
    }

    public boolean isEnabled() {
        return enabled;
    }

    @NotNull
    public String getMythicType() {
        return mythicType;
    }

    public int getTickRate() {
        return tickRate;
    }
}
