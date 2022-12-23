package me.zelha.nextbots;

import me.zelha.nextbots.commands.NextbotCommand;
import me.zelha.nextbots.nextbot.Nextbot;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R2.CraftChunk;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public final class Main extends JavaPlugin {

    private static final List<Nextbot> bots = new ArrayList<>();
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;

        getCommand("nextbot").setExecutor(new NextbotCommand());

        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    chunk:
                    for (Chunk chunk : world.getLoadedChunks()) {
                        LevelChunk nmsChunk = ((CraftChunk) chunk).getHandle();

                        for (Nextbot bot : bots) {
                            if (bot.level != nmsChunk.getLevel()) continue;

                            ChunkPos botChunk = bot.level.getChunk((int) bot.getX() >> 4, (int) bot.getX() >> 4).getPos();
                            double dist = Math.pow(botChunk.x - nmsChunk.getPos().x, 2) + Math.pow(botChunk.z - nmsChunk.getPos().z, 2);

                            chunk.setForceLoaded(dist < 256);

                            continue chunk;
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0, 1);
    }

    @Override
    public void onDisable() {
        for (Nextbot bot : new ArrayList<>(bots)) {
            bot.despawn();
        }
    }

    public static void registerBot(Nextbot bot) {
        bots.add(bot);
    }

    public static void unregisterBot(Nextbot bot) {
        bots.remove(bot);
    }

    public static Main getInstance() {
        return instance;
    }

    public static List<Nextbot> getBots() {
        return bots;
    }
}
