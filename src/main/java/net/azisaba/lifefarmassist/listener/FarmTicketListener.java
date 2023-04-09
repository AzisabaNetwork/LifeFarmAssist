package net.azisaba.lifefarmassist.listener;

import net.azisaba.lifefarmassist.LifeFarmAssist;
import net.azisaba.lifefarmassist.config.TicketConfig;
import org.bukkit.Bukkit;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FarmTicketListener implements Listener {
    private final List<TicketConfig> ticketConfig;

    public FarmTicketListener(@NotNull List<TicketConfig> ticketConfig) {
        this.ticketConfig = ticketConfig;
    }

    @EventHandler
    public void onCropBreak(BlockBreakEvent e) {
        if (!LifeFarmAssist.getInstance().getFarmAssistConfig().isAllowedWorld(e.getBlock().getWorld().getName())) {
            return;
        }
        BlockData blockData = e.getBlock().getBlockData();
        if (!(blockData instanceof Ageable)) {
            return;
        }
        Ageable ageable = (Ageable) blockData;
        if (ageable.getAge() < ageable.getMaximumAge()) return;
        for (TicketConfig config : ticketConfig) {
            if (config.getChance() <= Math.random()) {
                continue;
            }
            // invoke MMLuck command
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mmluck:mlg " + e.getPlayer().getName() + " " + config.getMythicType() + " 1 1");
        }
    }
}
