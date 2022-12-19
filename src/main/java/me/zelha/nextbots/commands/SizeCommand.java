package me.zelha.nextbots.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

public class SizeCommand extends NextbotCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1) {
            help(sender);

            return true;
        }

        FileConfiguration config = getConfig(args[1], sender);

        if (config == null) return true;

        BufferedImage image;
        String imageLink = config.getString("imageLink");
        String imageFile = config.getString("imageFile");
        double width;
        double height;
        double size;

        try {
            size = Double.parseDouble(args[2]);
        } catch (Throwable e) {
            sender.sendMessage("§cInvalid number.");

            return true;
        }

        if (imageLink.isEmpty() && imageFile.isEmpty()) {
            sender.sendMessage("§cNo image is set!");

            return true;
        }

        try {
            if (!imageFile.isEmpty()) {
                image = ImageIO.read(new File(imageFile));
            } else {
                image = ImageIO.read(new URL(imageLink));
            }
        } catch (Throwable e) {
            e.printStackTrace();
            sender.sendMessage("§cFailed to load image.");

            return true;
        }

        if (image.getWidth() <= image.getHeight()) {
            width = size * ((double) image.getWidth() / image.getHeight());
            height = size;
        } else {
            width = size;
            height = size * ((double) image.getHeight() / image.getWidth());
        }

        config.set("width", width);
        config.set("height", height);

        if (!save(config, args[1], sender)) return true;

        applyToBots(args[1], display -> {
            display.setXRadius(width);
            display.setZRadius(height);
        });

        return true;
    }

    @Override
    public void help(CommandSender sender) {
        sender.sendMessage(
                "§7-------------------- [ §cNextbots §7] --------------------\n" +
                "§c/Nextbot size <name> <number>\n" +
                "§7Sets the width and height so that whichever is largest is equal to the given number, and whichever is smallest is set to " +
                "§7(size * (smallest / largest))\n" +
                "§7ex: §c/nextbot size bot 5"
        );
    }
}
