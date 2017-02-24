package me.phit.gmb;

import com.google.common.io.ByteSink;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.TreeMap;

public class GMBCore {
    public static void generate(WorldChunkManager manager, String mapname, int scale, int radius, int originx, int originz, int width, int height, ICommandSender icommandsender) {
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

        icommandsender.addChatMessage(new ChatComponentText("Beginning render of map with 1:" + scale + " scale covering a " + radius * 2 + "x" + radius * 2 + " block area."));
        generateMap(manager, colors, scale, radius, originx, originz, width, height, path);

        writeAssets(gmbpath);

        ChatComponentText itextcomponent = new ChatComponentText("Success! Click here to open your map \"" + mapname + "\"");
        itextcomponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, FilenameUtils.normalize(gmbpath.getAbsolutePath() + File.separator + "index.html")));
        icommandsender.addChatMessage(itextcomponent);
    }

    private static void generateMap(WorldChunkManager manager, int[] colors, int scale, int radius, int originx, int originz, int width, int height, File path) {
        int[] pixels = new int[width * height];
        BiomeGenBase[] biomeTemp = new BiomeGenBase[1];

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                BiomeGenBase e = manager.getBiomesForGeneration(biomeTemp, originx - radius + x * scale, originz - radius + y * scale, 1, 1)[0];
                pixels[y * width + x] = colors[e.biomeID];
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

        for(int json = 0; json < 256; ++json) {
            if(BiomeGenBase.getBiomeGenArray()[json] != null) {
                int bcolor = BiomeGenBase.getBiomeGenArray()[json].color;
                String name = BiomeGenBase.getBiomeGenArray()[json].biomeName;
                clist[json] = bcolor;

                Color color = new Color(bcolor);
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                legend.put(name, red + "," + green + "," + blue);
            } else {
                clist[json] = 0;
            }
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
