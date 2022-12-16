package me.zelha.nextbots.nextbot;

import me.zelha.nextbots.Main;
import net.minecraft.server.v1_8_R3.Chunk;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkUnloadPrevention implements Listener {
    @EventHandler
    public void onUnload(ChunkUnloadEvent e) {
        Chunk nmsChunk = ((CraftChunk) e.getChunk()).getHandle();

        for (Nextbot bot : Main.getBots()) {
            Chunk botChunk = bot.getWorld().getChunkAt((int) bot.locX >> 4, (int) bot.locZ >> 4);

            if (nmsChunk.locX - 16 > botChunk.locX) continue;
            if (nmsChunk.locX + 16 < botChunk.locX) continue;
            if (nmsChunk.locZ - 16 > botChunk.locZ) continue;
            if (nmsChunk.locZ + 16 < botChunk.locZ) continue;

            e.setCancelled(true);

            return;
        }
    }
}
