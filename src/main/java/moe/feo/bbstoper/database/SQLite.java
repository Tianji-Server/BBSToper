package moe.feo.bbstoper.database;

import moe.feo.bbstoper.BBSToper;
import moe.feo.bbstoper.config.Message;
import moe.feo.bbstoper.config.Option;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class SQLite extends AbstractSQLConnection {

	private final static SQLite sqler = new SQLite();
	private Connection connection;

	public static SQLite getInstance() {
		return sqler;
	}

	@Override
	protected Connection getConnection() {
		return this.connection;
	}

	@Override
	protected void closeConnection() {
		try {
			if (!connection.isClosed()) {// 如果连接没有关闭，则将关闭这个连接
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getUrl() {// 获取数据库url
		String folder = BBSToper.INSTANCE.getDataFolder().getPath();// 获取插件文件夹
		String path = Option.DATABASE_SQLITE_FOLDER.getString().replaceAll("%PLUGIN_FOLDER%", "%s");
		String url = "jdbc:sqlite:" + path + File.separator + Option.DATABASE_SQLITE_DATABASE.getString();
		return String.format(url, folder);
	}

	@Override
	protected void load() {
		connect();
		createTablePosters();
		createTableTopStates();
	}

	protected void connect() {
		String driver = "org.sqlite.JDBC";
		try {
			Class.forName(driver);
			this.connection = DriverManager.getConnection(getUrl());
		} catch (ClassNotFoundException | SQLException e) {
			BBSToper.INSTANCE.getLogger().log(Level.WARNING, Message.FAILEDCONNECTSQL.getString(), e);
		}
	}

	protected void createTablePosters() {
		String sql = String.format(
				"CREATE TABLE IF NOT EXISTS `%s` ( `uuid` char(36) NOT NULL, `name` varchar(255) NOT NULL, `bbsname` varchar(255) NOT NULL COLLATE NOCASE, `binddate` bigint(0) NOT NULL, `rewardbefore` char(10) NOT NULL, `rewardtimes` int(0) NOT NULL, PRIMARY KEY (`uuid`) );",
				getTableName("posters"));
		try (Statement stmt = connection.createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void createTableTopStates() {
		String sql = String.format(
				"CREATE TABLE IF NOT EXISTS `%s` ( `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `bbsname` varchar(255) NOT NULL COLLATE NOCASE, `time` varchar(16) NOT NULL);",
				getTableName("topstates"));
		try (Statement stmt = connection.createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
