package me.zelha.nextbots;

import hm.zelha.particlesfx.ParticleSFXMain;
import me.zelha.nextbots.commands.NextbotCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        ParticleSFXMain.setPlugin(this);
        getCommand("nextbot").setExecutor(new NextbotCommand());
    }
}
