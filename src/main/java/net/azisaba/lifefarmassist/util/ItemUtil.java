package net.azisaba.lifefarmassist.util;

import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
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
}
