package me.zelha.nextbots.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class FuzzCommand extends NextbotCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1) {
            help(sender, 0);

            return true;
        }

        FileConfiguration config = getConfig(args[1], sender);
        int fuzz;

        if (config == null) return true;

        try {
            fuzz = Integer.parseInt(args[2]);
        } catch (Throwable e) {
            sender.sendMessage("§cInvalid number.");

            return true;
        }

        config.set("fuzz", fuzz);

        if (!save(config, args[1], sender)) return true;

        applyToBots(args[1], display -> display.setFuzz(fuzz));

        return true;
    }

    @Override
    public void help(CommandSender sender, int page) {
        sender.sendMessage(
                "§7-------------------- [ §cNextbots §7] --------------------\n" +
                "§c/Nextbot fuzz <name> <number>\n" +
                "§7Sets how close pixel colors need to be to ignored colors in order to be ignored.\n" +
                "§7ex: §c/nextbot fuzz bot 5"
        );
    }
}
