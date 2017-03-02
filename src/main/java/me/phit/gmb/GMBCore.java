package me.phit.gmb;

import com.google.common.io.ByteSink;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;
import java.util.TreeMap;

public class GMBCore {
    public static void generate(BiomeProvider provider, String mapname, int scale, int radius, int originx, int originz, int width, int height, ICommandSender icommandsender) {
        File gmbpath = new File(GiveMeBiomes.savepath, mapname);
        File path = new File(gmbpath, "data");
        if (!path.exists()) {
            path.mkdirs();
        }

        JsonObject info = new JsonObject();
        info.addProperty("width", width);
        info.addProperty("height", height);
        info.addProperty("originx", originx);
        info.addProperty("originy", originz);
        info.addProperty("scale", scale);
        info.addProperty("radius", radius);

        writeFile("mapinfo = " + info.toString(), path, "mapinfo.json");
        int[] colors = generateColors(path);

        icommandsender.sendMessage(new TextComponentString("Beginning render of map with 1:" + scale + " scale covering a " + radius * 2 + "x" + radius * 2 + " block area."));

        generateMap(provider, colors, scale, radius, originx, originz, width, height, path);

        writeAssets(gmbpath);

        TextComponentString itextcomponent = new TextComponentString("Success! Click here to open your map \"" + mapname + "\"");
        itextcomponent.getStyle().setClickEvent(new ClickEvent(Action.OPEN_FILE, FilenameUtils.normalize(gmbpath.getAbsolutePath() + File.separator + "index.html")));
        icommandsender.sendMessage(itextcomponent);
    }

    private static void generateMap(BiomeProvider provider, int[] colors, int scale, int radius, int originx, int originz, int width, int height, File path) {
        int[] pixels = new int[width * height];
        Biome[] biome = new Biome[1];
        int lastpercent = 0;

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                provider.getBiomes(biome, originx - radius + x * scale, originz - radius + y * scale, 1, 1);
                pixels[y * width + x] = colors[Biome.getIdForBiome(biome[0])];

                double progress = (double)(y * width + x) / (double)(height * width) * 100.0D;
                if(Math.floor(progress) > (double)lastpercent) {
                    lastpercent = (int)Math.floor(progress);
                    Logging.logInfo("Progress: " + lastpercent + "%");
                }
            }
        }

        BufferedImage img = new BufferedImage(width, height, 1);
        img.setRGB(0, 0, width, height, pixels, 0, width);

        try {
            ImageIO.write(img, "png", new File(path, "map_0_0.png"));
        } catch (IOException err) {
            Logging.logError("Saving image failed!", new Object[]{err});
        }
    }

    private static int[] generateColors(File path) {
        int[] clist = new int[512];
        TreeMap legend = new TreeMap();
        Iterator iter = Biome.REGISTRY.iterator();

        while (iter.hasNext()) {
            Biome biome = (Biome) iter.next();
            String name = biome.getBiomeName();

            int biomeid = Biome.getIdForBiome(biome);
            int bcolor = BiomeColors.getMapColor(name);

            clist[biomeid] = bcolor;

            Color color = new Color(bcolor);
            int red = color.getRed();
            int green = color.getGreen();
            int blue = color.getBlue();

            legend.put(name, red + "," + green + "," + blue);
        }

        String legendjson = (new Gson()).toJson(legend);
        if (!path.exists()) {
            path.mkdirs();
        }

        writeFile("legend=" + legendjson, path, "legend.json");
        return clist;
    }

    private static void writeFile(String data, File path, String filename) {
        try {
            File err = new File(path, filename);
            FileWriter f = new FileWriter(err);
            f.write(data);
            f.flush();
            f.close();
        } catch (IOException err) {
            Logging.logError("Couldn't write: " + filename, new Object[]{err});
        }

    }

    private static void writeAssets(File path) {
        /* first mkdirs is redundant since the second folder is deeper, but keeping it for clarity */
        File assetsp = new File(path, "assets");
        if (!assetsp.exists()) {
            assetsp.mkdirs();
        }

        File imagesp = new File(assetsp, "images");
        if (!imagesp.exists()) {
            imagesp.mkdirs();
        }

        writeAsset(path, "index.html");
        writeAsset(path, "assets/style.css");
        writeAsset(path, "assets/leaflet-1.0.3.css");
        writeAsset(path, "assets/leaflet-1.0.3.js");
        writeAsset(path, "assets/L.Control.MousePosition.js");
        writeAsset(path, "assets/L.ImageOverlay.PixelFilter.js");
        writeAsset(path, "assets/images/layers.png");
        writeAsset(path, "assets/images/layers-2x.png");
        writeAsset(path, "assets/images/marker-icon.png");
        writeAsset(path, "assets/images/marker-icon-2x.png");
        writeAsset(path, "assets/images/marker-shadow.png");
    }

    private static void writeAsset(File path, String filename) {
        try {
            final File asset = new File(path, filename);
            InputStream inputStream = GiveMeBiomes.class.getResource("/assets/givemebiomes/" + filename).openStream();
            ByteSink out = new ByteSink() {
                public OutputStream openStream() throws IOException {
                    return new FileOutputStream(asset);
                }
            };
            out.writeFrom(inputStream);
        } catch (Throwable err) {
            Logging.logError("Couldn't write asset: " + filename, new Object[]{err});
        }

    }
}
