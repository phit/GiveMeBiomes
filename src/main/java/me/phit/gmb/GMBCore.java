package me.phit.gmb;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;
import javax.imageio.ImageIO;
import com.google.common.io.ByteSink;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.io.FilenameUtils;

import static me.phit.gmb.BiomeColors.getMapColor;

public class GMBCore {

    public static void generate(BiomeProvider manager, String mapname, int scale, int originx, int originz, int width, int height, ICommandSender icommandsender) {
        int imagesx = (int)Math.ceil((double)width / (128.0D * (double)scale));
        int imagesy = (int)Math.ceil((double)height / (128.0D * (double)scale));
        double progress = 0.0D;
        int lastpercent = 0;
        File path = new File(GiveMeBiomes.savepath, mapname);
        File datapath = new File(path, "data");
        if(!datapath.exists()) {
            datapath.mkdirs();
        }

        JsonObject info = new JsonObject();
        info.addProperty("tileSize", Integer.valueOf(128));
        info.addProperty("tilesx", Integer.valueOf(imagesx));
        info.addProperty("tilesy", Integer.valueOf(imagesy));
        info.addProperty("originx", Integer.valueOf(originx));
        info.addProperty("originy", Integer.valueOf(originz));
        info.addProperty("scale", Integer.valueOf(scale));

        try {
            FileWriter colours = new FileWriter(new File(datapath, "mapinfo.json"));
            colours.write("mapinfo = " + info.toString());
            colours.flush();
            colours.close();
        } catch (IOException err) {
            Logging.logError("Couldn\'t write mapinfo.json!", err);
        }

        int[] colours = generateLists(datapath);
        icommandsender.sendMessage(new TextComponentString("Beginning render of " + imagesx * imagesy + " tiles covering a " + width + " by " + height + " block area."));

        for(int y = 0; y < imagesy; ++y) {
            for(int x = 0; x < imagesx; ++x) {
                generateTile(manager, colours, scale, originx + x * 128 * scale, originz + y * 128 * scale, 128, 128, datapath, "tile_" + x + "_" + y);
                progress = (double)(y * imagesx + x) / (double)(imagesx * imagesy) * 100.0D;
                if(Math.floor(progress) > (double)lastpercent) {
                    lastpercent = (int)Math.floor(progress);
                    Logging.logInfo("Progress: " + lastpercent + "%");
                }
            }
        }
        putViewer(path);
        ITextComponent itextcomponent = new TextComponentString("Success! Click here to open your map " + "\"" + mapname + "\"");
        itextcomponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, FilenameUtils.normalize(path.getAbsolutePath() + File.separator + "GMBViewer.htm")));
        icommandsender.sendMessage(itextcomponent);
    }

    private static void generateTile(BiomeProvider manager, int[] colours, int scale, int originx, int originz, int width, int height, File path, String name) {
        int[] pixels = new int[width * height];
        int[] biomeids = new int[width * height];
        Biome[] biometemp = new Biome[1];

        for(int img = 0; img < height; ++img) {
            for(int json = 0; json < width; ++json) {
                Biome e = manager.getBiomesForGeneration(biometemp, originx + json * scale, originz + img * scale, 1, 1)[0];
                pixels[img * width + json] = colours[e.getIdForBiome(e)];
                biomeids[img * width + json] = e.getIdForBiome(e);
            }
        }

        BufferedImage img = new BufferedImage(width, height, 1);
        img.setRGB(0, 0, width, height, pixels, 0, width);
        String biomeidsjson = (new Gson()).toJson(biomeids);

        try {
            ImageIO.write(img, "png", new File(path, name + ".png"));
        } catch (IOException err) {
            Logging.logError("Image save failed", err);
        }

        try {
            FileWriter idsfile = new FileWriter(new File(path, name + ".json"));
            idsfile.write("mapdata[\'" + name + "\'] = " + biomeidsjson + ";");
            idsfile.flush();
            idsfile.close();
        } catch (IOException err) {
            Logging.logError("Couldn\'t write mapdata json for tile" + name, err);
        }

    }

    private static int[] generateLists(File path) {
        int[] clist = new int[512];
        String[] namelist = new String[512];

        Iterator<Biome> iter = Biome.REGISTRY.iterator();
        while (iter.hasNext())
        {
            Biome biome = iter.next();
            int id = Biome.getIdForBiome(biome);
            String name = biome.getBiomeName();

            int bcolor = getMapColor(name);

            clist[id] = bcolor;
            namelist[id] = name;
        }

        String namelistjson = (new Gson()).toJson(namelist);
        if(!path.exists()) {
            path.mkdirs();
        }

        File filepath = new File(path, "biomeids.json");

        try {
            FileWriter e = new FileWriter(filepath);
            e.write("biomeids = " + namelistjson);
            e.flush();
            e.close();
        } catch (IOException err) {
            Logging.logError("Couldn\'t write biomeids.json", err);
            return null;
        }
        return clist;
    }

    public static File putViewer(File path) {
        try {
            final File t = new File(path, "GMBViewer.htm");
            String htmlPath = "/assets/givemebiomes/GMBViewer.htm";
            InputStream inputStream = GiveMeBiomes.class.getResource(htmlPath).openStream();
            ByteSink out = new ByteSink() {
                public OutputStream openStream() throws IOException {
                    return new FileOutputStream(t);
                }
            };
            out.writeFrom(inputStream);
            return t;
        } catch (Throwable err) {
            Logging.logError("Couldn\'t copy viewer html!", err);
            return null;
        }
    }
}
