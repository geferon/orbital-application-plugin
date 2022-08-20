package net.geferon.economyplugin.commands;

import net.geferon.economyplugin.services.EconomyService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class CommandSetBal implements CommandExecutor {
    private final EconomyService econ;

    @Inject
    public CommandSetBal(EconomyService econ) {
        this.econ = econ;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player ply && !ply.isOp()) { // Only allow console or OP to run this cmd
            sender.sendMessage(ChatColor.RED + "This command can only be ran by an OP!");
            return true;
        }

        if (args.length != 2) return false; // Invalid arg count, return usage

        String name = args[0];
        Player target = Bukkit.getPlayer(name);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player with name \"" + name + "\" could not be found");
            return true;
        }

        try {
            long amount = Long.parseLong(args[1]);

            econ.setBalance(target, amount);
            target.sendMessage("Balance set successfully");
        } catch (NumberFormatException err) {
            //sender.sendMessage(ChatColor.RED + "Invalid amount supplied");
            return false; // Will return false to send usage of command
        }

        return true;
    }
}
