package me.rainnny.accountBase.account;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.UUID;

public class AccountRepository {
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `accounts`.`accounts` ( `id` INT NOT NULL AUTO_INCREMENT , `uuid` VARCHAR(36) NOT NULL , `rank` VARCHAR(35) NOT NULL , PRIMARY KEY (`id`))";
    private static final String SELECT_ACCOUNT = "SELECT * FROM accounts WHERE uuid=?";
    private static final String INSERT_ACCOUNT = "INSERT INTO `accounts` (`id`, `uuid`, `rank`) VALUES (NULL, ?, ?)";
    private static final String UPDATE_RANK = "UPDATE `accounts` SET `rank` = ? WHERE `accounts`.`uuid` = ?";

    private final HikariDataSource dataSource;

    public AccountRepository() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/accounts");
        config.setUsername("accounts");
        config.setPassword("c9gBH+eBWDYbj+");
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        dataSource = new HikariDataSource(config);
    }

    public void createTable() {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.prepareStatement(CREATE_TABLE).execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public boolean loadAccount(UUID uuid, Rank rank) {
        int id = -1;

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_ACCOUNT);
            statement.setString(1, uuid.toString());
            boolean newAccount = true;

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                newAccount = false;
                id = rs.getInt("id");
                rank = Rank.valueOf(rs.getString("rank"));
            }

            if (newAccount) {
                statement = connection.prepareStatement(INSERT_ACCOUNT, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, uuid.toString());
                statement.setString(2, rank.name());
                statement.execute();
                rs = statement.getGeneratedKeys();
                if (rs.next())
                    id = rs.getInt(1);
            }

            if (id == -1)
                return false;
            else {
                new Account(id, uuid, rank);
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

    public boolean updateRank(UUID uuid, Rank rank) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(UPDATE_RANK);
            statement.setString(1, rank.name());
            statement.setString(2, uuid.toString());
            statement.execute();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }
}