package me.phit.gmb;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import static me.phit.gmb.BiomeColors.getMapColor;

public class GMBCore {

    public GMBCore() {
    }

    @SideOnly(Side.CLIENT)
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
        } catch (IOException var18) {
            var18.printStackTrace();
        }

        int[] var19 = generateLists(datapath);
        icommandsender.sendMessage(new TextComponentString("Beginning render of " + imagesx * imagesy + " tiles covering a " + width + " by " + height + " block area."));

        for(int y = 0; y < imagesy; ++y) {
            for(int x = 0; x < imagesx; ++x) {
                generateTile(manager, var19, scale, originx + x * 128 * scale, originz + y * 128 * scale, 128, 128, datapath, "tile_" + x + "_" + y);
                progress = (double)(y * imagesx + x) / (double)(imagesx * imagesy) * 100.0D;
                if(Math.floor(progress) > (double)lastpercent) {
                    lastpercent = (int)Math.floor(progress);
                    System.out.println("Progress: " + lastpercent + "%");
                }
            }
        }
        putViewer(path);
        ITextComponent itextcomponent = new TextComponentString("Success! Click here to open your map" + "\"" + mapname + "\"");
        itextcomponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, path.getAbsolutePath() + File.separator + "GMBViewer.htm"));
        icommandsender.sendMessage(itextcomponent);
    }

    @SideOnly(Side.CLIENT)
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

        BufferedImage var17 = new BufferedImage(width, height, 1);
        var17.setRGB(0, 0, width, height, pixels, 0, width);
        String var18 = (new Gson()).toJson(biomeids);

        try {
            ImageIO.write(var17, "png", new File(path, name + ".png"));
        } catch (IOException var16) {
            System.out.println("Image save failed");
        }

        try {
            FileWriter var19 = new FileWriter(new File(path, name + ".json"));
            var19.write("mapdata[\'" + name + "\'] = " + var18 + ";");
            var19.flush();
            var19.close();
        } catch (IOException var15) {
            var15.printStackTrace();
        }

    }

    @SideOnly(Side.CLIENT)
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

        String var7 = (new Gson()).toJson(namelist);
        if(!path.exists()) {
            path.mkdirs();
        }

        File filepath = new File(path, "biomeids.json");

        try {
            FileWriter e = new FileWriter(filepath);
            e.write("biomeids = " + var7);
            e.flush();
            e.close();
        } catch (IOException var6) {
            var6.printStackTrace();
        }

        return clist;
    }

    @SideOnly(Side.CLIENT)
    private static void putViewer(File path) {
        URL input = Minecraft.getMinecraft().getClass().getResource("/assets/givemebiomes/GMBViewer.htm");

        try {
            FileUtils.copyURLToFile(input, new File(path, "GMBViewer.htm"));
        } catch (IOException var3) {
            var3.printStackTrace();
        }
    }
}
