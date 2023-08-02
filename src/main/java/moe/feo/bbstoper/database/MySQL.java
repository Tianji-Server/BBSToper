package moe.feo.bbstoper.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import moe.feo.bbstoper.BBSToper;
import moe.feo.bbstoper.config.Message;
import moe.feo.bbstoper.config.Option;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL extends AbstractSQLConnection {

	private final static MySQL sqler = new MySQL();
	private HikariDataSource ds;

	public static MySQL getInstance() {
		return sqler;
	}

	@Override
	protected Connection getConnection() {
		try {
			return ds.getConnection();
		} catch (SQLException e) {
			BBSToper.INSTANCE.getLogger().severe(Message.FAILEDCONNECTSQL.getString());
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void closeConnection() {
		ds.close();
	}

	@Override
	protected void load() {
		connect();
		createTablePosters();
		createTableTopStates();
	}

	protected void connect() {
		String driver = null;
		try{
			Class.forName("com.mysql.cj.jdbc.Driver");
			driver = "com.mysql.cj.jdbc.Driver";
		} catch (ClassNotFoundException ignored) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				driver = "com.mysql.jdbc.Driver";
			} catch (ClassNotFoundException ignored1) {}
		}

		HikariConfig config = new HikariConfig();
		config.setDriverClassName(driver);
		config.setJdbcUrl("jdbc:mysql://" + Option.DATABASE_MYSQL_ADDRESS + "/" + Option.DATABASE_MYSQL_DATABASE + Option.DATABASE_MYSQL_PARAMETERS);
		config.setUsername(Option.DATABASE_MYSQL_USERNAME.getString());
		config.setPassword(Option.DATABASE_MYSQL_PASSWORD.getString());
		config.setConnectionTimeout(Option.DATABASE_POOL_CONNECTIONTIMEOUT.getLong());
		config.setIdleTimeout(Option.DATABASE_POOL_IDLETIMEOUT.getLong());
		config.setMaxLifetime(Option.DATABASE_POOL_MAXLIFETIME.getLong());
		config.setMaximumPoolSize(Option.DATABASE_POOL_MAXIUMPOOLSIZE.getInt());
		config.setKeepaliveTime(Option.DATABASE_POOL_KEEPALIVETIME.getLong());
		config.setMinimumIdle(Option.DATABASE_POOL_MINIMUMIDLE.getInt());
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

		ds = new HikariDataSource(config);
	}

	protected void createTablePosters() {
		String sql = String.format(
				"CREATE TABLE IF NOT EXISTS `%s` ( `uuid` char(36) NOT NULL, `name` varchar(255) NOT NULL, `bbsname` varchar(255) NOT NULL, `binddate` bigint(0) NOT NULL, `rewardbefore` char(10) NOT NULL, `rewardtimes` int(0) NOT NULL, PRIMARY KEY (`uuid`) ) CHARACTER SET utf8 COLLATE utf8_unicode_ci;",
				getTableName("posters"));
		try (Statement stmt = getConnection().createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void createTableTopStates() {
		String sql = String.format(
				"CREATE TABLE IF NOT EXISTS `%s` ( `id` int(0) NOT NULL AUTO_INCREMENT, `bbsname` varchar(255) NOT NULL, `time` varchar(16) NOT NULL, PRIMARY KEY (`id`) ) CHARACTER SET utf8 COLLATE utf8_unicode_ci;",
				getTableName("topstates"));
		try (Statement stmt = getConnection().createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
