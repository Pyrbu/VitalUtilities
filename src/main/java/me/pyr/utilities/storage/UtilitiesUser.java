package me.pyr.utilities.storage;

import lombok.Data;

import java.util.UUID;

@Data
public class UtilitiesUser {

    private final UUID uuid;
    private boolean commandSpyEnabled;

    public UtilitiesUser(UUID uuid) {
        this.uuid = uuid;
    }
}
