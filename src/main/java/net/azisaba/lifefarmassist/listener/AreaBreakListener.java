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
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class AreaBreakListener implements Listener {
    public static final Set<BlockPos> RECENTLY_BROKEN = new HashSet<>();
    private final AreaBreakArmorConfig config;

    public AreaBreakListener(LifeFarmAssist plugin) {
        this.config = plugin.getFarmAssistConfig().getAreaBreakArmorConfig();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!config.isEnabled() || config.getRadius() == 0) return;
        if (!LifeFarmAssist.getInstance().getFarmAssistConfig().isAllowedWorld(e.getPlayer().getWorld().getName()) ||
                !PlayerUtil.wearingMythicItem(e.getPlayer(), config.getMythicType())) {
            return;
        }
        BlockPos center = new BlockPos(e.getBlock().getLocation());
        addRecentlyBroken(center);
        for (BlockPos pos : CuboidRegion.radius(center, config.getRadius())) {
            Block block = pos.getBlock();
            if (!pos.equals(center) && block.getBlockData() instanceof Ageable) {
                block.breakNaturally(e.getPlayer().getInventory().getItemInMainHand());
                addRecentlyBroken(pos);
            }
        }
    }

    public void addRecentlyBroken(@NotNull BlockPos pos) {
        RECENTLY_BROKEN.add(pos);
        Bukkit.getScheduler().runTaskLaterAsynchronously(
                LifeFarmAssist.getInstance(),
                () -> Bukkit.getScheduler().runTask(LifeFarmAssist.getInstance(), () -> RECENTLY_BROKEN.remove(pos)),
                config.getPreventAutoPlantTicks() - 1
        );
    }
}
