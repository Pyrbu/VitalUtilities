package lol.pyr.utilities.storage.implementations;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import lol.pyr.utilities.UtilitiesPlugin;
import lol.pyr.utilities.configuration.UtilitiesConfiguration;
import lol.pyr.utilities.storage.StorageImplementationProvider;
import lol.pyr.utilities.storage.model.User;
import lombok.val;
import org.bson.Document;
import org.bson.UuidRepresentation;

import java.util.UUID;

public class MongoDBStorage implements StorageImplementationProvider {
    private final MongoClient client;
    private final MongoDatabase database;

    public MongoDBStorage(UtilitiesPlugin plugin) {
        UtilitiesConfiguration config = plugin.getUtilitiesConfig();
        MongoClientOptions options = MongoClientOptions.builder()
                .uuidRepresentation(UuidRepresentation.JAVA_LEGACY).build();
        MongoCredential credential = MongoCredential.createCredential(
                config.getMongoUsername(),
                config.getMongoDatabase(),
                config.getMongoPassword().toCharArray()
        );
        ServerAddress address = new ServerAddress(
                config.getMongoHostname(),
                config.getMongoPort()
        );
        client = new MongoClient(address, credential, options);
        database = client.getDatabase(config.getMongoDatabase());

    }

    @Override
    public User loadUser(UUID uuid) {
        Document doc = database.getCollection("users").find(new Document("_id", uuid)).first();
        val user = new User(uuid);
        if (doc == null) return user;
        if (doc.containsKey("command-spy-enabled")) user.setCommandSpyEnabled(doc.getBoolean("command-spy-enabled"));
        if (doc.containsKey("staff-chat-toggled")) user.setCommandSpyEnabled(doc.getBoolean("staff-chat-toggled"));
        if (doc.containsKey("staff-notifications-enabled")) user.setCommandSpyEnabled(doc.getBoolean("staff-notifications-enabled"));
        return user;
    }

    @Override
    public void saveUser(User user) {
        Document doc = userToDocument(user);
        database.getCollection("users").replaceOne(new Document("_id", user.getUuid()), doc, new ReplaceOptions().upsert(true));
    }

    @Override
    public void shutdown() {
        if (client != null) client.close();
    }

    private static Document userToDocument(User user) {
        return new Document("_id", user.getUuid())
                .append("command-spy-enabled", user.isCommandSpyEnabled())
                .append("staff-chat-toggled", user.isStaffChatToggled())
                .append("staff-notifications-enabled", user.isStaffNotificationsEnabled());
    }
}
