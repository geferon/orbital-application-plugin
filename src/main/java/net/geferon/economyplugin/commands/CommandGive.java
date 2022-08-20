package net.geferon.economyplugin.commands;

import net.geferon.economyplugin.services.EconomyService;
import net.geferon.economyplugin.util.BalanceException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class CommandGive implements CommandExecutor {
    private final EconomyService econ;

    @Inject
    public CommandGive(EconomyService econ) {
        this.econ = econ;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be ran by a player!");
            return true;
        }
        Player player = (Player) sender;

        if (args.length != 2) return false; // Invalid parameter count, show parameters

        String name = args[0];
        Player target = Bukkit.getPlayer(name);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player with name \"" + name + "\" could not be found");
            return true;
        }

        Long amount;
        try {
            amount = Long.parseLong(args[1]);
        } catch (NumberFormatException err) {
            //sender.sendMessage(ChatColor.RED + "Amount is invalid or not a number");
            return false;
        }
        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "Amount cannot be negative or 0");
            return true;
        }

        try {
            econ.pay(player, target, amount);
        } catch (BalanceException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            return true;
        }

        return true;
    }
}
