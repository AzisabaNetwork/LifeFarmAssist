package net.azisaba.lifefarmassist.util;

import net.azisaba.lifefarmassist.LifeFarmAssist;
import net.azisaba.lifefarmassist.config.DropBoostArmorConfig;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
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
        net.minecraft.server.v1_15_R1.ItemStack nms = CraftItemStack.asNMSCopy(item);
        if (nms == null) {
            return null;
        }
        NBTTagCompound tag = nms.getTag();
        if (tag == null || !tag.hasKey("MYTHIC_TYPE")) {
            return null;
        }
        return tag.getString("MYTHIC_TYPE");
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

    public static @Nullable ItemStack incrementBreakCount(@Nullable ItemStack item) {
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
        DropBoostArmorConfig config = null;
        for (DropBoostArmorConfig element : LifeFarmAssist.getInstance().getFarmAssistConfig().getListOfType(DropBoostArmorConfig.class)) {
            if (element.getMythicType().equals(mythicType)) {
                config = element;
                break;
            }
        }
        if (config == null) return null;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        Integer currentRaw = pdc.get(getBreakCountKey(), PersistentDataType.INTEGER);
        int current = currentRaw == null ? 0 : currentRaw;
        pdc.set(getBreakCountKey(), PersistentDataType.INTEGER, Math.min(config.getMaxRequiredBreakCount(), ++current));
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

    private static ChatColor getColorForBreakCount(int max, int current) {
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