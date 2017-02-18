package me.phit.gmb;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.lang3.StringUtils;

public class GMBCommand extends CommandBase {
    public int getRequiredPermissionLevel() {
        return 0;
    }

    public String getName() {
        return "gmb";
    }

    public String getUsage(ICommandSender sender) {
        return "/gmb <mapname> [<scale> <radius>] [<centrex> <centrez>]";
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] arguments) throws WrongUsageException {
        if (arguments.length == 0) {
            sender.sendMessage(new TextComponentString("Syntax: " + this.getUsage(sender)));
        } else {
            BlockPos pos;
            if(arguments.length == 1) { //gmb name
                pos = sender.getCommandSenderEntity().getPosition();
                this.executeCommand(sender, arguments[0], 1, 512, pos.getX(), pos.getZ());
            } else if(arguments.length == 2) {
                throw new WrongUsageException("Not enough arguments.", new Object[0]);
            } else if(arguments.length == 3) {
                if(StringUtils.isNumeric(arguments[1]) && StringUtils.isNumeric(arguments[2])) {
                    pos = sender.getCommandSenderEntity().getPosition();
                    this.executeCommand(sender, arguments[0], Integer.parseInt(arguments[1]), Integer.parseInt(arguments[2]), pos.getX(), pos.getZ());
                } else {
                    throw new WrongUsageException("Scale and radius have to be numeric!", new Object[0]);
                }
            } else if(arguments.length == 4) {
                throw new WrongUsageException("Not enough arguments.", new Object[0]);
            } else {
                if(arguments.length != 5) {
                    throw new WrongUsageException("Too many arguments!", new Object[0]);
                }
                if(!StringUtils.isNumeric(arguments[1]) && !StringUtils.isNumeric(arguments[2]) && !StringUtils.isNumeric(arguments[3]) && !StringUtils.isNumeric(arguments[4])) {
                    throw new WrongUsageException("Scale, radius and center coords have to be numeric!", new Object[0]);
                }
                this.executeCommand(sender, arguments[0], Integer.parseInt(arguments[1]), Integer.parseInt(arguments[2]), Integer.parseInt(arguments[3]), Integer.parseInt(arguments[4]));
            }
        }
    }

    private void executeCommand(ICommandSender sender, String name, int scale, int radius, int x, int z) throws WrongUsageException {
        if(name.length() == 0) {
            throw new WrongUsageException("Invalid map name.", new Object[0]);
        } else {
            if(radius < 1) {
                throw new WrongUsageException("Invalid radius.", new Object[0]);
            } else {
                GMBCore.generate(sender.getEntityWorld().getBiomeProvider(), name, scale, x - radius, z - radius, radius * 2, radius * 2, sender);
            }
        }
    }
}