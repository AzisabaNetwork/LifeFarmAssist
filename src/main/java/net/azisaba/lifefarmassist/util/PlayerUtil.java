package net.azisaba.lifefarmassist.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;

public class PlayerUtil {
    public static boolean wearingMythicItem(@NotNull Player player, @NotNull String mythicType) {
        return mythicType.equals(ItemUtil.getMythicType(player.getInventory().getHelmet())) ||
                mythicType.equals(ItemUtil.getMythicType(player.getInventory().getChestplate())) ||
                mythicType.equals(ItemUtil.getMythicType(player.getInventory().getLeggings())) ||
                mythicType.equals(ItemUtil.getMythicType(player.getInventory().getBoots()));
    }

    /**
     * Returns the helmet, chestplate, leggings, boots as iterator.
     * Note that the items are fetched in {@link ReplaceableIterator#next()}, not during this method.
     * You can use {@link ReplaceableIterator#replace(Object)} to replace the current item.
     * @param player the player
     * @return the iterator
     */
    public static @NotNull ReplaceableIterator<@Nullable ItemStack> getArmors(@NotNull Player player) {
        return new ReplaceableIterator<ItemStack>() {
            private int armorSlot = -1;

            @Override
            public void replace(ItemStack itemStack) {
                player.getInventory().setItem(player.getInventory().getSize() - 2 - armorSlot, itemStack);
            }

            @Override
            public boolean hasNext() {
                return armorSlot < 3;
            }

            @Override
            public ItemStack next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return player.getInventory().getItem(player.getInventory().getSize() - 2 - (++armorSlot));
            }
        };
    }
}
