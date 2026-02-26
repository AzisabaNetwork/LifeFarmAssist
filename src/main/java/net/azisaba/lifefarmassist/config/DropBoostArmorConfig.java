package net.azisaba.lifefarmassist.config;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DropBoostArmorConfig extends BaseArmorConfig {
    public static final String TYPE = "drop-boost";

    private final double dropMultiplier;
    private final boolean onlyFullyGrown;
    private final Set<Material> targetMaterials;
    private final boolean includeSeeds;
    private final RoundingMode roundingMode;

    public DropBoostArmorConfig(ConfigurationSection section) {
        super(section);
        this.dropMultiplier = Math.max(1.0D, section.getDouble("drop-multiplier", 1.0D));
        this.onlyFullyGrown = section.getBoolean("only-fully-grown", true);
        this.targetMaterials = parseMaterials(section.getStringList("target-materials"));
        this.includeSeeds = section.getBoolean("include-seeds", false);
        this.roundingMode = RoundingMode.from(section.getString("rounding-mode", "CHANCE"));
    }

    public double getDropMultiplier() {
        return dropMultiplier;
    }

    public boolean isOnlyFullyGrown() {
        return onlyFullyGrown;
    }

    public Set<Material> getTargetMaterials() {
        return targetMaterials;
    }

    public boolean isIncludeSeeds() {
        return includeSeeds;
    }

    public RoundingMode getRoundingMode() {
        return roundingMode;
    }

    public boolean isTargetMaterial(Material material) {
        return targetMaterials.isEmpty() || targetMaterials.contains(material);
    }

    private Set<Material> parseMaterials(List<String> names) {
        if (names == null || names.isEmpty()) {
            return Collections.emptySet();
        }
        Set<Material> set = EnumSet.noneOf(Material.class);
        for (String raw : names) {
            if (raw == null || raw.trim().isEmpty()) {
                continue;
            }
            try {
                set.add(Material.valueOf(raw.trim().toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return Collections.unmodifiableSet(set);
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
