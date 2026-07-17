package xyz.cozy.cozyclearlag;

import org.bukkit.plugin.java.JavaPlugin;

import xyz.cozy.cozyclearlag.commands.MainCommand;
import xyz.cozy.cozyclearlag.managers.AbyssManager;
import xyz.cozy.cozyclearlag.managers.ConfigManager;
import xyz.cozy.cozyclearlag.managers.LangManager;
import xyz.cozy.cozyclearlag.tasks.AutoClearTask;
import xyz.cozy.cozyclearlag.tasks.ClearTask;

public class CozyClearLag extends JavaPlugin {

    private ConfigManager configManager;
    private LangManager langManager;
    private AbyssManager abyssManager;
    private ClearTask clearTask;
    private AutoClearTask autoClearTask;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.configManager = new ConfigManager(this);
        this.langManager = new LangManager(this);
        this.abyssManager = new AbyssManager(this);
        this.clearTask = new ClearTask(this);

        MainCommand mainCommand = new MainCommand(this);
        getCommand("cozyclearlag").setExecutor(mainCommand);
        getCommand("cozyclearlag").setTabCompleter(mainCommand);

        startAutoClearTask();

        getLogger().info("CozyClearLag đã được kích hoạt! (Chỉ dọn item + đạn bay, không đụng tới mob)");
    }

    @Override
    public void onDisable() {
        if (autoClearTask != null) {
            autoClearTask.cancel();
        }
        getLogger().info("CozyClearLag đã tắt.");
    }

    private void startAutoClearTask() {
        if (autoClearTask != null) {
            autoClearTask.cancel();
        }
        if (configManager.getAutoClearInterval() > 0) {
            autoClearTask = new AutoClearTask(this);
            autoClearTask.runTaskTimer(this, 20L, 20L);
        }
    }

    public void reloadAll() {
        configManager.reload();
        langManager.reload();
        startAutoClearTask();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LangManager getLangManager() {
        return langManager;
    }

    public AbyssManager getAbyssManager() {
        return abyssManager;
    }

    public ClearTask getClearTask() {
        return clearTask;
    }
          }
