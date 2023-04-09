package net.azisaba.lifefarmassist.config;

import net.azisaba.lifefarmassist.util.MapBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FarmAssistConfig {
    public static final Map<String, Function<ConfigurationSection, BaseArmorConfig>> TYPES =
            Collections.unmodifiableMap(
                    new MapBuilder<String, Function<ConfigurationSection, BaseArmorConfig>>()
                            .put(AutoPlantArmorConfig.TYPE, AutoPlantArmorConfig::new)
                            .put(AutoGrowArmorConfig.TYPE, AutoGrowArmorConfig::new)
                            .put(AreaBreakArmorConfig.TYPE, AreaBreakArmorConfig::new)
                            .put(AreaCollectArmorConfig.TYPE, AreaCollectArmorConfig::new)
                            .build()
            );
    private final boolean allowedOnAllWorlds;
    private final Set<String> allowedWorlds;
    private final List<TicketConfig> ticket = new ArrayList<>();
    private final List<BaseArmorConfig> list = new ArrayList<>();

    public FarmAssistConfig(@NotNull ConfigurationSection section) {
        this.allowedOnAllWorlds = section.getBoolean("allowed-on-all-worlds", false);
        this.allowedWorlds = new HashSet<>(section.getStringList("allowed-worlds"));
        for (Map<?, ?> map : section.getMapList("ticket")) {
            MemoryConfiguration subSection = new MemoryConfiguration();
            map.forEach((key, value) -> subSection.set(key.toString(), value));
            double chance = subSection.getDouble("chance", 0.0);
            String mythicType = subSection.getString("mythic-type", null);
            ticket.add(new TicketConfig(chance, Objects.requireNonNull(mythicType, "mythic-type is not set")));
        }
        for (Map<?, ?> map : section.getMapList("list")) {
            MemoryConfiguration subSection = new MemoryConfiguration();
            map.forEach((key, value) -> subSection.set(key.toString(), value));
            String type = subSection.getString("type", "<not set>");
            Function<ConfigurationSection, ? extends BaseArmorConfig> constructor = TYPES.get(type);
            if (constructor == null) {
                throw new IllegalArgumentException("Unknown type: " + type);
            }
            list.add(Objects.requireNonNull(constructor.apply(subSection), "constructor of " + type + " returned null"));
        }
    }

    public boolean isAllowedOnAllWorlds() {
        return allowedOnAllWorlds;
    }

    @NotNull
    public Set<String> getAllowedWorlds() {
        return allowedWorlds;
    }

    public @NotNull List<@NotNull TicketConfig> getTicket() {
        return ticket;
    }

    public @NotNull List<@NotNull BaseArmorConfig> getList() {
        return list;
    }

    public <T extends BaseArmorConfig> @NotNull List<@NotNull T> getListOfType(@NotNull Class<T> clazz) {
        return list.stream().filter(clazz::isInstance).map(clazz::cast).collect(Collectors.toList());
    }

    public boolean isAllowedWorld(@NotNull String worldName) {
        if (isAllowedOnAllWorlds()) {
            // If allowedOnAllWorlds is true, this list works like blockedWorlds.
            return !getAllowedWorlds().contains(worldName);
        } else {
            return getAllowedWorlds().contains(worldName);
        }
    }
}
