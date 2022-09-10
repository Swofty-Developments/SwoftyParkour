package net.swofty.parkour.plugin.sql;

import net.swofty.parkour.plugin.SwoftyParkour;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class SQLDatabase
{
    private static final String DATABASE_FILENAME = "database.db";

    private Connection connection;
    private File file;

    public SQLDatabase()
    {
        File file = new File(SwoftyParkour.getPlugin().getDataFolder(), DATABASE_FILENAME);
        if (!file.exists())
        {
            try
            {
                file.getParentFile().mkdirs();
                file.createNewFile();
                SwoftyParkour.getPlugin().saveResource(DATABASE_FILENAME, false);
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
        this.file = file;
    }

    public Connection getConnection()
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
            if (connection != null)
            {
                connection.prepareStatement("CREATE TABLE IF NOT EXISTS `parkourTimes` (\n" +
                        "\t`uuid` TEXT,\n" +
                        "\t`parkour-times` TEXT\n" +
                        ");").execute();
                return connection;
            }
        }
        catch (SQLException | ClassNotFoundException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    public HashMap<String, Long> getParkourTimes(UUID uuid) {
        try (Connection connection = SwoftyParkour.getPlugin().sql.getConnection())
        {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `parkourTimes` WHERE uuid=?");
            statement.setString(1, uuid.toString());
            ResultSet set = statement.executeQuery();
            set.next();

            HashMap<String, Long> parkourTimes = new HashMap<>();

            Arrays.stream(set.getString("parkour-times").split(";")).forEach(entry -> {
                parkourTimes.put(entry.split("---")[0], Long.parseLong(entry.split("---")[1]));
            });

            set.close();
            return parkourTimes;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }
}