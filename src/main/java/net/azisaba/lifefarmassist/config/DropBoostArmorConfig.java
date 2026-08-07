package net.azisaba.lifefarmassist.config;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class DropBoostArmorConfig extends BaseArmorConfig {
    public static final String TYPE = "drop-boost";

    private final double maxDropMultiplier;
    private final int divisor;
    private final Material targetMaterial;
    private final boolean includeSeeds;
    private final RoundingMode roundingMode;

    public DropBoostArmorConfig(ConfigurationSection section) {
        super(section);
        this.maxDropMultiplier = Math.max(0.0D, section.getDouble("max-drop-multiplier", 3.0D));
        this.divisor = Math.max(1, section.getInt("divisor", 1));
        this.targetMaterial = Material.valueOf(Objects.requireNonNull(section.getString("target-material", "CARROTS")).toUpperCase(Locale.ROOT));
        this.includeSeeds = section.getBoolean("include-seeds", false);
        this.roundingMode = RoundingMode.from(section.getString("rounding-mode", "CHANCE"));
    }

    public double getMaxAdditionalDropMultiplier() {
        return maxDropMultiplier;
    }

    public int getDivisor() {
        return divisor;
    }

    public Material getTargetMaterial() {
        return targetMaterial;
    }

    public boolean isIncludeSeeds() {
        return includeSeeds;
    }

    public RoundingMode getRoundingMode() {
        return roundingMode;
    }

    // Helper methods

    public int getMaxRequiredBreakCount() {
        return (int) Math.ceil(getDivisor() * getMaxAdditionalDropMultiplier());
    }

    public double getAdditionalDropMultiplier(int breakCount) {
        return Math.min(getMaxAdditionalDropMultiplier(), breakCount / (double) getDivisor());
    }

    public enum RoundingMode {
        FLOOR,
        ROUND,
        CHANCE;

        public static RoundingMode from(String raw) {
            if (raw == null) {
                return CHANCE;
            }
            try {
                return valueOf(raw.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {
                return CHANCE;
            }
        }
    }
}
