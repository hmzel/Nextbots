package me.zelha.nextbots.commands;

import me.zelha.nextbots.Main;
import me.zelha.nextbots.nextbot.Nextbot;
import me.zelha.nextbots.nextbot.NextbotDisplay;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ImageLinkCommand extends NextbotCommand {

    private final File dataFolder = Main.getInstance().getDataFolder();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 2) {
            help(sender);

            return true;
        }

        File configFile = new File(dataFolder, args[1] + ".yml");

        if (!configFile.exists()) {
            sender.sendMessage("§cNextbot " + args[1] + " doesn't exist!");

            return true;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        config.set("imageLink", args[2]);

        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
            sender.sendMessage("§cSomething went wrong saving config file " + configFile.getPath());

            return true;
        }

        for (Nextbot bot : Main.getBots()) {
            if (!bot.getName().equals(args[1])) continue;

            NextbotDisplay display = bot.getDisplay();

            for (int i = 0; i < display.getFrameAmount(); i++) {
                display.removeFrame(0);
            }

            display.addImage(args[2]);
        }

        sender.sendMessage("§cSet " + args[1] + "'s image to " + args[2]);

        return true;
    }

    @Override
    public void help(CommandSender sender) {
        sender.sendMessage(
                "§7-------------------- [ §cNextbots §7] --------------------\n" +
                "§c/Nextbot imagelink <name> <link>\n" +
                "§7Sets what link the nextbot will get it's image from. can be jpg, png, gif, etc.\n" +
                "§7ex: §c/nextbot imagelink bot https://media.tenor.com/b8Pf3bbUYfAAAAAC/faith-new-blood.gif"
        );
    }
}
