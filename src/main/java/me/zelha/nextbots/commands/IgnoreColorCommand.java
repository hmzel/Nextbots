package me.zelha.nextbots.commands;

import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class IgnoreColorCommand extends NextbotCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1) {
            help(sender, 0);

            return true;
        }

        FileConfiguration config = getConfig(args[1], sender);

        if (config == null) return true;

        ConfigurationSection ignoredColors = config.getConfigurationSection("ignoredColors");
        Color color;
        int red;
        int green;
        int blue;

        try {
            red = Integer.parseInt(args[2]);
            green = Integer.parseInt(args[3]);
            blue = Integer.parseInt(args[4]);
        } catch (Throwable e) {
            sender.sendMessage("§cOne or more numbers are invalid.");

            return true;
        }

        try {
            color = Color.fromRGB(red, green, blue);
        } catch (Throwable e) {
            sender.sendMessage("§cNumbers must be 0-255");

            return true;
        }

        ignoredColors.set("color" + ignoredColors.getKeys(false).size(), color);

        if (!save(config, args[1], sender)) return true;

        applyToBots(args[1], display -> display.addIgnoredColor(new hm.zelha.particlesfx.util.Color(red, green, blue)));

        return true;
    }

    @Override
    public void help(CommandSender sender, int page) {
        sender.sendMessage(
                "§7-------------------- [ §cNextbots §7] --------------------\n" +
                "§c/Nextbot ignorecolor <name> <red> <green> <blue>\n" +
                "§7Makes the given nextbot not display the given color and any color within the range of the fuzz value.\n" +
                "§7ex: §c/nextbot ignorecolor bot 0 0 0"
        );
    }
}
