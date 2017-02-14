package me.phit.gmb;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;

@SideOnly(Side.CLIENT)
@Mod(
        modid = GiveMeBiomes.MOD_ID,
        name = GiveMeBiomes.MOD_NAME,
        version = GiveMeBiomes.VERSION
)
public class GiveMeBiomes {

    public static final String MOD_ID = "givemebiomes";
    public static final String MOD_NAME = "GiveMeBiomes";
    public static final String VERSION = "1.0-SNAPSHOT";

    public static File savepath;

    @Mod.Instance("GiveMeBiomes")
    public static GiveMeBiomes instance;

    @SideOnly(Side.CLIENT)
    @EventHandler
    public void load(FMLInitializationEvent event) {
        savepath = new File(Minecraft.getMinecraft().mcDataDir, "gmb");
    }

    @SideOnly(Side.CLIENT)
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new GMBCommand());
    }

}