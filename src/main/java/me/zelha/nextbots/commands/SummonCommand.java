package me.zelha.nextbots.commands;

import hm.zelha.particlesfx.util.Color;
import hm.zelha.particlesfx.util.LocationSafe;
import me.zelha.nextbots.Main;
import me.zelha.nextbots.nextbot.Nextbot;
import me.zelha.nextbots.nextbot.NextbotDisplay;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class SummonCommand extends NextbotCommand {

    private final File dataFolder = Main.getInstance().getDataFolder();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1) {
            help(sender);

            return true;
        }

        File configFile = new File(dataFolder, args[1] + ".yml");

        if (!configFile.exists()) {
            sender.sendMessage("§cNextbot " + args[1] + " doesn't exist!");

            return true;
        }

        LocationSafe center = new LocationSafe(Bukkit.getWorld("world"), 0, 255, 0);

        if (args.length > 2) {
            try {
                center.setX(Double.parseDouble(args[2]));
                center.setY(Double.parseDouble(args[3]));
                center.setZ(Double.parseDouble(args[4]));
            } catch (Throwable e) {
                sender.sendMessage("§cInvalid coordinates.");

                return true;
            }
        } else if (sender instanceof Player) {
            center = new LocationSafe(((Player) sender).getLocation());
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        String imageLink = config.getString("imageLink");
        String imageFile = config.getString("imageFile");
        int particles = config.getInt("particles");
        int xRadius = config.getInt("xRadius");
        int zRadius = config.getInt("zRadius");
        int frameDelay = config.getInt("frameDelay");
        int fuzz = config.getInt("fuzz");
        ConfigurationSection ignoredColors = config.getConfigurationSection("ignoredColors");

        if (imageLink.isEmpty() && imageFile.isEmpty()) {
            sender.sendMessage("§cNo image is set!");

            return true;
        }

        Nextbot bot;
        NextbotDisplay display;

        if (!imageFile.isEmpty()) {
            bot = new Nextbot(center, new File(imageFile), args[1]);
        } else {
            bot = new Nextbot(center, imageLink, args[1]);
        }

        display = bot.getDisplay();

        display.setParticleFrequency(particles);

        if (xRadius == 0 && zRadius == 0) {
            config.set("xRadius", display.getXRadius());
            config.set("zRadius", display.getZRadius());

            try {
                config.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            display.setXRadius(xRadius);
            display.setZRadius(zRadius);
        }

        display.setFrameDelay(frameDelay);
        display.setFuzz(fuzz);

        for (String string : ignoredColors.getValues(false).keySet()) {
            org.bukkit.Color color = ignoredColors.getColor(string);

            display.addIgnoredColor(new Color(color.getRed(), color.getGreen(), color.getBlue()));
        }

        return true;
    }

    @Override
    public void help(CommandSender sender) {
        sender.sendMessage(
                "§7-------------------- [ §cNextbots §7] --------------------\n" +
                "§c/Nextbot summon <name> <x> <y> <z>\n" +
                "§7Summons the nextbot with the given name using the info provided in it's config file.\n" +
                "§7ex:\n" +
                "§c/nextbot summon bot\n" +
                "§c/nextbot summon bot 0 255 0"
        );
    }
}
