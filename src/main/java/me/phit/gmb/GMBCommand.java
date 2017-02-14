package me.phit.gmb;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;

public class GMBCommand extends CommandBase {
    private static final String defaultRadius = "512";

    public int getRequiredPermissionLevel() {
        return 0;
    }

    public String getName() {
        return "gmb";
    }

    public String getUsage(ICommandSender commandsender) {
        return "/gmb [ <scale> ] <mapname> [ <radius> ] [ <centrex> <centrez> ]";
    }

    @SideOnly(Side.CLIENT)
    public void execute(MinecraftServer server, ICommandSender commandsender, String[] astring) throws WrongUsageException {
        if (astring.length == 0) {
            commandsender.sendMessage(new TextComponentString("Give Me Biomes"));
            commandsender.sendMessage(new TextComponentString("Generates a biome map of a designated region. Defaults to 512 blocks radius and player coordinates."));
            commandsender.sendMessage(new TextComponentString("Syntax: " + this.getUsage(commandsender)));
        } else {
            BlockPos pos;
            if(!StringUtils.isNumeric(astring[0])) {
                if(astring.length == 1) {
                    pos = commandsender.getCommandSenderEntity().getPosition();
                    this.executeCommand(commandsender, astring[0], "1", "512", pos.getX() + "", pos.getZ() + "");
                } else if(astring.length == 2) {
                    pos = commandsender.getCommandSenderEntity().getPosition();
                    this.executeCommand(commandsender, astring[0], "1", astring[1], pos.getX() + "", pos.getZ() + "");
                } else if(astring.length == 3) {
                    this.executeCommand(commandsender, astring[0], "1", "512", astring[1], astring[2]);
                } else {
                    if(astring.length != 4) {
                        throw new WrongUsageException("Invalid arguments.", new Object[0]);
                    }

                    this.executeCommand(commandsender, astring[0], "1", astring[1], astring[2], astring[3]);
                }
            } else {
                if(astring.length < 2 || StringUtils.isNumeric(astring[1])) {
                    throw new WrongUsageException("Invalid arguments.", new Object[0]);
                }

                if(astring.length == 2) {
                    pos = commandsender.getCommandSenderEntity().getPosition();
                    this.executeCommand(commandsender, astring[1], astring[0], "512", pos.getX() + "", pos.getZ() + "");
                } else if(astring.length == 3) {
                    pos = commandsender.getCommandSenderEntity().getPosition();
                    this.executeCommand(commandsender, astring[1], astring[0], astring[2], pos.getX() + "", pos.getZ() + "");
                } else if(astring.length == 4) {
                    this.executeCommand(commandsender, astring[1], astring[0], "512", astring[2], astring[3]);
                } else {
                    if(astring.length != 5) {
                        throw new WrongUsageException("Invalid arguments.", new Object[0]);
                    }

                    this.executeCommand(commandsender, astring[1], astring[0], astring[2], astring[3], astring[4]);
                }
            }

        }
    }

    @SideOnly(Side.CLIENT)
    private void executeCommand(ICommandSender icommandsender, String name, String scale, String radius, String x, String z) throws WrongUsageException {
        if(name.length() == 0) {
            throw new WrongUsageException("Invalid map name.", new Object[0]);
        } else {
            int rad;
            int ix;
            int iz;
            int iscale;
            try {
                rad = Integer.parseInt(radius);
                ix = Integer.parseInt(x);
                iz = Integer.parseInt(z);
                iscale = Integer.parseInt(scale);
            } catch (NumberFormatException var12) {
                throw new WrongUsageException("Invalid number format. All numbers must be integers.", new Object[0]);
            }

            if(rad < 1) {
                throw new WrongUsageException("Invalid radius.", new Object[0]);
            } else {
                GMBCore.generate(icommandsender.getEntityWorld().getBiomeProvider(), name, iscale, ix - rad, iz - rad, rad * 2, rad * 2, icommandsender);
            }
        }
    }
}