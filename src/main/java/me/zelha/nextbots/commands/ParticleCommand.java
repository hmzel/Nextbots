package me.zelha.nextbots.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class ParticleCommand extends NextbotCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1) {
            help(sender, 0);

            return true;
        }

        FileConfiguration config = getConfig(args[1], sender);
        int particles;

        if (config == null) return true;

        try {
            particles = Integer.parseInt(args[2]);
        } catch (Throwable e) {
            sender.sendMessage("§cInvalid number.");

            return true;
        }

        if (particles < 1) {
            sender.sendMessage("§cNeeds to be at least 1 particle.");

            return true;
        }

        config.set("particles", particles);

        if (!save(config, args[1], sender)) return true;

        applyToBots(args[1], display -> display.setParticleFrequency(particles));

        return true;
    }

    @Override
    public void help(CommandSender sender, int page) {
        sender.sendMessage(
                "§7-------------------- [ §cNextbots §7] --------------------\n" +
                "§c/Nextbot particle <name> <number>\n" +
                "§7Sets how many particles the given nextbot will send every tick, default 2000.\n" +
                "§7ex: §c/nextbot particle bot 1000"
        );
    }
}
