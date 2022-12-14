package net.swofty.parkour.plugin.sql;

import net.swofty.parkour.plugin.SwoftyParkour;
import net.swofty.parkour.plugin.parkour.Parkour;
import net.swofty.parkour.plugin.parkour.ParkourRegistry;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class SQLDatabase {
    private static final String DATABASE_FILENAME = "database.db";
    private File file;

    public SQLDatabase() {
        File file = new File(SwoftyParkour.getPlugin().getDataFolder(), DATABASE_FILENAME);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                SwoftyParkour.getPlugin().saveResource(DATABASE_FILENAME, false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        this.file = file;
    }

    public Connection getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
            if (connection != null) {
                return connection;
            }
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Long getParkourTime(Parkour parkour, UUID uuid, SwoftyParkour plugin) {
        Long toReturn = null;
        try (Connection connection = plugin.sql.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `parkour_" + parkour.getName() + "` WHERE uuid=?");
            statement.setString(1, uuid.toString());
            ResultSet set = statement.executeQuery();

            toReturn = set.getLong("time");

            return toReturn;
        } catch (SQLException ex) {
            toReturn = null;
        }
        return toReturn;
    }

    public Map<UUID, Long> getParkourTop(Parkour parkour, SwoftyParkour plugin) {
        try (Connection connection = plugin.sql.getConnection()) {
            HashMap<UUID, Long> map = new HashMap();

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `parkour_" + parkour.getName() + "`");
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                map.put(UUID.fromString(set.getString("uuid")), set.getLong("time"));
            }

            return sortByValue(map);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Map<Parkour, Long> getTimesForPlayer(UUID uuid, SwoftyParkour plugin) {
        Map<Parkour, Long> toReturn = new HashMap<>();
        ParkourRegistry.parkourRegistry.forEach(parkour2 -> {
            toReturn.put(parkour2, getParkourTime(parkour2, uuid, plugin));
        });
        if (toReturn.values().stream().allMatch(Objects::isNull)) return null;
        return toReturn;
    }

    public int getPosition(UUID uuid, Parkour parkour, SwoftyParkour plugin) {
        Map<UUID, Long> map = getParkourTop(parkour, plugin);

        for (int x = 0; x < map.size(); x++) {
            if (new ArrayList<>(map.entrySet()).get(x).getKey().toString().equals(uuid.toString())) {
                return x + 1;
            }
        }

        return 0;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());
        Collections.reverse(list);

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        List<K> alKeys = new ArrayList<>(result.keySet());
        Collections.reverse(alKeys);

        Map<K, V> toReturn = new LinkedHashMap<>();
        // iterate LHM using reverse order of keys
        for(K strKey : alKeys){
            toReturn.put(strKey, result.get(strKey));
        }

        return toReturn;
    }
}