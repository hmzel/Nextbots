package me.zelha.nextbots.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class ImageFileCommand extends NextbotCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 2) {
            help(sender, 0);

            return true;
        }

        File imageFile = new File(args[2]);

        if (!imageFile.exists()) {
            sender.sendMessage("§cThere is no file at " + args[2] + " !");

            return true;
        }

        FileConfiguration config = getConfig(args[1], sender);

        if (config == null) return true;

        config.set("imageFile", args[2]);
        config.set("imageLink", "");

        if (!save(config, args[1], sender)) return true;

        applyToBots(args[1], display -> {
            while (display.getFrameAmount() != 0) {
                display.removeFrame(0);
            }

            display.addImage(imageFile);
        });

        sender.sendMessage("§cSet " + args[1] + "'s image to " + args[2]);

        return true;
    }

    @Override
    public void help(CommandSender sender, int page) {
        sender.sendMessage(
                "§7-------------------- [ §cNextbots §7] --------------------\n" +
                "§c/Nextbot imagefile <name> <path>\n" +
                "§7Sets what file the nextbot will get it's image from. can be jpg, png, gif, etc.\n" +
                "§7ex: §c/nextbot imagefile bot plugins/Nextbots/quack.gif"
        );
    }
}
