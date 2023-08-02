package moe.feo.bbstoper;

import moe.feo.bbstoper.config.Message;
import moe.feo.bbstoper.config.Option;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import moe.feo.bbstoper.gui.GUIManager;
import moe.feo.bbstoper.sql.SQLManager;

public class BBSToper extends JavaPlugin {
	public static BBSToper INSTANCE;

	@Override
	public void onLoad() {
		INSTANCE = this;
		getLogger().info("Loading configuration.");
		saveDefaultConfig();
		saveResource("lang.yml", false);
		Option.load();
		Message.load();
	}

	@Override
	public void onEnable() {

		SQLManager.initializeSQLer();
		getCommand("bbstoper").setExecutor(CLI.getInstance());
		getCommand("bbstoper").setTabCompleter(CLI.getInstance());

		new Reminder(this);
		new GUIManager(this);

		SQLManager.startTimingReconnect();
		Util.startAutoReward();
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new PAPIExpansion().register();
		}

		new Metrics(this);
		this.getLogger().info(Message.ENABLE.getString());
	}

	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(INSTANCE);
		Thread thread = new Thread(() -> {
			Util.waitForAllTask();// 此方法会阻塞
			SQLManager.closeSQLer();
			INSTANCE = null;
		});
		thread.setDaemon(true);
		thread.start();
	}

}
