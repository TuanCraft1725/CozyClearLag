package xyz.cozy.cozyclearlag.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import xyz.cozy.cozyclearlag.CozyClearLag;
import xyz.cozy.cozyclearlag.managers.AbyssManager;
import xyz.cozy.cozyclearlag.managers.ConfigManager;
import xyz.cozy.cozyclearlag.managers.LangManager;
import xyz.cozy.cozyclearlag.tasks.ClearTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainCommand implements CommandExecutor, TabCompleter {

    private final CozyClearLag plugin;

    public MainCommand(CozyClearLag plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        LangManager lang = plugin.getLangManager();
        ConfigManager cfg = plugin.getConfigManager();

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help":
                sendHelp(sender);
                return true;

            case "reload":
                if (!sender.hasPermission("cozyclearlag.admin")) {
                    sender.sendMessage(lang.getPrefixed(cfg, "messages.no_permission"));
                    return true;
                }
                plugin.reloadAll();
                sender.sendMessage(lang.getPrefixed(cfg, "messages.reloaded"));
                return true;

            case "clear":
                return handleClear(sender, args);

            case "abyss":
                return handleAbyss(sender);

            default:
                sender.sendMessage(lang.getPrefixed(cfg, "messages.unknown_command"));
                return true;
        }
    }

    private boolean handleClear(CommandSender sender, String[] args) {
        LangManager lang = plugin.getLangManager();
        ConfigManager cfg = plugin.getConfigManager();
        ClearTask clearTask = plugin.getClearTask();

        if (args.length < 2) {
            sendHelp(sender);
            return true;
        }

        String target = args[1].toLowerCase();

        switch (target) {
            case "items": {
                if (!sender.hasPermission("cozyclearlag.clear.items") && !sender.hasPermission("cozyclearlag.admin")) {
                    sender.sendMessage(lang.getPrefixed(cfg, "messages.no_permission"));
                    return true;
                }
                if (!cfg.isItemsEnabled()) {
                    sender.sendMessage(lang.getPrefixed(cfg, "messages.clear.items_disabled"));
                    return true;
                }
                int removed = clearTask.clearItems();
                sender.sendMessage(lang.getPrefixed(cfg, "messages.clear.items_done", "items", removed));
                return true;
            }
            case "projectiles": {
                if (!sender.hasPermission("cozyclearlag.clear.projectiles") && !sender.hasPermission("cozyclearlag.admin")) {
                    sender.sendMessage(lang.getPrefixed(cfg, "messages.no_permission"));
                    return true;
                }
                if (!cfg.isProjectilesEnabled()) {
                    sender.sendMessage(lang.getPrefixed(cfg, "messages.clear.projectiles_disabled"));
                    return true;
                }
                int removed = clearTask.clearProjectiles();
                sender.sendMessage(lang.getPrefixed(cfg, "messages.clear.projectiles_done", "projectiles", removed));
                return true;
            }
            case "all": {
                if (!sender.hasPermission("cozyclearlag.admin")
                        && !sender.hasPermission("cozyclearlag.clear.items")
                        && !sender.hasPermission("cozyclearlag.clear.projectiles")) {
                    sender.sendMessage(lang.getPrefixed(cfg, "messages.no_permission"));
                    return true;
                }
                int items = cfg.isItemsEnabled() ? clearTask.clearItems() : 0;
                int projectiles = cfg.isProjectilesEnabled() ? clearTask.clearProjectiles() : 0;
                sender.sendMessage(lang.getPrefixed(cfg, "messages.clear.all_done",
                        "items", items, "projectiles", projectiles));
                return true;
            }
            default:
                sendHelp(sender);
                return true;
        }
    }

    private boolean handleAbyss(CommandSender sender) {
        LangManager lang = plugin.getLangManager();
        ConfigManager cfg = plugin.getConfigManager();
        AbyssManager abyss = plugin.getAbyssManager();

        if (!(sender instanceof Player player)) {
            sender.sendMessage(lang.getPrefixed(cfg, "messages.player_only"));
            return true;
        }
        if (!player.hasPermission("cozyclearlag.abyss")) {
            player.sendMessage(lang.getPrefixed(cfg, "messages.no_permission"));
            return true;
        }
        if (!cfg.isAbyssEnabled()) {
            player.sendMessage(lang.getPrefixed(cfg, "messages.abyss.disabled"));
            return true;
        }
        if (!abyss.isOpen()) {
            player.sendMessage(lang.getPrefixed(cfg, "messages.abyss.empty"));
            return true;
        }

        abyss.openFor(player);
        player.sendMessage(lang.getPrefixed(cfg, "messages.abyss.opened"));
        player.sendMessage(lang.getPrefixed(cfg, "messages.abyss.closed_notice",
                "seconds", abyss.getSecondsUntilClose()));
        return true;
    }

    private void sendHelp(CommandSender sender) {
        for (String line : plugin.getLangManager().getList("messages.help")) {
            sender.sendMessage(xyz.cozy.cozyclearlag.utils.MessageUtil.color(line));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filter(Arrays.asList("help", "clear", "abyss", "reload"), args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("clear")) {
            return filter(Arrays.asList("items", "projectiles", "all"), args[1]);
        }
        return new ArrayList<>();
    }

    private List<String> filter(List<String> options, String input) {
        List<String> result = new ArrayList<>();
        for (String opt : options) {
            if (opt.startsWith(input.toLowerCase())) {
                result.add(opt);
            }
        }
        return result;
    }
                               }
