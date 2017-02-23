package me.phit.gmb;

import com.google.common.io.ByteSink;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.TreeMap;
import javax.imageio.ImageIO;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import org.apache.commons.io.FilenameUtils;

public class GMBCore {
    public static void generate(BiomeProvider manager, String mapname, int scale, int radius, int originx, int originz, int width, int height, ICommandSender icommandsender) {
        File gmbpath = new File(GiveMeBiomes.savepath, mapname);
        File path = new File(gmbpath, "data");
        if(!path.exists()) {
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
        generateMap(manager, colors, scale, radius, originx, originz, width, height, path);

        writeAssets(gmbpath);

        TextComponentString itextcomponent = new TextComponentString("Success! Click here to open your map \"" + mapname + "\"");
        itextcomponent.getStyle().setClickEvent(new ClickEvent(Action.OPEN_FILE, FilenameUtils.normalize(gmbpath.getAbsolutePath() + File.separator + "index.html")));
        icommandsender.sendMessage(itextcomponent);
    }

    private static void generateMap(BiomeProvider manager, int[] colors, int scale, int radius, int originx, int originz, int width, int height, File path) {
        int[] pixels = new int[width * height];
        Biome[] biomeTemp = new Biome[1];

        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                Biome e = manager.getBiomesForGeneration(biomeTemp, originx - radius + x * scale, originz - radius + y * scale, 1, 1)[0];
                pixels[y * width + x] = colors[Biome.getIdForBiome(e)];
            }
        }

        BufferedImage img = new BufferedImage(width, height, 1);
        img.setRGB(0, 0, width, height, pixels, 0, width);

        try {
            ImageIO.write(img, "png", new File(path, "map_0_0.png"));
        } catch (IOException err) {
            Logging.logError("Image save failed", new Object[]{err});
        }
    }

    private static int[] generateColors(File path) {
        int[] clist = new int[512];
        TreeMap legend = new TreeMap();
        Iterator iter = Biome.REGISTRY.iterator();

        while(iter.hasNext()) {
            Biome biome = (Biome)iter.next();
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
        if(!path.exists()) {
            path.mkdirs();
        }

        writeFile(legendjson, path, "legend.json");
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
            Logging.logError("Couldn\'t write: " + filename, new Object[]{err});
        }

    }

    private static void writeAssets(File path) {
        File assetsp = new File(path, "assets");
        if(!assetsp.exists()) {
            assetsp.mkdirs();
        }

        File imagesp = new File(assetsp, "images");
        if(!imagesp.exists()) {
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
            Logging.logError("Couldn\'t write asset: " + filename, new Object[]{err});
        }

    }
}
