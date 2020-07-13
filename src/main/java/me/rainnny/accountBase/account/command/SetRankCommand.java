package me.rainnny.accountBase.account.command;

import me.rainnny.accountBase.AccountBase;
import me.rainnny.accountBase.account.Account;
import me.rainnny.accountBase.account.Rank;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetRankCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("accountbase.command.setrank")) {
            sender.sendMessage("§cNo permission.");
            return true;
        }
        if (args.length < 2)
            return false;
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }
        Rank rank;
        try {
            rank = Rank.valueOf(args[1].toUpperCase());
        } catch (Exception ex) {
            sender.sendMessage("§cRank not found.");
            return true;
        }
        if (AccountBase.getAccountManager().getRepository().updateRank(target.getUniqueId(), rank)) {
            Account.get(target.getUniqueId()).setRank(rank);
            sender.sendMessage("§aSet §f" + target.getName() + "'s §arank to §6" + WordUtils.capitalize(rank.name().toLowerCase()));
        } else {
            sender.sendMessage("§cFailed to update targets rank!");
        }
        return true;
    }
}