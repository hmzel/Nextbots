package me.zelha.nextbots.nextbot;

import hm.zelha.particlesfx.particles.ParticleDust;
import hm.zelha.particlesfx.particles.parents.ColorableParticle;
import hm.zelha.particlesfx.shapers.ParticleImage;
import hm.zelha.particlesfx.util.Color;
import hm.zelha.particlesfx.util.LocationSafe;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;

public class NextbotDisplay extends ParticleImage {
    public NextbotDisplay(LocationSafe center, String link) {
        super(new ParticleDust(), center, link, 5, 2000);
    }

    public NextbotDisplay(LocationSafe center, File path) {
        super(new ParticleDust(), center, path, 5, 2000);
    }

    @Override
    public void display() {
        if (frame >= images.size()) {
            frame = 0;

            return;
        }

        BufferedImage image = images.get(frame);

        for (Player player : Bukkit.getOnlinePlayers()) {
            Location location = player.getEyeLocation();

            if (getCenter().distanceSquared(location) > Math.pow(240, 2)) continue;

            face(location);

            if (rot.getPitch() > -90) {
                rot.setPitch(-90);
            }

            main:
            for (int i = 0; i < particleFrequency; i++) {
                ColorableParticle particle = (ColorableParticle) getCurrentParticle();
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

                particle.setColor(red, green, blue);
                locationHelper.zero().add(getCenter());
                vectorHelper.setX(((x / image.getWidth() * 2) - 1) * xRadius);
                vectorHelper.setY(0);
                vectorHelper.setZ(((z / image.getHeight() * 2) - 1) * -zRadius);
                rot.apply(vectorHelper);
                locationHelper.add(vectorHelper);

                particle.displayForPlayers(locationHelper, player);
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
}
