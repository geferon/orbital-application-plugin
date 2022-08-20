package net.geferon.economyplugin.commands;

import net.geferon.economyplugin.services.EconomyService;
import net.geferon.economyplugin.util.BalanceException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class CommandEarn implements CommandExecutor {
    private final EconomyService econ;

    @Inject
    public CommandEarn(EconomyService econ) {
        this.econ = econ;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be ran by a player!");
            return true;
        }
        Player player = (Player) sender;

        try {
            var earned = econ.earn(player);
            sender.sendMessage("Earned " + earned + ". Current balance: " + econ.getBalance(player));
        } catch (BalanceException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            return true;
        }

        return true;
    }
}
