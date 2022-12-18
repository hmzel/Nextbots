package me.zelha.nextbots.commands;

import me.zelha.nextbots.Main;
import me.zelha.nextbots.NextbotSubCommands;
import me.zelha.nextbots.nextbot.Nextbot;
import me.zelha.nextbots.nextbot.NextbotDisplay;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class NextbotCommand implements CommandExecutor {

    protected final File dataFolder = Main.getInstance().getDataFolder();

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
                "§7- §c/Nextbot help <command> §f- §7Displays this, or the usage of a command.\n" +
                "§7- §c/Nextbot create <name> §f- §7Creates a nextbot with the given name.\n" +
                "§7- §c/Nextbot summon <name> <x> <y> <z> §f- §7Summons the nextbot with the given name.\n" +
                "§7- §c/Nextbot list §f- §7Lists all existing nextbots.\n" +
                "§7- §c/Nextbot remove <name> §f- §7Removes the nextbot with the given name.\n" +
                "§7- §c/Nextbot imagelink <name> <link> §f- §7Sets the link that the given nextbot will get it's image from.\n" +
                "§7- §c/Nextbot imagefile <name> <path> §f- §7Sets the file that the given nextbot will get it's image from.\n" +
                "§7- §c/Nextbot particle <name> <number> §f- §7Sets the amount of particles the given nextbot will send per tick.\n" +
                "§7- §c/Nextbot size <name> <number> §f- §7Sets the size of the given nextbot based on it's aspect ratio."
        );
    }

    protected FileConfiguration getConfig(String name, CommandSender sender) {
        File configFile = new File(dataFolder, name + ".yml");

        if (!configFile.exists()) {
            sender.sendMessage("§cNextbot " + name + " doesn't exist!");

            return null;
        }

        return YamlConfiguration.loadConfiguration(configFile);
    }

    protected boolean save(FileConfiguration config, String name, CommandSender sender) {
        File configFile = new File(dataFolder, name + ".yml");

        try {
            config.save(configFile);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            sender.sendMessage("§cSomething went wrong saving config file " + configFile.getPath());

            return false;
        }
    }

    protected void applyToBots(String name, Consumer<NextbotDisplay> consumer) {
        for (Nextbot bot : Main.getBots()) {
            if (!bot.getName().equals(name)) continue;

            consumer.accept(bot.getDisplay());
        }
    }
}
