package me.zelha.nextbots.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ListCommand extends NextbotCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        StringBuilder list = new StringBuilder();

        if (dataFolder.list() == null) {
            sender.sendMessage("§cNo nextbots found!");

            return true;
        }

        for (String string : dataFolder.list()) {
            list.append(string.replace(".yml", "")).append("\n");
        }

        sender.sendMessage(list.toString());

        return true;
    }

    @Override
    public void help(CommandSender sender, int page) {
        sender.sendMessage(
                "§7-------------------- [ §cNextbots §7] --------------------\n" +
                "§c/Nextbot list\n" +
                "§7Lists all nextbots with a valid .yml file in the plugin's data folder.\n" +
                "§7ex: §c/nextbot list"
        );
    }
}
