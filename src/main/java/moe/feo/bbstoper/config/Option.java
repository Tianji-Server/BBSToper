package moe.feo.bbstoper.config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;

import com.google.common.base.Charsets;
import moe.feo.bbstoper.BBSToper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Option {
	DEBUG("debug"),
	DATABASE_TYPE("database.type"),
	DATABASE_PREFIX("database.prefix"),
	DATABASE_TIMINGRECONNECT("timingreconnect"),
	DATABASE_MYSQL_IP("database.mysql.ip"),
	DATABASE_MYSQL_PORT("database.mysql.port"),
	DATABASE_MYSQL_DATABASE("database.mysql.database"),
	DATABASE_MYSQL_USER("database.mysql.user"),
	DATABASE_MYSQL_PASSWORD("database.mysql.password"),
	DATABASE_MYSQL_SSL("database.mysql.ssl"),
	DATABASE_SQLITE_FOLDER("database.sqlite.folder"),
	DATABASE_SQLITE_DATABASE("database.sqlite.database"),
	MCBBS_LINK("mcbbs.link"),
	MCBBS_URL("mcbbs.url"), MCBBS_PAGESIZE("mcbbs.pagesize"),
	MCBBS_CHANGEIDCOOLDOWN("mcbbs.changeidcooldown"),
	MCBBS_QUERYCOOLDOWN("mcbbs.querycooldown"),
	MCBBS_JOINMESSAGE("mcbbs.joinmessage"),
	PROXY_ENABLE("proxy.enable"),
	PROXY_IP("proxy.ip"),
	PROXY_PORT("proxy.port"),
	GUI_TOPPLAYERS("gui.topplayers"),
	GUI_DISPLAYHEADSKIN("gui.displayheadskin"),
	GUI_USECHATGETID("gui.usechatgetid"),
	GUI_CANCELKEYWORDS("gui.cancelkeywords"),
	REWARD_AUTO("reward.auto"),
	REWARD_PERIOD("reward.period"),
	REWARD_INTERVAL("reward.interval"),
	REWARD_TIMES("reward.times"),
	REWARD_COMMANDS("reward.commands"),
	REWARD_INCENTIVEREWARD_ENABLE("reward.incentivereward.enable"),
	REWARD_INCENTIVEREWARD_EXTRA("reward.incentivereward.extra"),
	REWARD_INCENTIVEREWARD_PERIOD("reward.incentivereward.period"),
	REWARD_INCENTIVEREWARD_COMMANDS("reward.incentivereward.commands"),
	REWARD_OFFDAYREWARD_ENABLE("reward.offdayreward.enable"),
	REWARD_OFFDAYREWARD_EXTRA("reward.offdayreward.extra"),
	REWARD_OFFDAYREWARD_OFFDAYS("reward.offdayreward.offdays"),
	REWARD_OFFDAYREWARD_COMMANDS("reward.offdayreward.commands");

	private final String path;

	Option(String path) {
		this.path = path;
	}

	public static void load() {
		BBSToper.INSTANCE.reloadConfig();
		InputStream defConfigStream = BBSToper.INSTANCE.getResource("config.yml");
		if (defConfigStream != null) {
			BBSToper.INSTANCE.getConfig().setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
		}
	}

	public String getString() {
		return BBSToper.INSTANCE.getConfig().getString(path);
	}

	public List<String> getStringList() {
		return BBSToper.INSTANCE.getConfig().getStringList(path);
	}

	public boolean getBoolean() {
		return BBSToper.INSTANCE.getConfig().getBoolean(path);
	}

	public int getInt() {
		return BBSToper.INSTANCE.getConfig().getInt(path);
	}
}
