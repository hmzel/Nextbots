package me.zelha.nextbots;

import hm.zelha.particlesfx.ParticleSFXMain;
import hm.zelha.particlesfx.util.LocationSafe;
import me.zelha.nextbots.commands.NextbotCommand;
import me.zelha.nextbots.nextbot.Nextbot;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class Main extends JavaPlugin {

    private static Main instance;
    private static List<Nextbot> bots = new ArrayList<>();

    @Override
    public void onEnable() {
        ParticleSFXMain.setPlugin(this);
        getCommand("nextbot").setExecutor(new NextbotCommand());

        instance = this;

        new Nextbot(new LocationSafe(Bukkit.getPlayer("hmzel").getLocation()), new File("maxwell.gif"));
    }

    @Override
    public void onDisable() {
        for (Nextbot bot : new ArrayList<>(bots)) {
            bot.despawn();
        }
    }

    public static Main getInstance() {
        return instance;
    }

    public static void registerBot(Nextbot bot) {
        bots.add(bot);
    }

    public static void unregisterBot(Nextbot bot) {
        bots.remove(bot);
    }
}
