package lol.pyr.utilities.storage.model;

import lombok.Data;

import java.util.UUID;

@Data
public class User {

    private final UUID uuid;
    private boolean commandSpyEnabled;
    private boolean staffNotificationsEnabled;

    public User(UUID uuid) {
        this.uuid = uuid;
    }
}
