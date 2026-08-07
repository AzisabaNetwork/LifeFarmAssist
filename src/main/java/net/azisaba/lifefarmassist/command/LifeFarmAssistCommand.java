package net.azisaba.lifefarmassist.command;

import net.azisaba.lifefarmassist.LifeFarmAssist;
import net.azisaba.lifefarmassist.config.DropBoostArmorConfig;
import net.azisaba.lifefarmassist.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class LifeFarmAssistCommand implements CommandExecutor, TabCompleter {
    private final LifeFarmAssist plugin;

    public LifeFarmAssistCommand(@NotNull LifeFarmAssist plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "このコマンドはプレイヤーのみ実行できます。");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            Material material = Material.getMaterial(args[1].toUpperCase(Locale.ROOT));
            if (material == null || material.isAir() || !material.isItem()) {
                player.sendMessage(ChatColor.RED + "有効なアイテムのMaterialを指定してください。");
                return true;
            }

            ItemStack item = ItemUtil.createDropBoostItem(material, args[2]);
            if (item == null) {
                player.sendMessage(ChatColor.RED + "設定に存在するdrop-boostのmythic-typeを指定してください。");
                return true;
            }
            player.getInventory().addItem(item).values().forEach(leftover ->
                    player.getWorld().dropItemNaturally(player.getLocation(), leftover));
            player.sendMessage(ChatColor.GREEN + "ドロップ増加アイテムを付与しました。");
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("edit-drop-boost")) {
            int breakCount;
            try {
                breakCount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "破壊数は0以上の整数で指定してください。");
                return true;
            }
            if (breakCount < 0) {
                player.sendMessage(ChatColor.RED + "破壊数は0以上の整数で指定してください。");
                return true;
            }

            ItemStack edited = ItemUtil.setBreakCount(player.getInventory().getItemInMainHand(), breakCount);
            if (edited == null) {
                player.sendMessage(ChatColor.RED + "メインハンドに設定済みのドロップ増加アイテムを持ってください。");
                return true;
            }
            player.getInventory().setItemInMainHand(edited);
            player.sendMessage(ChatColor.GREEN + "破壊数を " + ItemUtil.getBreakCount(edited) + " に設定しました。");
            return true;
        }

        sendUsage(player, label);
        return true;
    }

    private void sendUsage(@NotNull CommandSender sender, @NotNull String label) {
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " give <material> <mythic-type>");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " edit-drop-boost <break_count>");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return filter(Arrays.asList("give", "edit-drop-boost"), args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            List<String> materials = new ArrayList<>();
            for (Material material : Material.values()) {
                if (!material.isAir() && material.isItem()) {
                    materials.add(material.name().toLowerCase(Locale.ROOT));
                }
            }
            return filter(materials, args[1]);
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            List<String> mythicTypes = new ArrayList<>();
            for (DropBoostArmorConfig config : plugin.getFarmAssistConfig().getListOfType(DropBoostArmorConfig.class)) {
                mythicTypes.add(config.getMythicType());
            }
            return filter(mythicTypes, args[2]);
        }
        return Collections.emptyList();
    }

    private List<String> filter(@NotNull List<String> values, @NotNull String input) {
        String lowerInput = input.toLowerCase(Locale.ROOT);
        List<String> matches = new ArrayList<>();
        for (String value : values) {
            if (value.toLowerCase(Locale.ROOT).startsWith(lowerInput)) {
                matches.add(value);
            }
        }
        return matches;
    }
}
