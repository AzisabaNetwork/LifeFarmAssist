package net.azisaba.lifefarmassist.util;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerUtil {
    public static boolean wearingMythicItem(@NotNull Player player, @NotNull String mythicType) {
        return mythicType.equals(ItemUtil.getMythicType(player.getInventory().getHelmet())) ||
                mythicType.equals(ItemUtil.getMythicType(player.getInventory().getChestplate())) ||
                mythicType.equals(ItemUtil.getMythicType(player.getInventory().getLeggings())) ||
                mythicType.equals(ItemUtil.getMythicType(player.getInventory().getBoots()));
    }
}
