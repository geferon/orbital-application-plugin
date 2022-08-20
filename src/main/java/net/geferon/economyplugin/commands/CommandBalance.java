package net.geferon.economyplugin.commands;

import net.geferon.economyplugin.services.EconomyService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class CommandBalance implements CommandExecutor {
    private final EconomyService econ;

    @Inject
    public CommandBalance(EconomyService econ) {
        this.econ = econ;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target;
        if (args.length > 0) {
            String name = String.join(" ", args);
            target = Bukkit.getPlayer(name);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player with name \"" + name + "\" could not be found");
                return true;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be ran by a player!");
                return true;
            }
            target = (Player) sender;
        }

        sender.sendMessage((sender == target ? "Current" : target.getDisplayName() + "s") + " balance: " + econ.getBalance(target));

        return true;
    }
}
