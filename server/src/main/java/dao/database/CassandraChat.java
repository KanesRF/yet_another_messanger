package dao.database;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.cassandra.service.template.ColumnFamilyResult;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import models.Chat;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CassandraChat {
    private static CassandraChat INSTANCE = new CassandraChat();
    public static CassandraChat getInstance() {
        return INSTANCE;
    }

    private final String CONFIG_PATH = "config/cassandra.json";

    private final Cluster cluster;
    private final ColumnFamilyTemplate<String, String> template;
    private final String KEYSPACE_NAME = "messanger";
    private final String CHAT_COL_FAMILY = "chats";

    public CassandraChat() {
        Gson gson = new Gson();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(CONFIG_PATH));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Map<String, String> config = gson.fromJson(reader, HashMap.class);
        String clusterName = config.get("clusterName");
        String host = config.get("host");
        cluster = HFactory.getOrCreateCluster(clusterName,host);
        this.template = getTemplate(cluster, KEYSPACE_NAME, CHAT_COL_FAMILY);
    }


    public CassandraChat(String clusterName, String host) throws FileNotFoundException {
        this.cluster = HFactory.getOrCreateCluster(clusterName,host);
        this.template = getTemplate(cluster, KEYSPACE_NAME, CHAT_COL_FAMILY);
    }

    private static ColumnFamilyTemplate<String, String> getTemplate(Cluster cluster, String keyspaceName, String chatColFamily) {
        KeyspaceDefinition keyspaceDef = cluster.describeKeyspace(keyspaceName);
        if (keyspaceDef == null) {
            ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition(keyspaceName,
                    chatColFamily,
                    ComparatorType.BYTESTYPE);
            KeyspaceDefinition newKeyspace = HFactory.createKeyspaceDefinition(keyspaceName,
                    ThriftKsDef.DEF_STRATEGY_CLASS,
                    1,
                    Arrays.asList(cfDef));
            cluster.addKeyspace(newKeyspace, true);
        }
        Keyspace keyspace = HFactory.createKeyspace(keyspaceName, cluster);
        return new ThriftColumnFamilyTemplate<String, String>(keyspace,
                chatColFamily,
                StringSerializer.get(),
                StringSerializer.get());
    }

    public String update(Chat chat) {
        try {
            String uuid = TimeUUIDUtils.getUniqueTimeUUIDinMillis().toString();
            ColumnFamilyUpdater<String, String> updater = template.createUpdater(uuid);
            updater.setString("name", chat.getName());
            updater.setLong("timestamp", System.currentTimeMillis());
            template.update(updater);
            return uuid;
        } catch (HectorException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Chat get(String uuid) {
        try {
            ColumnFamilyResult<String, String> res = template.queryColumns(uuid);
            String name = res.getString("name");
            Chat chat = new Chat(name);
            return chat;
        } catch (HectorException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean delete(String uuid) {
        try {
            template.deleteRow(uuid);
            return true;
        } catch (HectorException e) {
            e.printStackTrace();
            return false;
        }
    }
}