package net.azisaba.lifefarmassist.util;

import io.lumine.mythic.bukkit.MythicBukkit;
import net.azisaba.lifefarmassist.LifeFarmAssist;
import net.azisaba.lifefarmassist.config.DropBoostArmorConfig;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ItemUtil {
    @Contract("null -> null")
    @Nullable
    public static String getMythicType(@Nullable ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return null;
        }
        String mythicType = MythicBukkit.inst().getItemManager().getMythicTypeFromItem(item);
        if (mythicType != null) {
            return mythicType;
        }
        if (!item.hasItemMeta()) {
            return null;
        }
        return item.getItemMeta().getPersistentDataContainer().get(getMythicTypeKey(), PersistentDataType.STRING);
    }

    public static @NotNull NamespacedKey getMythicTypeKey() {
        return new NamespacedKey(LifeFarmAssist.getInstance(), "mythic_type");
    }

    public static @NotNull NamespacedKey getBreakCountKey() {
        return new NamespacedKey(LifeFarmAssist.getInstance(), "break_count");
    }

    public static int getBreakCount(@Nullable ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return 0;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return 0;
        }
        Integer i = meta.getPersistentDataContainer().get(getBreakCountKey(), PersistentDataType.INTEGER);
        return i == null ? 0 : i;
    }

    public static @Nullable ItemStack createDropBoostItem(@NotNull Material material, @NotNull String mythicType) {
        if (material.isAir() || !material.isItem() || getDropBoostConfig(mythicType) == null) {
            return null;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        meta.getPersistentDataContainer().set(getMythicTypeKey(), PersistentDataType.STRING, mythicType);
        item.setItemMeta(meta);
        return setBreakCount(item, 0);
    }

    public static @Nullable ItemStack setBreakCount(@Nullable ItemStack item, int breakCount) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        String mythicType = getMythicType(item);
        if (mythicType == null) {
            return null;
        }
        DropBoostArmorConfig config = getDropBoostConfig(mythicType);
        if (config == null) {
            return null;
        }

        int current = Math.max(0, Math.min(config.getMaxRequiredBreakCount(), breakCount));
        meta.getPersistentDataContainer().set(getBreakCountKey(), PersistentDataType.INTEGER, current);
        ChatColor color = getColorForBreakCount(config.getMaxRequiredBreakCount(), current);
        meta.setDisplayName(ChatColor.GOLD + "[農業] " + color + "ドロップ増加");
        meta.setLore(Arrays.asList(
                ChatColor.WHITE + "ブロック(" + config.getTargetMaterial() + ")の破壊数に応じて",
                ChatColor.WHITE + "性能が変わる農業防具です。",
                "",
                ChatColor.WHITE + "現在の破壊数: " + color + current + ChatColor.GRAY + " / " + ChatColor.WHITE + config.getMaxRequiredBreakCount(),
                ChatColor.WHITE + "現在のドロップ率: " + color + "+" + (Math.round(config.getAdditionalDropMultiplier(current) * 10000.0) / 100.0) + "%",
                "",
                ChatColor.WHITE + "このアイテムの最大ドロップ率は" + ChatColor.GOLD + "+" + (config.getMaxAdditionalDropMultiplier() * 100.0) + "%" + ChatColor.WHITE + "です。"
        ));
        item.setItemMeta(meta);
        return item;
    }

    public static @Nullable ItemStack incrementBreakCount(@Nullable ItemStack item) {
        return item == null ? null : setBreakCount(item, getBreakCount(item) + 1);
    }

    private static @Nullable DropBoostArmorConfig getDropBoostConfig(@NotNull String mythicType) {
        for (DropBoostArmorConfig config : LifeFarmAssist.getInstance().getFarmAssistConfig().getListOfType(DropBoostArmorConfig.class)) {
            if (config.getMythicType().equals(mythicType)) {
                return config;
            }
        }
        return null;
    }

    private static ChatColor getColorForBreakCount(int max, int current) {
        if (max <= 0) {
            return ChatColor.GOLD;
        }
        double d = current / (double) max;
        if (d >= 1) {
            return ChatColor.GOLD;
        } else if (d >= 0.8) {
            return ChatColor.AQUA;
        } else if (d >= 0.6) {
            return ChatColor.GREEN;
        } else if (d >= 0.4) {
            return ChatColor.YELLOW;
        } else if (d >= 0.2) {
            return ChatColor.LIGHT_PURPLE;
        } else {
            return ChatColor.RED;
        }
    }
}
