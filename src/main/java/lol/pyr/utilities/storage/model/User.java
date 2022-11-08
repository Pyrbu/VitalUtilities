package lol.pyr.utilities.storage.model;

import lombok.Data;

import java.util.UUID;

@Data
public class User {
    private final UUID uuid;
    private boolean commandSpyEnabled = false;
    private boolean staffChatToggled = false;
    private boolean staffNotificationsEnabled = true;
}
