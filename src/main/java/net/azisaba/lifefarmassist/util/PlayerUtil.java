package net.azisaba.lifefarmassist.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class PlayerUtil {
    public static boolean wearingMythicItem(@NotNull Player player, @NotNull String mythicType) {
        EntityEquipment equipment = player.getEquipment();
        if(equipment == null) return false;
        return Stream.of(equipment.getHelmet(), equipment.getChestplate(), equipment.getLeggings(), equipment.getBoots())
                .anyMatch(stack -> mythicType.equals(ItemUtil.getMythicType(stack)));
    }
}
