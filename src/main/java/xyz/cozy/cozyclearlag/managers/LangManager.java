package xyz.cozy.cozyclearlag.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import xyz.cozy.cozyclearlag.CozyClearLag;
import xyz.cozy.cozyclearlag.utils.MessageUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class LangManager {

    private final CozyClearLag plugin;
    private FileConfiguration lang;
    private File file;

    public LangManager(CozyClearLag plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        file = new File(plugin.getDataFolder(), "lang.yml");
        if (!file.exists()) {
            plugin.saveResource("lang.yml", false);
        }
        lang = YamlConfiguration.loadConfiguration(file);

        try (InputStream defStream = plugin.getResource("lang.yml")) {
            if (defStream != null) {
                YamlConfiguration defaults = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(defStream, StandardCharsets.UTF_8));
                lang.setDefaults(defaults);
            }
        } catch (IOException ignored) {
        }
    }

    public String getPrefixed(ConfigManager cfgManager, String path, Object... placeholders) {
        String raw = lang.getString(path, path);
        return MessageUtil.format(cfgManager.getPrefix() + raw, placeholders);
    }

    public String get(String path, Object... placeholders) {
        String raw = lang.getString(path, path);
        return MessageUtil.format(raw, placeholders);
    }

    public List<String> getList(String path) {
        return lang.getStringList(path);
    }
}
