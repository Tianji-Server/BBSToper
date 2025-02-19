package moe.feo.bbstoper.listener;

import moe.feo.bbstoper.BBSToper;
import moe.feo.bbstoper.Crawler;
import moe.feo.bbstoper.Poster;
import moe.feo.bbstoper.Util;
import moe.feo.bbstoper.config.Message;
import moe.feo.bbstoper.config.Config;
import moe.feo.bbstoper.database.DatabaseManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Reminder implements Listener {
	public Reminder(Plugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (!Config.MCBBS_JOINMESSAGE.getBoolean()) {// 如果设置了不提示消息则直接返回
			return;
		}
		new BukkitRunnable() {// 这里由于牵涉到数据库IO, 主线程执行可能会卡顿，所以改成异步
			@Override
			public void run() {
				Util.addRunningTaskID(this.getTaskId());
				task();
				Util.removeRunningTaskID(this.getTaskId());
			}

			public void task() {
				boolean isbinded = true;// 是否绑定
				boolean isposted = true;// 是否有顶贴者
				UUID uuid = event.getPlayer().getUniqueId();
				Poster poster = DatabaseManager.database.getPoster(uuid.toString());
				String datenow = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
				if (poster == null) {// 玩家未绑定
					isbinded = false;
					isposted = false;
				} else if (!datenow.equals(poster.getRewardbefore())) {// 玩家上一次顶贴不是今天
					isposted = false;
				}
				if (!isposted) {// 没有顶贴
					// 提示的信息
					List<String> list = new ArrayList<>(Message.INFO.getStringList());
					Crawler crawler = new Crawler();
					String extra = Util.getExtraReward(crawler);
					if (extra != null) {// 说明有额外奖励信息
						list.add(Message.EXTRAINFO.getString().replaceAll("%EXTRA%", extra));
					}
					String url = Config.MCBBS_LINK.getString() + "thread-" + Config.MCBBS_URL.getString() + "-1-1.html";
					for (String msg : list) {
						event.getPlayer().sendMessage(Message.PREFIX.getString() + msg.replaceAll("%PAGE%", url));
					}
				}
				if (!isbinded) {// 没有绑定
					event.getPlayer().sendMessage(Message.PREFIX.getString() + Message.HELP_BINDING.getString());
				}
			}
		}.runTaskAsynchronously(BBSToper.INSTANCE);
	}
}
