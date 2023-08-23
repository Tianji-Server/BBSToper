package moe.feo.bbstoper.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.dreamvoid.bbstoper.Utils;
import moe.feo.bbstoper.BBSToper;
import moe.feo.bbstoper.config.Message;
import moe.feo.bbstoper.config.Option;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite extends AbstractSQLConnection {
	private HikariDataSource ds;

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
	protected void load() throws ClassNotFoundException {
		connect();
		createTablePosters();
		createTableTopStates();
	}

	protected void connect() throws ClassNotFoundException {
		String driver;
		if(Utils.findClass("org.sqlite.JDBC")){
			driver = "org.sqlite.JDBC";
		} else throw new ClassNotFoundException("\"org.sqlite.JDBC\" not found.");

		HikariConfig config = new HikariConfig();
		config.setDriverClassName(driver);
		config.setJdbcUrl("jdbc:sqlite:" + Option.DATABASE_SQLITE_FOLDER.getString().replaceAll("%PLUGIN_FOLDER%", "%s") + File.separator + Option.DATABASE_SQLITE_DATABASE.getString());
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
				"CREATE TABLE IF NOT EXISTS `%s` ( `uuid` char(36) NOT NULL, `name` varchar(255) NOT NULL, `bbsname` varchar(255) NOT NULL COLLATE NOCASE, `binddate` bigint(0) NOT NULL, `rewardbefore` char(10) NOT NULL, `rewardtimes` int(0) NOT NULL, PRIMARY KEY (`uuid`) );",
				getTableName("posters"));
		try (Statement stmt = getConnection().createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void createTableTopStates() {
		String sql = String.format(
				"CREATE TABLE IF NOT EXISTS `%s` ( `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `bbsname` varchar(255) NOT NULL COLLATE NOCASE, `time` varchar(16) NOT NULL);",
				getTableName("topstates"));
		try (Statement stmt = getConnection().createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
