package me.zelha.nextbots.commands;

import me.zelha.nextbots.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;

public class RemoveCommand extends NextbotCommand {

    private final File dataFolder = Main.getInstance().getDataFolder();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1) {
            help(sender);

            return true;
        }

        if (new File(dataFolder, args[1] + ".yml").delete()) {
            sender.sendMessage("§cSuccessfully deleted " + args[1]);
        } else {
            sender.sendMessage("§cNextbot " + args[1] + " doesn't exist!");
        }

        return true;
    }

    @Override
    public void help(CommandSender sender) {
        sender.sendMessage(
                "§7-------------------- [ §cNextbots §7] --------------------\n" +
                "§c/Nextbot remove <name>\n" +
                "§7Removes the config file that is used by this plugin in order to summon a nextbot.\n" +
                "§7ex: §c/nextbot remove bot"
        );
    }
}
