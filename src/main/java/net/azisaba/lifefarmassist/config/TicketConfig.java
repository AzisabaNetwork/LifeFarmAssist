package net.azisaba.lifefarmassist.config;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TicketConfig {
    private final double chance;
    private final String mythicType;

    public TicketConfig(double chance, @NotNull String mythicType) {
        if (chance < 0) throw new IllegalArgumentException("chance < 0");
        this.chance = chance;
        this.mythicType = Objects.requireNonNull(mythicType, "mythicType is null");
    }

    public double getChance() {
        return chance;
    }

    public @NotNull String getMythicType() {
        return mythicType;
    }
}
