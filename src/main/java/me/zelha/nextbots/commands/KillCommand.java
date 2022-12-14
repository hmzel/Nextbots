package me.zelha.nextbots.commands;

import me.zelha.nextbots.Main;
import me.zelha.nextbots.nextbot.Nextbot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R2.util.CraftChatMessage;

import java.util.ArrayList;

public class KillCommand extends NextbotCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1) {
            help(sender, 0);

            return true;
        }

        for (Nextbot bot : new ArrayList<>(Main.getBots())) {
            if (!CraftChatMessage.fromComponent(bot.getCustomName()).equals(args[1])) continue;

            bot.despawn();
        }

        return true;
    }

    @Override
    public void help(CommandSender sender, int page) {
        sender.sendMessage(
                "§7-------------------- [ §cNextbots §7] --------------------\n" +
                "§c/Nextbot kill <name>\n" +
                "§7Kills all bots currently alive with the given name.\n" +
                "§7ex: §c/nextbot kill bot"
        );
    }
}
