package me.zelha.nextbots.commands;

import me.zelha.nextbots.Main;
import me.zelha.nextbots.nextbot.Nextbot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.ArrayList;

public class RemoveCommand extends NextbotCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1) {
            help(sender, 0);

            return true;
        }

        if (new File(dataFolder, args[1] + ".yml").delete()) {
            sender.sendMessage("§cSuccessfully deleted " + args[1]);

            for (Nextbot bot : new ArrayList<>(Main.getBots())) {
                if (!bot.getName().equals(args[1])) continue;

                bot.despawn();
            }
        } else {
            sender.sendMessage("§cNextbot " + args[1] + " doesn't exist!");
        }

        return true;
    }

    @Override
    public void help(CommandSender sender, int page) {
        sender.sendMessage(
                "§7-------------------- [ §cNextbots §7] --------------------\n" +
                "§c/Nextbot remove <name>\n" +
                "§7Removes the config file that is used by this plugin in order to summon a nextbot.\n" +
                "§7ex: §c/nextbot remove bot"
        );
    }
}
