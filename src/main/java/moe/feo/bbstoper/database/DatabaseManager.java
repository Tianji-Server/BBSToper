package moe.feo.bbstoper.database;

import moe.feo.bbstoper.BBSToper;
import moe.feo.bbstoper.Util;
import moe.feo.bbstoper.config.Option;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class DatabaseManager {
	public static AbstractSQLConnection connection;
	private static BukkitTask timingReconnectTask;

	public static void initializeDatabase() {// 初始化或重载数据库
		try {
			AbstractSQLConnection.writelock.lock();
			if (connection != null) {
				connection.closeConnection();// 此方法会在已经建立过连接的情况下关闭连接
			}
			if (Option.DATABASE_TYPE.getString().equalsIgnoreCase("mysql")) {
				connection = new MySQL();
			} else if (Option.DATABASE_TYPE.getString().equalsIgnoreCase("sqlite")) {
				connection = new SQLite();
			}
			connection.load();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			AbstractSQLConnection.writelock.unlock();
		}
	}

	public static void closeSQLer() {// 关闭数据库
		connection.closeConnection();
		connection = null;
	}

	public static void startTimingReconnect() {// 自动重连数据库的方法
		if (timingReconnectTask != null && !timingReconnectTask.isCancelled()) {// 将之前的任务取消(如果存在)
			timingReconnectTask.cancel();
		}
		int period = Option.DATABASE_TIMINGRECONNECT.getInt() * 20;
		if (period > 0) {
			timingReconnectTask = new BukkitRunnable() {
				@Override
				public void run() {
					Util.addRunningTaskID(this.getTaskId());
					initializeDatabase();// 重载数据库
					Util.removeRunningTaskID(this.getTaskId());
				}
			}.runTaskTimerAsynchronously(BBSToper.INSTANCE, period, period);
		}
	}
}
