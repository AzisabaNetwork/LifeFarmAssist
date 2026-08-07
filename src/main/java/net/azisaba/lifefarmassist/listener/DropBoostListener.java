package net.azisaba.lifefarmassist.listener;

import net.azisaba.lifefarmassist.LifeFarmAssist;
import net.azisaba.lifefarmassist.config.DropBoostArmorConfig;
import net.azisaba.lifefarmassist.util.ItemUtil;
import net.azisaba.lifefarmassist.util.PlayerUtil;
import net.azisaba.lifefarmassist.util.ReplaceableIterator;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DropBoostListener implements Listener {
    private final List<DropBoostArmorConfig> configList;

    public DropBoostListener(LifeFarmAssist plugin) {
        this.configList = plugin.getFarmAssistConfig().getListOfType(DropBoostArmorConfig.class);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockDrop(BlockDropItemEvent e) {
        Player player = e.getPlayer();
        BlockState block = e.getBlockState();

        if (!LifeFarmAssist.getInstance().getFarmAssistConfig().isAllowedWorld(block.getWorld().getName())) {
            return;
        }

        for (DropBoostArmorConfig config : configList) {
            if (!config.isEnabled()) {
                continue;
            }
            if (config.getMaxAdditionalDropMultiplier() <= 0.0D) {
                continue;
            }
            if (!PlayerUtil.wearingMythicItem(player, config.getMythicType())) {
                continue;
            }
            if (config.getTargetMaterial() != block.getType()) {
                continue;
            }
            int breakCount = 0;
            for (ReplaceableIterator<ItemStack> it = PlayerUtil.getArmors(player); it.hasNext();) {
                ItemStack armor = it.next();
                if (config.getMythicType().equals(ItemUtil.getMythicType(armor))) {
                    breakCount = ItemUtil.getBreakCount(armor);
                    break;
                }
            }
            if (breakCount == 0) {
                continue;
            }
            if (!isFullyGrown(block)) {
                continue;
            }

            for (Item item : e.getItems()) {
                ItemStack stack = item.getItemStack();
                if (stack == null || stack.getType().isAir()) {
                    continue;
                }
                if (!config.isIncludeSeeds() && isSeedDrop(stack.getType())) {
                    continue;
                }

                int boosted = applyMultiplier(stack.getAmount(), 1 + config.getAdditionalDropMultiplier(breakCount), config.getRoundingMode());
                if (boosted > stack.getAmount()) {
                    stack.setAmount(boosted);
                    item.setItemStack(stack);
                }
            }
        }
    }

    private boolean isFullyGrown(BlockState block) {
        if (!(block.getBlockData() instanceof Ageable)) {
            return false;
        }
        Ageable ageable = (Ageable) block.getBlockData();
        return ageable.getAge() >= ageable.getMaximumAge();
    }

    private int applyMultiplier(int amount, double multiplier, DropBoostArmorConfig.RoundingMode mode) {
        double raw = amount * multiplier;
        switch (mode) {
            case FLOOR:
                return (int) Math.floor(raw);
            case ROUND:
                return (int) Math.round(raw);
            case CHANCE:
            default:
                int base = (int) Math.floor(raw);
                double fraction = raw - base;
                if (fraction > 0 && ThreadLocalRandom.current().nextDouble() < fraction) {
                    base++;
                }
                return base;
        }
    }

    private boolean isSeedDrop(Material material) {
        return material == Material.WHEAT_SEEDS || material == Material.BEETROOT_SEEDS;
    }
}
