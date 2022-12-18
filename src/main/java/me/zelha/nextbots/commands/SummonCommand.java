package me.zelha.nextbots.commands;

import hm.zelha.particlesfx.util.Color;
import hm.zelha.particlesfx.util.LocationSafe;
import me.zelha.nextbots.nextbot.Nextbot;
import me.zelha.nextbots.nextbot.NextbotDisplay;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class SummonCommand extends NextbotCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1) {
            help(sender);

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

        FileConfiguration config = getConfig(args[1], sender);

        if (config == null) return true;

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
            save(config, sender);
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
