package moe.feo.bbstoper;

import moe.feo.bbstoper.config.Message;
import moe.feo.bbstoper.config.Option;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import moe.feo.bbstoper.listener.InventoryListener;
import moe.feo.bbstoper.database.DatabaseManager;

public class BBSToper extends JavaPlugin {
	public static BBSToper INSTANCE;
	public static boolean legacyApi = false;

	@Override
	public void onLoad() {
		INSTANCE = this;
		getLogger().info("Loading configuration.");
		saveDefaultConfig();
		saveResource("lang.yml", false);
		Option.load();
		Message.load();

		if(Integer.parseInt(getServer().getVersion().split("\\.")[1]) < 13){
			legacyApi = true;
			getLogger().info("Server version is 1.12 and below, will using legacy material api.");
		}
	}

	@Override
	public void onEnable() {

		DatabaseManager.initializeDatabase();
		getCommand("bbstoper").setExecutor(CLI.getInstance());
		getCommand("bbstoper").setTabCompleter(CLI.getInstance());

		new Reminder(this);
		new InventoryListener(this);

		DatabaseManager.startTimingReconnect();
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
			DatabaseManager.closeSQLer();
			INSTANCE = null;
		});
		thread.setDaemon(true);
		thread.start();
	}

}
