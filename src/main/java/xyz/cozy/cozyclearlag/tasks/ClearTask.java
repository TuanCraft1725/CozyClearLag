package xyz.cozy.cozyclearlag.tasks;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import xyz.cozy.cozyclearlag.CozyClearLag;
import xyz.cozy.cozyclearlag.managers.ConfigManager;

import java.util.ArrayList;
import java.util.List;

public class ClearTask {

    private final CozyClearLag plugin;

    public ClearTask(CozyClearLag plugin) {
        this.plugin = plugin;
    }

    public int clearItems() {
        ConfigManager cfg = plugin.getConfigManager();
        if (!cfg.isItemsEnabled()) {
            return 0;
        }

        int removed = 0;

        for (World world : Bukkit.getWorlds()) {
            if (!cfg.isWorldEnabled(world.getName())) {
                continue;
            }
            List<Item> toRemove = new ArrayList<>();
            for (Entity entity : world.getEntitiesByClass(Item.class)) {
                Item item = (Item) entity;

                if (cfg.getItemBlacklist().contains(item.getItemStack().getType())) {
                    continue;
                }

                if (cfg.isItemTimeLivedEnabled()) {
                    long aliveMs = (long) item.getTicksLived() * 50L;
                    if (aliveMs < cfg.getItemTimeLivedValue()) {
                        continue;
                    }
                }

                toRemove.add(item);
            }

            for (Item item : toRemove) {
                plugin.getAbyssManager().store(item.getItemStack());
                item.remove();
                removed++;
            }
        }

        if (cfg.isDebug()) {
            plugin.getLogger().info("[Debug] Đã dọn " + removed + " item rơi.");
        }
        return removed;
    }

    public int clearProjectiles() {
        ConfigManager cfg = plugin.getConfigManager();
        if (!cfg.isProjectilesEnabled()) {
            return 0;
        }

        int removed = 0;
        boolean listMode = cfg.isProjectileListMode();

        for (World world : Bukkit.getWorlds()) {
            if (!cfg.isWorldEnabled(world.getName())) {
                continue;
            }
            List<Projectile> toRemove = new ArrayList<>();
            for (Entity entity : world.getEntitiesByClass(Projectile.class)) {
                EntityType type = entity.getType();
                boolean inList = cfg.getProjectileList().contains(type);

                boolean shouldRemove = listMode == inList;
                if (shouldRemove) {
                    toRemove.add((Projectile) entity);
                }
            }

            for (Projectile p : toRemove) {
                p.remove();
                removed++;
            }
        }

        if (cfg.isDebug()) {
            plugin.getLogger().info("[Debug] Đã dọn " + removed + " đạn bay.");
        }
        return removed;
    }

    public void alertPlayers(String message, boolean actionbar) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("cozyclearlag.alerts.bypass")) {
                continue;
            }
            if (actionbar) {
                player.sendActionBar(message);
            } else {
                player.sendMessage(message);
            }
        }
    }
}
