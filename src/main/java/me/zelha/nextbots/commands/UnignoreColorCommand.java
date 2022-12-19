package me.zelha.nextbots.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Set;

public class UnignoreColorCommand extends NextbotCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1) {
            help(sender, 0);

            return true;
        }

        FileConfiguration config = getConfig(args[1], sender);

        if (config == null) return true;

        ConfigurationSection ignoredColors = config.getConfigurationSection("ignoredColors");
        Set<String> keySet = ignoredColors.getKeys(false);
        int index;

        try {
            index = Integer.parseInt(args[2]);
        } catch (Throwable e) {
            sender.sendMessage("§cInvalid number.");

            return true;
        }

        if (keySet.size() == 0) {
            sender.sendMessage("§cThis nextbot has no ignored colors!");

            return true;
        }

        if (index <= 0 || index > keySet.size()) {
            sender.sendMessage(
                    "§cTheres no color at that index!\n" +
                    "§cNumber must be between 1-" + keySet.size()
            );

            return true;
        }

        for (int i = index; i < keySet.size(); i++) {
            ignoredColors.set("color" + (i - 1), ignoredColors.getColor("color" + i));
        }

        ignoredColors.set("color" + (keySet.size() - 1), null);

        if (!save(config, args[1], sender)) return true;

        applyToBots(args[1], display -> display.removeIgnoredColor(index));

        return true;
    }

    @Override
    public void help(CommandSender sender, int page) {
        sender.sendMessage(
                "§7-------------------- [ §cNextbots §7] --------------------\n" +
                "§c/Nextbot unignorecolor <name> <index>\n" +
                "§7Makes the given nextbot not ignore the color set at the given index.\n" +
                "§7ex: §c/nextbot unignorecolor bot 1"
        );
    }
}
