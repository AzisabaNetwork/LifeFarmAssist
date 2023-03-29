package net.azisaba.lifefarmassist.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder<K, V> {
    private final Map<K, V> map;

    public MapBuilder() {
        this.map = new HashMap<>();
    }

    public MapBuilder(@NotNull Map<K, V> map) {
        this.map = map;
    }

    @Contract("_, _ -> this")
    public @NotNull MapBuilder<K, V> put(@NotNull K key, V value) {
        map.put(key, value);
        return this;
    }

    @Contract("_ -> this")
    public @NotNull MapBuilder<K, V> putAll(@NotNull Map<K, V> map) {
        this.map.putAll(map);
        return this;
    }

    public @NotNull Map<K, V> build() {
        return map;
    }
}
