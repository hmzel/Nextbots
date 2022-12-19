package me.zelha.nextbots.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class HeightCommand extends NextbotCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1) {
            help(sender, 0);

            return true;
        }

        FileConfiguration config = getConfig(args[1], sender);
        double height;

        if (config == null) return true;

        try {
            height = Double.parseDouble(args[2]);
        } catch (Throwable e) {
            sender.sendMessage("§cInvalid number.");

            return true;
        }

        config.set("height", height);

        if (!save(config, args[1], sender)) return true;

        applyToBots(args[1], display -> display.setZRadius(height));

        return true;
    }

    @Override
    public void help(CommandSender sender, int page) {
        sender.sendMessage(
                "§7-------------------- [ §cNextbots §7] --------------------\n" +
                "§c/Nextbot height <name> <number>\n" +
                "§7Sets the height of the nextbot's image in blocks.\n" +
                "§7ex: §c/nextbot height bot 5"
        );
    }
}
