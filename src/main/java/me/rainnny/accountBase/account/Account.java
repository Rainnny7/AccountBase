package me.rainnny.accountBase.account;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Setter @Getter
public class Account {
    @Getter private static final Map<UUID, Account> cache = new HashMap<>();

    private final int id;
    private final UUID uuid;
    private Rank rank;

    private long logout;

    public Account(int id, UUID uuid, Rank rank) {
        this.id = id;
        this.uuid = uuid;
        this.rank = rank;
        cache.put(uuid, this);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public void remove() {
        cache.remove(uuid);
    }

    public static Account get(UUID uuid) {
        return cache.get(uuid);
    }
}