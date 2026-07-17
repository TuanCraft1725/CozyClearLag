package xyz.cozy.cozyclearlag.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import xyz.cozy.cozyclearlag.CozyClearLag;
import xyz.cozy.cozyclearlag.utils.MessageUtil;

import java.util.ArrayList;
import java.util.List;

public class AbyssManager {

    private final CozyClearLag plugin;
    private Inventory storage;
    private BukkitTask closeTask;
    private long closesAt = -1;

    public AbyssManager(CozyClearLag plugin) {
        this.plugin = plugin;
    }

    public void store(ItemStack item) {
        ConfigManager cfg = plugin.getConfigManager();
        if (!cfg.isAbyssEnabled()) {
            return;
        }
        Material type = item.getType();
        if (cfg.getAbyssBlacklist().contains(type)) {
            return;
        }
        if (storage == null) {
            open();
        }
        storage.addItem(item.clone());
        resetCloseTimer();
    }

    private void open() {
        String title = MessageUtil.color("Vực Thẳm - Item đã xóa");
        storage = Bukkit.createInventory(null, 54, title);
    }

    private void resetCloseTimer() {
        ConfigManager cfg = plugin.getConfigManager();
        int seconds = cfg.getAbyssCloseAfter();
        closesAt = System.currentTimeMillis() + (seconds * 1000L);

        if (closeTask != null) {
            closeTask.cancel();
        }
        closeTask = Bukkit.getScheduler().runTaskLater(plugin, this::close, seconds * 20L);
    }

    private void close() {
        storage = null;
        closesAt = -1;
        closeTask = null;
    }

    public boolean isOpen() {
        return storage != null;
    }

    public int getSecondsUntilClose() {
        if (closesAt < 0) {
            return 0;
        }
        long diff = closesAt - System.currentTimeMillis();
        return (int) Math.max(0, diff / 1000L);
    }

    public boolean openFor(Player player) {
        if (storage == null) {
            return false;
        }
        player.openInventory(storage);
        return true;
    }

    public List<ItemStack> getContents() {
        List<ItemStack> list = new ArrayList<>();
        if (storage != null) {
            for (ItemStack item : storage.getContents()) {
                if (item != null) {
                    list.add(item);
                }
            }
        }
        return list;
    }
          }
