package me.zelha.nextbots.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class WidthCommand extends NextbotCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1) {
            help(sender, 0);

            return true;
        }

        FileConfiguration config = getConfig(args[1], sender);
        double width;

        if (config == null) return true;

        try {
            width = Double.parseDouble(args[2]);
        } catch (Throwable e) {
            sender.sendMessage("§cInvalid number.");

            return true;
        }

        config.set("width", width);

        if (!save(config, args[1], sender)) return true;

        applyToBots(args[1], display -> display.setXRadius(width));

        return true;
    }

    @Override
    public void help(CommandSender sender, int page) {
        sender.sendMessage(
                "§7-------------------- [ §cNextbots §7] --------------------\n" +
                "§c/Nextbot width <name> <number>\n" +
                "§7Sets the width of the nextbot's image in blocks.\n" +
                "§7ex: §c/nextbot width bot 5"
        );
    }
}
