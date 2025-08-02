package net.azisaba.lifefarmassist.listener;

import net.azisaba.lifefarmassist.LifeFarmAssist;
import net.azisaba.lifefarmassist.config.AutoGrowArmorConfig;
import net.azisaba.lifefarmassist.region.BlockPos;
import net.azisaba.lifefarmassist.region.CuboidRegion;
import net.azisaba.lifefarmassist.region.Region;
import org.bukkit.Material;
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
        if (center.getBlock().getType() == Material.FARMLAND) {
            center = center.add(0, 1, 0);
        }

        Region region;
        if (config.isVerticalGrowth()) {
            region = CuboidRegion.radius(center, config.getRadius());
        } else {
            region = CuboidRegion.radiusIgnoreY(center, config.getRadius());
        }

        for (BlockPos pos : region) {
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
