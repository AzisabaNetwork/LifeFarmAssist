package net.azisaba.lifefarmassist.listener;

import net.azisaba.lifefarmassist.LifeFarmAssist;
import net.azisaba.lifefarmassist.config.AutoPlantArmorConfig;
import net.azisaba.lifefarmassist.region.BlockPos;
import net.azisaba.lifefarmassist.region.CuboidRegion;
import net.azisaba.lifefarmassist.region.Region;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AutoPlantArmorListener extends BaseArmorListener<AutoPlantArmorConfig> {
    public AutoPlantArmorListener(@NotNull LifeFarmAssist plugin) {
        super(plugin.getFarmAssistConfig().getListOfType(AutoPlantArmorConfig.class));
    }

    @Override
    public void onMove(@NotNull AutoPlantArmorConfig config, @NotNull Player player) {
        BlockPos center = new BlockPos(player.getLocation());
        int modY = 0;
        if (center.getBlock().getType() == Material.FARMLAND) {
            modY = 1;
        }

        BlockPos min = center.subtract(config.getPlantRadius(), config.getVerticalPlantRadius(), config.getPlantRadius());
        BlockPos max = center.add(config.getPlantRadius(), config.getVerticalPlantRadius(), config.getPlantRadius());
        Region region = new CuboidRegion(min, max);

        for (BlockPos pos : region) {
            if (pos.add(BlockFace.DOWN).getBlock().getType() != Material.FARMLAND) {
                continue;
            }
            Block block = pos.getBlock();
            if (block.getType().isAir() && !AreaBreakListener.RECENTLY_BROKEN.contains(pos)) {
                block.setType(Material.WHEAT);
            }
            if (config.getGrowRadius() <= 0) {
                continue;
            }
            BlockData blockData = block.getBlockData();
            if (!(blockData instanceof Ageable)) {
                continue;
            }
            Ageable ageable = (Ageable) blockData;
            if (center.distance(pos) <= config.getGrowRadius()) {
                ageable.setAge(ageable.getMaximumAge());
            }
            block.setBlockData(ageable);
        }
    }
}
