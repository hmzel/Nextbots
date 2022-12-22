package me.zelha.nextbots.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class ImageLinkCommand extends NextbotCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 2) {
            help(sender, 0);

            return true;
        }

        FileConfiguration config = getConfig(args[1], sender);

        if (config == null) return true;

        config.set("imageLink", args[2]);
        config.set("imageFile", "");

        if (!save(config, args[1], sender)) return true;

        applyToBots(args[1], display -> display.setImage(args[2]));
        sender.sendMessage("§cSet " + args[1] + "'s image to " + args[2]);

        return true;
    }

    @Override
    public void help(CommandSender sender, int page) {
        sender.sendMessage(
                "§7-------------------- [ §cNextbots §7] --------------------\n" +
                "§c/Nextbot imagelink <name> <link>\n" +
                "§7Sets what link the nextbot will get it's image from. can be jpg, png, gif, etc.\n" +
                "§7ex: §c/nextbot imagelink bot https://media.tenor.com/b8Pf3bbUYfAAAAAC/faith-new-blood.gif"
        );
    }
}
