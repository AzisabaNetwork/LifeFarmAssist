package net.azisaba.lifefarmassist.listener;

import net.azisaba.lifefarmassist.LifeFarmAssist;
import net.azisaba.lifefarmassist.config.AreaBreakArmorConfig;
import net.azisaba.lifefarmassist.region.BlockPos;
import net.azisaba.lifefarmassist.region.CuboidRegion;
import net.azisaba.lifefarmassist.region.Region;
import net.azisaba.lifefarmassist.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.storageBox.utils.StorageBox;
import xyz.acrylicstyle.storageBox.utils.StorageBoxUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AreaBreakListener implements Listener {
    public static final Set<BlockPos> RECENTLY_BROKEN = new HashSet<>();
    private final List<AreaBreakArmorConfig> configList;
    private final LifeFarmAssist plugin;
    private final AtomicBoolean processing = new AtomicBoolean();

    public AreaBreakListener(LifeFarmAssist plugin) {
        this.configList = plugin.getFarmAssistConfig().getListOfType(AreaBreakArmorConfig.class);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        if (processing.get()) return; // prevent loop
        if (!LifeFarmAssist.getInstance().getFarmAssistConfig().isAllowedWorld(e.getPlayer().getWorld().getName())) {
            return;
        }
        BlockPos center = new BlockPos(e.getBlock().getLocation());
        if (RECENTLY_BROKEN.contains(center)) {
            // prevent loop
            return;
        }
        int delay = 0;
        for (AreaBreakArmorConfig config : configList) {
            if (!config.isEnabled() || config.getRadius() == 0 || delay > 0) continue;
            if (!PlayerUtil.wearingMythicItem(e.getPlayer(), config.getMythicType())) {
                continue;
            }

            Region region;
            if (config.isVerticalDestruction()) {
                region = CuboidRegion.radius(center, config.getRadius());
            } else {
                region = CuboidRegion.radiusIgnoreY(center, config.getRadius());
            }

            addRecentlyBroken(center, config.getPreventAutoPlantTicks());
            for (BlockPos pos : region) {
                Block block = pos.getBlock();
                if (pos.equals(center) || !(block.getBlockData() instanceof Ageable)) {
                    continue;
                }
                Ageable ageable = (Ageable) block.getBlockData();
                if (ageable.getMaximumAge() != ageable.getAge()) {
                    continue;
                }
                RECENTLY_BROKEN.add(pos);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    RECENTLY_BROKEN.add(pos);
                    BlockBreakEvent event = new BlockBreakEvent(block, e.getPlayer());
                    processing.set(true);
                    Bukkit.getPluginManager().callEvent(event);
                    processing.set(false);
                    if (event.isCancelled()) {
                        RECENTLY_BROKEN.remove(pos);
                    } else {
                        if (plugin.getFarmAssistConfig().isDropsAddToInventory()) {
                            Collection<ItemStack> drops = block.getDrops(e.getPlayer().getInventory().getItemInMainHand());
                            block.setType(Material.AIR);
                            if (plugin.getFarmAssistConfig().isStorageBoxEnabled()) {
                                addToStorageOrInventory(e.getPlayer(), drops);
                            } else {
                                e.getPlayer().getInventory().addItem(drops.toArray(new ItemStack[0]));
                            }
                        } else {
                            block.breakNaturally(e.getPlayer().getInventory().getItemInMainHand());
                        }
                        addRecentlyBroken(pos, config.getPreventAutoPlantTicks());
                    }
                }, (int) (++delay / 2.0));
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

    private void addToStorageOrInventory(Player player, Collection<ItemStack> drops) {
        PlayerInventory inventory = player.getInventory();
        List<ItemStack> remainingDrops = new ArrayList<>();

        for (ItemStack drop : drops) {
            if (drop == null || drop.getType().isAir()) continue;

            long amountToStore = drop.getAmount();

            Map.Entry<Integer, StorageBox> entry = StorageBoxUtils.getStorageBoxForType(inventory, drop);
            if (entry != null) {
                int slot = entry.getKey();
                StorageBox box = entry.getValue();

                box.setAmount(box.getAmount() + amountToStore);
                inventory.setItem(slot, box.getItemStack());
                amountToStore = 0;
            }

            if (amountToStore > 0) {
                for (int i = 0; i < inventory.getSize(); i++) {
                    if (amountToStore <= 0) break;
                    ItemStack boxStack = inventory.getItem(i);
                    if (boxStack == null) continue;

                    StorageBox box = StorageBox.getStorageBox(boxStack);
                    if (box != null && box.isAutoCollect() && box.isEmpty()) {
                        box.importComponent(drop);
                        inventory.setItem(i, box.getItemStack());
                        amountToStore = 0;
                        break;
                    }
                }
            }

            if (amountToStore > 0) {
                ItemStack remaining = drop.clone();
                remaining.setAmount((int) amountToStore);
                remainingDrops.add(remaining);
            }
        }

        if (!remainingDrops.isEmpty()) {
            inventory.addItem(remainingDrops.toArray(new ItemStack[0]));
        }
    }
}