package net.azisaba.lifefarmassist.listener;

import net.azisaba.lifefarmassist.LifeFarmAssist;
import net.azisaba.lifefarmassist.config.BaseArmorConfig;
import net.azisaba.lifefarmassist.util.PlayerUtil;
import net.azisaba.lifefarmassist.util.ServerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class BaseArmorListener<C extends BaseArmorConfig> implements Listener {
    private final Map<UUID, Integer> tick = new HashMap<>();
    protected final C config;

    public BaseArmorListener(@NotNull C config) {
        this.config = config;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!config.isEnabled()) return;
        int prevTick = tick.getOrDefault(e.getPlayer().getUniqueId(), 0);
        int currTick = ServerUtil.getCurrentTick();
        if (currTick - prevTick < config.getTickRate()) {
            return;
        }
        tick.put(e.getPlayer().getUniqueId(), currTick);

        if (!LifeFarmAssist.getInstance().getFarmAssistConfig().isAllowedWorld(e.getPlayer().getWorld().getName()) ||
                !PlayerUtil.wearingMythicItem(e.getPlayer(), config.getMythicType())) {
            return;
        }
        onMove(e.getPlayer());
    }

    public abstract void onMove(@NotNull Player player);
}
