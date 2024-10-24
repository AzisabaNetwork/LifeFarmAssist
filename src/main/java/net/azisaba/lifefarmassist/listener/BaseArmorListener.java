package net.azisaba.lifefarmassist.listener;

import net.azisaba.lifefarmassist.LifeFarmAssist;
import net.azisaba.lifefarmassist.config.BaseArmorConfig;
import net.azisaba.lifefarmassist.util.PlayerUtil;
import net.azisaba.lifefarmassist.util.ServerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseArmorListener<C extends BaseArmorConfig> implements Listener {
    private final Map<String, Integer> tick = new HashMap<>();
    protected final List<C> configList;

    public BaseArmorListener(@NotNull List<C> configList) {
        this.configList = configList;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        int currTick = ServerUtil.getCurrentTick();
        for (int i = 0; i < configList.size(); i++) {
            C config = configList.get(i);
            if (!config.isEnabled()) continue;
            int prevTick = tick.getOrDefault(e.getPlayer().getUniqueId() + "_" + i, 0);
            if (currTick - prevTick < config.getTickRate()) {
                continue;
            }
            tick.put(e.getPlayer().getUniqueId() + "_" + i, currTick);

            if (!LifeFarmAssist.getInstance().getFarmAssistConfig().isAllowedWorld(e.getPlayer().getWorld().getName()) ||
                    !PlayerUtil.wearingMythicItem(e.getPlayer(), config.getMythicType())) {
                continue;
            }
            onMove(config, e.getPlayer());
        }
    }

    public abstract void onMove(@NotNull C config, @NotNull Player player);
}
