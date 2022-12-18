package me.zelha.nextbots.commands;

import me.zelha.nextbots.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class CreateCommand extends NextbotCommand {

    private final File dataFolder = Main.getInstance().getDataFolder();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1) {
            help(sender);

            return true;
        }

        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        File configFile = new File(dataFolder, args[1] + ".yml");

        if (configFile.exists()) {
            sender.sendMessage("§cA nextbot by this name already exists!");

            return true;
        }

        try {
            configFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            sender.sendMessage("§cSomething went wrong creating file " + configFile.getPath());

            return true;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        config.set("imageLink", "");
        config.set("imageFile", "");
        config.set("particles", 2000);
        config.set("xRadius", 0);
        config.set("zRadius", 0);
        config.set("frameDelay", 0);
        config.set("fuzz", 0);
        config.createSection("ignoredColors");

        if (!save(config, args[1], sender)) return true;

        sender.sendMessage("§cSuccessfully created " + args[1]);

        return true;
    }

    @Override
    public void help(CommandSender sender) {
        sender.sendMessage(
                "§7-------------------- [ §cNextbots §7] --------------------\n" +
                "§c/Nextbot create <name>\n" +
                "§7Creates a config file that can be read by this plugin in order to summon a nextbot.\n" +
                "§7ex: §c/nextbot create bot"
        );
    }
}
