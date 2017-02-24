package me.phit.gmb;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = GiveMeBiomes.MODID, name = GiveMeBiomes.MODNAME, version = GiveMeBiomes.MODVERSION, dependencies = "required-after:Forge@[11.16.0.1865,)", acceptableRemoteVersions = "*")
public class GiveMeBiomes {

    public static final String MODID = "givemebiomes";
    public static final String MODNAME = "GiveMeBiomes";
    public static final String MODVERSION = "2.0.1";

    public static File savepath;

    @Mod.Instance("GiveMeBiomes")
    public static GiveMeBiomes instance;

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        savepath = new File("." + File.separator + "gmb" + File.separator);
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent e) {
        e.registerServerCommand(new GMBCommand());
    }
}