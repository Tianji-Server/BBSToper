package moe.feo.bbstoper.database;

import moe.feo.bbstoper.BBSToper;
import moe.feo.bbstoper.config.Message;
import moe.feo.bbstoper.config.Option;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class MySQL extends AbstractSQLConnection {

	private final static MySQL sqler = new MySQL();
	private Connection connection;

	public static MySQL getInstance() {
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
		boolean ssl = Option.DATABASE_MYSQL_SSL.getBoolean();
		return "jdbc:mysql://" + Option.DATABASE_MYSQL_IP.getString() + ":"
				+ Option.DATABASE_MYSQL_PORT.getString() + "/" + Option.DATABASE_MYSQL_DATABASE.getString() + "?useSSL="
				+ ssl + "&serverTimezone=UTC" + "&autoReconnect=true" + "&allowPublicKeyRetrieval=true" + "&characterEncoding=utf8";
	}

	@Override
	protected void load() {
		connect();
		createTablePosters();
		createTableTopStates();
	}

	protected void connect() {
		String driver = "com.mysql.jdbc.Driver";
		String user = Option.DATABASE_MYSQL_USER.getString();
		String password = Option.DATABASE_MYSQL_PASSWORD.getString();
		try {
			Class.forName(driver);
			this.connection = DriverManager.getConnection(getUrl(), user, password);
		} catch (ClassNotFoundException | SQLException e) {
			BBSToper.INSTANCE.getLogger().log(Level.WARNING, Message.FAILEDCONNECTSQL.getString(), e);
		}
	}

	protected void createTablePosters() {
		String sql = String.format(
				"CREATE TABLE IF NOT EXISTS `%s` ( `uuid` char(36) NOT NULL, `name` varchar(255) NOT NULL, `bbsname` varchar(255) NOT NULL, `binddate` bigint(0) NOT NULL, `rewardbefore` char(10) NOT NULL, `rewardtimes` int(0) NOT NULL, PRIMARY KEY (`uuid`) ) CHARACTER SET utf8 COLLATE utf8_unicode_ci;",
				getTableName("posters"));
		try (Statement stmt = connection.createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void createTableTopStates() {
		String sql = String.format(
				"CREATE TABLE IF NOT EXISTS `%s` ( `id` int(0) NOT NULL AUTO_INCREMENT, `bbsname` varchar(255) NOT NULL, `time` varchar(16) NOT NULL, PRIMARY KEY (`id`) ) CHARACTER SET utf8 COLLATE utf8_unicode_ci;",
				getTableName("topstates"));
		try (Statement stmt = connection.createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
