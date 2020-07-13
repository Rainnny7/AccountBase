package me.rainnny.accountBase;

import lombok.Getter;
import me.rainnny.accountBase.account.AccountManager;
import me.rainnny.accountBase.account.command.SetRankCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class AccountBase extends JavaPlugin {
    @Getter private static AccountManager accountManager;

    @Override
    public void onEnable() {
        accountManager = new AccountManager(this);
        getCommand("setrank").setExecutor(new SetRankCommand());
    }
}