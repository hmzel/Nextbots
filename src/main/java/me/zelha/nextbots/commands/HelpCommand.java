package me.zelha.nextbots.commands;

import me.zelha.nextbots.NextbotSubCommands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class HelpCommand extends NextbotCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1) {
            super.help(sender);

            return true;
        }

        for (NextbotSubCommands subCommand : NextbotSubCommands.values()) {
            if (subCommand.name().equalsIgnoreCase(args[1])) {
                subCommand.getCommand().help(sender);

                return true;
            }
        }

        super.help(sender);

        return true;
    }

    @Override
    public void help(CommandSender sender) {
        sender.sendMessage(
                "§7-------------------- [ §cNextbots §7] --------------------\n" +
                "§c/Nextbot help <command>\n" +
                "§7Displays useful information about commands, and goes more in-depth on a command's usage if you put one as an argument.\n" +
                "§7ex: §c/nextbot help help"
        );
    }
}
