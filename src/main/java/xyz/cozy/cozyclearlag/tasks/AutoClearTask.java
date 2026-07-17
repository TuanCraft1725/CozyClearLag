package xyz.cozy.cozyclearlag.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import xyz.cozy.cozyclearlag.CozyClearLag;
import xyz.cozy.cozyclearlag.managers.ConfigManager;
import xyz.cozy.cozyclearlag.managers.LangManager;

public class AutoClearTask extends BukkitRunnable {

    private final CozyClearLag plugin;
    private int secondsRemaining;

    public AutoClearTask(CozyClearLag plugin) {
        this.plugin = plugin;
        ConfigManager cfg = plugin.getConfigManager();
        this.secondsRemaining = Math.max(cfg.getAutoClearInterval(), 1);
    }

    @Override
    public void run() {
        ConfigManager cfg = plugin.getConfigManager();
        LangManager lang = plugin.getLangManager();
        ClearTask clearTask = plugin.getClearTask();

        if (cfg.isAlertsEnabled() && cfg.getCountdownSeconds().contains(secondsRemaining)) {
            String msg = lang.get("messages.countdown.warning", "seconds", secondsRemaining);
            if (cfg.isAlertMessage()) {
                clearTask.alertPlayers(msg, false);
            }
            if (cfg.isAlertActionbar()) {
                clearTask.alertPlayers(msg, true);
            }
        }

        secondsRemaining--;

        if (secondsRemaining <= 0) {
            int items = clearTask.clearItems();
            int projectiles = clearTask.clearProjectiles();

            if (cfg.isAlertsEnabled() && cfg.isAlertMessage()) {
                String done = lang.get("messages.clear.all_done", "items", items, "projectiles", projectiles);
                clearTask.alertPlayers(done, false);
            }

            secondsRemaining = Math.max(cfg.getAutoClearInterval(), 1);
        }
    }
}
