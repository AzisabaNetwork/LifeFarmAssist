package net.azisaba.lifefarmassist.listener;

import net.azisaba.lifefarmassist.LifeFarmAssist;
import net.azisaba.lifefarmassist.config.AutoGrowArmorConfig;
import net.azisaba.lifefarmassist.region.BlockPos;
import net.azisaba.lifefarmassist.region.CuboidRegion;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AutoGrowArmorListener extends BaseArmorListener<AutoGrowArmorConfig> {
    public AutoGrowArmorListener(@NotNull LifeFarmAssist plugin) {
        super(plugin.getFarmAssistConfig().getListOfType(AutoGrowArmorConfig.class));
    }

    @Override
    public void onMove(@NotNull AutoGrowArmorConfig config, @NotNull Player player) {
        BlockPos center = new BlockPos(player.getLocation());
        for (BlockPos pos : CuboidRegion.radius(center, config.getRadius())) {
            if (config.getChance() <= Math.random()) {
                continue;
            }
            Block block = pos.getBlock();
            if (!(block.getBlockData() instanceof Ageable)) {
                continue;
            }
            Ageable ageable = (Ageable) block.getBlockData();
            ageable.setAge(Math.min(ageable.getAge() + 1, ageable.getMaximumAge()));
            block.setBlockData(ageable);
        }
    }
}
