package me.zelha.nextbots.nextbot;

import com.sun.imageio.plugins.gif.GIFImageReader;
import hm.zelha.particlesfx.shapers.parents.RotationHandler;
import hm.zelha.particlesfx.util.Color;
import hm.zelha.particlesfx.util.LocationSafe;
import hm.zelha.particlesfx.util.PatchedGIFImageReader;
import me.zelha.nextbots.Main;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class NextbotDisplay extends RotationHandler {

    private final Location locationHelper = new Location(null, 0, 0, 0);
    private final Vector vectorHelper = new Vector(0, 0, 0);
    private final ThreadLocalRandom rng = ThreadLocalRandom.current();
    private final List<BufferedImage> images = new ArrayList<>();
    private final List<Color> ignoredColors = new ArrayList<>();
    private final BukkitTask animator;
    private int particleFrequency = 2000;
    private double xRadius = 0;
    private double zRadius = 0;
    private int fuzz = 0;
    private int frameDelay = 0;
    private int frame = 0;
    private int displaysThisFrame = 0;
    private Thread currentThread = null;

    public NextbotDisplay(LocationSafe center, String link) {
        setCenter(center);
        setImage(link);

        animator = new BukkitRunnable() {
            @Override
            public void run() {
                display();
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 1, 1);
    }

    public NextbotDisplay(LocationSafe center, File path) {
        setCenter(center);
        setImage(path);

        animator = new BukkitRunnable() {
            @Override
            public void run() {
                display();
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 1, 1);
    }
    
    public void display() {
        if (frame >= images.size()) {
            frame = 0;

            return;
        }

        BufferedImage image = images.get(frame);

        for (Player player : Bukkit.getOnlinePlayers()) {
            Location location = player.getEyeLocation();

            if (!location.getWorld().equals(getCenter().getWorld())) continue;
            if (getCenter().distanceSquared(location) > Math.pow(240, 2)) continue;

            face(location);

            main:
            for (int i = 0; i < particleFrequency; i++) {
                double x = rng.nextDouble(image.getWidth());
                double z = rng.nextDouble(image.getHeight());
                Object data = image.getRaster().getDataElements((int) x, (int) z, null);
                ColorModel model = image.getColorModel();

                if (model.hasAlpha() && model.getAlpha(data) == 0) {
                    i--;

                    continue;
                }

                int red = model.getRed(data);
                int green = model.getGreen(data);
                int blue = model.getBlue(data);

                for (int k = 0; k < ignoredColors.size(); k++) {
                    Color ignored = ignoredColors.get(k);

                    if (red > ignored.getRed() + fuzz || red < ignored.getRed() - fuzz) continue;
                    if (green > ignored.getGreen() + fuzz || green < ignored.getGreen() - fuzz) continue;
                    if (blue > ignored.getBlue() + fuzz || blue < ignored.getBlue() - fuzz) continue;

                    i--;

                    continue main;
                }
                
                locationHelper.zero().add(getCenter());
                vectorHelper.setX(((x / image.getWidth() * 2) - 1) * xRadius);
                vectorHelper.setY(0);
                vectorHelper.setZ(((z / image.getHeight() * 2) - 1) * -zRadius);
                rot.apply(vectorHelper);
                locationHelper.add(vectorHelper);

                ((CraftPlayer) player).getHandle().connection.send(
                        new ClientboundLevelParticlesPacket(
                                new DustParticleOptions(new Vector3f(red / 255F, green / 255F, blue / 255F), 1),
                                true, locationHelper.getX(), locationHelper.getY(), locationHelper.getZ(),
                                0, 0, 0, 1, 0
                        )
                );
            }
        }

        displaysThisFrame++;

        if (displaysThisFrame >= frameDelay) {
            displaysThisFrame = 0;
            frame++;
        }

        if (frame >= images.size()) {
            frame = 0;
        }
    }

    public void stop() {
        animator.cancel();
    }

    private void setImages(Object toLoad) {
        if (currentThread != null) {
            try {
                currentThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        images.clear();

        Thread thread = new Thread(() -> {
            try {
                ImageInputStream input = ImageIO.createImageInputStream(toLoad);
                ImageReader reader = ImageIO.getImageReaders(input).next();

                if (reader instanceof GIFImageReader) {
                    reader = new PatchedGIFImageReader(null);
                }

                reader.setInput(input);

                double imageAmount = reader.getNumImages(true);

                for (int i = 0; i < imageAmount; i++) {
                    BufferedImage image = reader.read(i);

                    images.add(image);

                    if (xRadius == 0 && zRadius == 0) {
                        if (image.getWidth() >= image.getHeight()) {
                            xRadius = 3 * ((double) image.getWidth() / image.getHeight());
                            zRadius = 3;
                        } else {
                            xRadius = 3;
                            zRadius = 3 * ((double) image.getHeight() / image.getWidth());
                        }
                    }
                }
            } catch (Throwable ex) {
                Bukkit.getServer().getLogger().log(Level.SEVERE, "Failed to load image from " + toLoad.toString(), ex);
            }
        });

        thread.start();

        currentThread = thread;
    }
    
    public void setImage(String link) {
        try {
            setImages(new URL(link).openStream());
        }  catch (Throwable ex) {
            Bukkit.getServer().getLogger().log(Level.SEVERE, "Failed to load image from " + link, ex);
        }
    }
    
    public void setImage(File path) {
        setImages(path);
    }

    public void addIgnoredColor(Color color) {
        ignoredColors.add(color);
    }

    public void removeIgnoredColor(int index) {
        ignoredColors.remove(index);
    }
    
    public void setParticleFrequency(int particleFrequency) {
        this.particleFrequency = particleFrequency;
    }

    public void setCenter(LocationSafe center) {
        Validate.notNull(center, "Location cannot be null!");
        Validate.notNull(center.getWorld(), "Location's world cannot be null!");

        locations.add(center);
        setWorld(center.getWorld());
        originalCentroid.zero().add(center);
        center.setChanged(true);

        if (locations.size() > 1) {
            locations.remove(0);
        }
    }
    
    public void setFuzz(int fuzz) {
        this.fuzz = fuzz;
    }
    
    public void setFrameDelay(int frameDelay) {
        this.frameDelay = frameDelay;
    }

    public Location getCenter() {
        return locations.get(0);
    }
}
