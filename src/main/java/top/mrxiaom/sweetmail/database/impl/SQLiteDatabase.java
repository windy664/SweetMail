package top.mrxiaom.sweetmail.database.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.configuration.MemoryConfiguration;
import top.mrxiaom.sweetmail.SweetMail;
import top.mrxiaom.sweetmail.utils.Util;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLiteDatabase extends AbstractSQLDatabase {
    HikariDataSource dataSource = null;
    @Override
    public void reload(MemoryConfiguration config) {
        onDisable();

        if (!Util.isPresent("org.sqlite.JDBC")) {
            SweetMail.warn("预料中的错误: 未找到 SQLite JDBC: org.sqlite.jdbc");
            SweetMail.warn("正在卸载插件，请使用最新版 Spigot 或其衍生服务端");
            Bukkit.getPluginManager().disablePlugin(SweetMail.getInstance());
            return;
        }

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("org.sqlite.JDBC");
        hikariConfig.setAutoCommit(true);
        hikariConfig.setMaxLifetime(120000L);
        hikariConfig.setIdleTimeout(5000L);
        hikariConfig.setConnectionTimeout(5000L);
        hikariConfig.setMinimumIdle(10);
        hikariConfig.setMaximumPoolSize(100);
        hikariConfig.setJdbcUrl("jdbc:sqlite:plugins/SweetMail/mail.db");

        dataSource = new HikariDataSource(hikariConfig);

        createTables();
    }

    @Override
    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void onDisable() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }

}
