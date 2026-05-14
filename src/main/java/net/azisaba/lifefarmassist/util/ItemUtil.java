package net.azisaba.lifefarmassist.util;

import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class ItemUtil {
    @Contract("null -> null")
    @Nullable
    public static String getMythicType(@Nullable ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return null;
        }
        return MythicBukkit.inst().getItemManager().getMythicTypeFromItem(item);
    }
}