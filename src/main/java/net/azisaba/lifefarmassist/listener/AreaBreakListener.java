package net.azisaba.lifefarmassist.listener;

import net.azisaba.lifefarmassist.LifeFarmAssist;
import net.azisaba.lifefarmassist.config.AreaBreakArmorConfig;
import net.azisaba.lifefarmassist.region.BlockPos;
import net.azisaba.lifefarmassist.region.CuboidRegion;
import net.azisaba.lifefarmassist.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AreaBreakListener implements Listener {
    public static final Set<BlockPos> RECENTLY_BROKEN = new HashSet<>();
    private final List<AreaBreakArmorConfig> configList;

    public AreaBreakListener(LifeFarmAssist plugin) {
        this.configList = plugin.getFarmAssistConfig().getListOfType(AreaBreakArmorConfig.class);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        if (!LifeFarmAssist.getInstance().getFarmAssistConfig().isAllowedWorld(e.getPlayer().getWorld().getName())) {
            return;
        }
        BlockPos center = new BlockPos(e.getBlock().getLocation());
        if (RECENTLY_BROKEN.contains(center)) {
            // prevent loop
            return;
        }
        for (AreaBreakArmorConfig config : configList) {
            if (!config.isEnabled() || config.getRadius() == 0) continue;
            if (!PlayerUtil.wearingMythicItem(e.getPlayer(), config.getMythicType())) {
                continue;
            }
            addRecentlyBroken(center, config.getPreventAutoPlantTicks());
            for (BlockPos pos : CuboidRegion.radius(center, config.getRadius())) {
                Block block = pos.getBlock();
                if (pos.equals(center) || !(block.getBlockData() instanceof Ageable)) {
                    continue;
                }
                Ageable ageable = (Ageable) block.getBlockData();
                if (ageable.getMaximumAge() != ageable.getAge()) {
                    continue;
                }
                RECENTLY_BROKEN.add(pos);
                BlockBreakEvent event = new BlockBreakEvent(block, e.getPlayer());
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    RECENTLY_BROKEN.remove(pos);
                    continue;
                }
                block.breakNaturally(e.getPlayer().getInventory().getItemInMainHand());
                addRecentlyBroken(pos, config.getPreventAutoPlantTicks());
            }
        }
    }

    public void addRecentlyBroken(@NotNull BlockPos pos, int preventAutoPlantTicks) {
        RECENTLY_BROKEN.add(pos);
        Bukkit.getScheduler().runTaskLater(
                LifeFarmAssist.getInstance(),
                () -> Bukkit.getScheduler().runTask(LifeFarmAssist.getInstance(), () -> RECENTLY_BROKEN.remove(pos)),
                Math.max(0, preventAutoPlantTicks - 1)
        );
    }
}
