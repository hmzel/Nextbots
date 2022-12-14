package me.zelha.nextbots.commands;

import me.zelha.nextbots.NextbotSubCommands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class NextbotCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            help(sender);

            return true;
        }

        for (NextbotSubCommands subCommand : NextbotSubCommands.values()) {
            if (subCommand.name().equalsIgnoreCase(args[0])) {
                subCommand.getCommand().onCommand(sender, command, label, args);

                return true;
            }
        }

        help(sender);

        return true;
    }

    public void help(CommandSender sender) {
        sender.sendMessage(
                "§7-------------------- [ §cNextbots §7] --------------------\n" +
                "§7- §c/Nextbot help <command> §f- §7Displays this, or the usage of a command."
        );
    }
}
