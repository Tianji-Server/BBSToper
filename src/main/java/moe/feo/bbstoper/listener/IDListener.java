/*
 * Module Copyright (c) 2018-2019 Karlatemp. All rights reserved.
 * Reserved.FileName: IDListener.java@author: karlatemp@vip.qq.com: 19-9-17 下午1:39@version: 2.0
 * Only the following methods:
 *	  Module name: com.maddyhome.idea.copyright.pattern.ModuleInfo@646505a3
 *	  Module Methods:
 *		  void execute(Listener, Event);
 *		  void <init>(UUID);
 *		  void callEvent(Event);
 *		  void register();
 *		  void unregister();
 *		  void unregister(UUID);
 *		  void unregister(CommandSender);
 *		  void register();
 */

package moe.feo.bbstoper.listener;

import java.util.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.scheduler.BukkitRunnable;

import moe.feo.bbstoper.BBSToper;
import moe.feo.bbstoper.CLI;
import moe.feo.bbstoper.config.Message;
import moe.feo.bbstoper.config.Config;
import moe.feo.bbstoper.Util;

public class IDListener extends RegisteredListener implements Listener, EventExecutor {
	public static final Object lock = new Object(); // 线程锁
	public static final Map<UUID, IDListener> map = new HashMap<>();

	private final UUID uid;
	private boolean state;

	@Override
	public void execute(Listener listener, Event event) throws EventException {
		callEvent(event);
	}

	@Override
	public void callEvent(Event event) {
		if (event instanceof AsyncPlayerChatEvent) {
			onPlayerChat((AsyncPlayerChatEvent) event);
		}
	}

	public IDListener(UUID uuid) {// 在构造函数中初始化RegisteredListener和UUID
		super(null, null, EventPriority.HIGH, BBSToper.INSTANCE, false);
		this.uid = uuid;
		this.state = false;
	}

	public static void unregister(UUID uniqueId) {
		synchronized (lock) {
			Optional.ofNullable(map.get(uniqueId)).ifPresent(IDListener::unregister);
		}
	}

	public static void unregister(CommandSender sender) {
		if (sender instanceof Player) {
			unregister(((Player) sender).getUniqueId());
		}
	}

	public void unregister() {
		synchronized (lock) {
			AsyncPlayerChatEvent.getHandlerList().unregister((RegisteredListener) this);
			if (!map.remove(uid, this)) {
				BBSToper.INSTANCE.getLogger().warning(Message.FAILEDUNINSTALLMO.getString());
			}
		}
	}

	public void register() {
		for (RegisteredListener lis : AsyncPlayerChatEvent.getHandlerList().getRegisteredListeners()) {
			if (lis == this)
				return; // 如果已经注册就取消注册
		}
		synchronized (lock) {
			IDListener old = map.put(uid, this);
			if (old != null && old != this)
				old.unregister();// 防止遗留
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				Util.addRunningTaskID(this.getTaskId());
				unregister(uid);
				Util.removeRunningTaskID(this.getTaskId());
			}
		}.runTaskLater(BBSToper.INSTANCE, 2 * 60 * 20);// 如果这个监听器还存在，那么将在2分钟后被取消
		AsyncPlayerChatEvent.getHandlerList().register(this);
	}

	public UUID getUid() {// 获取玩家名
		return this.uid;
	}

	public void onPlayerChat(AsyncPlayerChatEvent event) {// 处理事件
		if (!event.getPlayer().getUniqueId().equals(uid))
			return;
		Player player = event.getPlayer();
		String msg = event.getMessage();
		event.setCancelled(true);
		List<String> cancelkeywords = Config.GUI_CANCELKEYWORDS.getStringList();// 取消绑定关键词
		if (cancelkeywords.contains(msg)) {// 如果关键词中包含这次输入的消息
			unregister();// 取消监听事件
			CLI.getInstance().getCache().put(player.getUniqueId().toString(), null);// 清理这个键
			player.sendMessage(Message.PREFIX.getString() + Message.CANCELED.getString());
			return;
		}
		List<String> list = new ArrayList<>(Arrays.asList(msg.split("\\s+")));
		list.add(0, "binding");
		String[] args = list.toArray(new String[0]);
		CLI.getInstance().onCommand(player, null, null, args);
		if (state) {// state为true说明这是第二次进入这个方法
			unregister();
		} else {// state为false说明是第一次进入
			state = true;
		}
	}

}
