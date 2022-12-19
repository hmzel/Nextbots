package me.zelha.nextbots.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class FrameDelayCommand extends NextbotCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1) {
            help(sender);

            return true;
        }

        FileConfiguration config = getConfig(args[1], sender);
        int frameDelay;

        if (config == null) return true;

        try {
            frameDelay = Integer.parseInt(args[2]);
        } catch (Throwable e) {
            sender.sendMessage("§cInvalid number.");

            return true;
        }

        config.set("frameDelay", frameDelay);

        if (!save(config, args[1], sender)) return true;

        applyToBots(args[1], display -> display.setFrameDelay(frameDelay));

        return true;
    }

    @Override
    public void help(CommandSender sender) {
        sender.sendMessage(
                "§7-------------------- [ §cNextbots §7] --------------------\n" +
                "§c/Nextbot framedelay <name> <number>\n" +
                "§7Sets how many ticks it will take for the given nextbot's display to go to the next frame.\n" +
                "§7ex: §c/nextbot framedelay bot 5"
        );
    }
}
