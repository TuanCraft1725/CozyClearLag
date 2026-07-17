package xyz.cozy.cozyclearlag.managers;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import xyz.cozy.cozyclearlag.CozyClearLag;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class ConfigManager {

    private final CozyClearLag plugin;
    private FileConfiguration cfg;

    public ConfigManager(CozyClearLag plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        this.cfg = plugin.getConfig();
    }

    public String getPrefix() {
        return cfg.getString("prefix", "&8[&b&lCozyClearLag&8] ");
    }

    public List<String> getWorlds() {
        return cfg.getStringList("worlds");
    }

    public boolean isWorldEnabled(String worldName) {
        List<String> worlds = getWorlds();
        return worlds.contains("*") || worlds.contains(worldName);
    }

    public int getAutoClearInterval() {
        return cfg.getInt("auto_clear_interval", 1200);
    }

    public boolean isAlertsEnabled() {
        return cfg.getBoolean("alerts.enabled", true);
    }

    public boolean isAlertMessage() {
        return cfg.getBoolean("alerts.message", true);
    }

    public boolean isAlertActionbar() {
        return cfg.getBoolean("alerts.actionbar", false);
    }

    public List<Integer> getCountdownSeconds() {
        return cfg.getIntegerList("alerts.countdown_seconds");
    }

    // ---------------- Items ----------------

    public boolean isItemsEnabled() {
        return cfg.getBoolean("items.enabled", true);
    }

    public boolean isItemTimeLivedEnabled() {
        return cfg.getBoolean("items.time_lived.enabled", true);
    }

    public long getItemTimeLivedValue() {
        return cfg.getLong("items.time_lived.value", 10000L);
    }

    public Set<Material> getItemBlacklist() {
        return parseMaterials(cfg.getStringList("items.blacklist"));
    }

    public boolean isAbyssEnabled() {
        return cfg.getBoolean("items.abyss.enabled", true);
    }

    public int getAbyssCloseAfter() {
        return cfg.getInt("items.abyss.close_after", 60);
    }

    public Set<Material> getAbyssBlacklist() {
        return parseMaterials(cfg.getStringList("items.abyss.blacklist"));
    }

    // ---------------- Projectiles ----------------

    public boolean isProjectilesEnabled() {
        return cfg.getBoolean("projectiles.enabled", true);
    }

    public boolean isProjectileListMode() {
        return cfg.getBoolean("projectiles.list_mode", true);
    }

    public Set<EntityType> getProjectileList() {
        Set<EntityType> set = EnumSet.noneOf(EntityType.class);
        for (String raw : cfg.getStringList("projectiles.list")) {
            try {
                set.add(EntityType.valueOf(raw.trim().toUpperCase()));
            } catch (IllegalArgumentException ex) {
                plugin.getLogger().warning("Loại đạn bay không hợp lệ trong config: " + raw);
            }
        }
        return set;
    }

    public boolean isDebug() {
        return cfg.getBoolean("debug", false);
    }

    private Set<Material> parseMaterials(List<String> raw) {
        Set<Material> set = EnumSet.noneOf(Material.class);
        for (String name : raw) {
            Material mat = Material.matchMaterial(name.trim());
            if (mat != null) {
                set.add(mat);
            } else {
                plugin.getLogger().warning("Vật liệu không hợp lệ trong config: " + name);
            }
        }
        return set;
    }

    public static List<String> copy(List<String> list) {
        return new ArrayList<>(list);
    }
          }
