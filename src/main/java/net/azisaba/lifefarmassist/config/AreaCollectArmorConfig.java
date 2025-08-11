package net.azisaba.lifefarmassist.config;

import net.azisaba.lifefarmassist.LifeFarmAssist;
import net.azisaba.lifefarmassist.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class AreaCollectArmorConfig extends BaseArmorConfig {
    public static final String TYPE = "area-collect";
    private final int radius;
    private final boolean unfollowIfFullInventory;
    private final int verticalCollectRadius;

    public AreaCollectArmorConfig(ConfigurationSection section) {
        super(section);
        this.radius = Math.max(0, section.getInt("radius", 5));
        this.unfollowIfFullInventory = section.getBoolean("unfollow-if-full-inventory", true);
        this.verticalCollectRadius = Math.max(0, section.getInt("vertical-collect-radius", this.radius));
    }

    public int getRadius() {
        return radius;
    }

    public int getVerticalCollectRadius() {
        return verticalCollectRadius;
    }

    public void startTask() {
        if (!isEnabled() || getRadius() == 0) return;
        Bukkit.getScheduler().runTaskTimer(LifeFarmAssist.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getGameMode() == GameMode.SPECTATOR) continue;
                // if (unfollowIfFullInventory && player.getInventory().firstEmpty() == -1) continue;
                if (!LifeFarmAssist.getInstance().getFarmAssistConfig().isAllowedWorld(player.getWorld().getName()) ||
                        !PlayerUtil.wearingMythicItem(player, getMythicType())) {
                    continue;
                }
                for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), getRadius(), getVerticalCollectRadius(), getRadius(), entity -> entity instanceof Item)) {
                    Vector vec = new Vector(
                            player.getLocation().getX() - entity.getLocation().getX(),
                            player.getLocation().getY() + player.getEyeHeight() / 2.0 - entity.getLocation().getY(),
                            player.getLocation().getZ() - entity.getLocation().getZ()
                    );
                    double d = vec.lengthSquared();
                    if (Math.sqrt(d) < 0.4) {
                        entity.setVelocity(new Vector());
                        continue;
                    }
                    if (d < 256) {
                        double d2 = 1.0 - Math.sqrt(d) / 8.0;
                        entity.setVelocity(entity.getVelocity().add(vec.normalize().multiply(d2 * d2 * 0.3)));
                    }
                }
            }
        }, getTickRate(), getTickRate());
    }
}
