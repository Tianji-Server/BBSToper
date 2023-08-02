package moe.feo.bbstoper.config;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Charsets;
import moe.feo.bbstoper.BBSToper;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Message {
	PREFIX("prefix"),
	ENABLE("enable"),
	RELOAD("reload"),
	FAILEDCONNECTSQL("failedconnectsql"),
	QUERYCOOLDOWN("querycooldown"),
	POSTERID("posterid"),
	POSTERNUM("posternum"),
	OVERPAGE("overpage"),
	NOPLAYER("noplayer"),
	POSTERTIME("postertime"),
	PAGEINFO("pageinfo"),
	NOPOSTER("noposter"),
	POSTERPLAYER("posterplayer"),
	POSTERTOTAL("postertotal"),
	PAGEINFOTOP("pageinfotop"),
	NOTBOUND("notbound"),
	NOPOST("nopost"),
	OVERTIME("overtime"),
	WAITAMIN("waitamin"),
	INTERVALTOOSHORT("intervaltooshort"),
	REWARD("reward"),
	EXTRAREWARD("extrareward"),
	REWARDGIVED("rewardgived"),
	BROADCAST("broadcast"),
	ENTER("enter"),
	CANCELED("canceled"),
	REPEAT("repeat"),
	NOTSAME("notsame"),
	ONCOOLDOWN("oncooldown"),
	SAMEBIND("samebind"),
	OWNSAMEBIND("ownsamebind"),
	BINDINGSUCCESS("bindingsuccess"),
	IDOWNER("idowner"),
	IDNOTFOUND("idnotfound"),
	OWNERID("ownerid"),
	OWNERNOTFOUND("ownernotfound"),
	NOPERMISSION("nopermission"),
	INVALID("invalid"),
	INVALIDNUM("invalidnum"),
	PLAYERCMD("playercmd"),
	PAGENOTVISIBLE("pagenotvisible"),
	NONE("none"),
	FAILEDGETWEB("failedgetweb"),
	FAILEDRESOLVEWEB("failedresolveweb"),
	FAILEDUNINSTALLMO("faileduninstallmo"),
	GUI_TITLE("gui.title"),
	GUI_FRAME("gui.frame"),
	GUI_SKULL("gui.skull"),
	GUI_NOTBOUND("gui.notbound"),
	GUI_CLICKBOUND("gui.clickbound"),
	GUI_CLICKREBOUND("gui.clickrebound"),
	GUI_BBSID("gui.bbsid"),
	GUI_POSTTIMES("gui.posttimes"),
	GUI_REWARDS("gui.rewards"),
	GUI_INCENTIVEREWARDS("gui.incentiverewards"),
	GUI_OFFDAYREWARDS("gui.offdayrewards"),
	GUI_CLICKGET("gui.clickget"),
	GUI_TOPS("gui.tops"),
	GUI_PAGESTATE("gui.pagestate"),
	GUI_PAGEID("gui.pageid"),
	GUI_LASTPOST("gui.lastpost"),
	GUI_EXTRAREWARDS("gui.extrarewards"),
	GUI_PAGENOTVISIBLE("gui.pagenotvisible"),
	GUI_CLICKOPEN("gui.clickopen"),
	GUI_REWARDSINFO("gui.rewardsinfo"),
	CLICKPOSTICON("clickposticon"),
	DELETESUCCESS("deletesuccess"),
	INFO("info"),
	EXTRAINFO("extrainfo"),
	HELP_TITLE("help.title"),
	HELP_HELP("help.help"),
	HELP_BINDING("help.binding"),
	HELP_REWARD("help.reward"),
	HELP_TESTREWARD("help.testreward"),
	HELP_LIST("help.list"),
	HELP_TOP("help.top"),
	HELP_CHECK("help.check"),
	HELP_DELETE("help.delete"),
	HELP_RELOAD("help.reload");

	public final String path;

	private static FileConfiguration messageConfig;

	Message(String path) {
		this.path = path;
	}

	public static void load() {// 加载与重载
		messageConfig = YamlConfiguration.loadConfiguration(new File(BBSToper.INSTANCE.getDataFolder(), "lang.yml"));// 加载配置
		InputStream defConfigStream = BBSToper.INSTANCE.getResource("lang.yml");
		if (defConfigStream != null) {
			BBSToper.INSTANCE.getConfig().setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
		}
	}

	public String getString() {
		return ChatColor.translateAlternateColorCodes('&', messageConfig.getString(path));
	}

	public List<String> getStringList() {
		return Collections.unmodifiableList(// 禁止修改
				messageConfig.getStringList(path).stream().map(msg -> ChatColor.translateAlternateColorCodes('&', msg))
						.collect(Collectors.toList()));
	}

}
