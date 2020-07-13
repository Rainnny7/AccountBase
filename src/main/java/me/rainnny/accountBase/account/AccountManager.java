package me.rainnny.accountBase.account;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Bukkit.getPluginManager;
import static org.bukkit.Bukkit.getScheduler;

public class AccountManager implements Listener {
    private static final boolean INSTANT_REMOVE = false; // Instantly remove accounts on quit
    @Getter private final AccountRepository repository = new AccountRepository();

    private final Map<UUID, Long> connecting = new HashMap<>();

    public AccountManager(JavaPlugin plugin) {
        repository.createTable();

        if (!INSTANT_REMOVE) {
            getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                Account.getCache().entrySet()
                        .removeIf(entry -> System.currentTimeMillis() - entry.getValue().getLogout() >= 60000L * 3L);
            }, 60L * 20L, 60L * 20L);
        }

        getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onAsyncLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        connecting.put(uuid, System.currentTimeMillis());

        if (!repository.loadAccount(uuid, Rank.DEFAULT))
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "There was a problem logging you in!");

        long elapsed = System.currentTimeMillis() - connecting.remove(uuid);
        if (elapsed >= 10000L)
            Bukkit.getLogger().warning("[AccountBase] - " + event.getName() + " took longer than 10 seconds to connect!");
        Bukkit.getLogger().info("[AccountBase] - " + event.getName() + " took " + elapsed + "ms to connect");
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        Account account = Account.getCache().get(event.getPlayer().getUniqueId());
        if (INSTANT_REMOVE)
            account.remove();
        else account.setLogout(System.currentTimeMillis());
    }
}