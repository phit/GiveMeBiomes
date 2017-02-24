package me.phit.gmb;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import org.apache.commons.lang3.StringUtils;

public class GMBCommand extends CommandBase {
    public int getRequiredPermissionLevel() {
        return 4;
    }

    public String getCommandName() {
        return "gmb";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "/gmb <mapname> [<scale> <radius>] [<centrex> <centrez>]";
    }

    public void processCommand(ICommandSender sender, String[] arguments) throws WrongUsageException {
        if (arguments.length == 0) {
            sender.addChatMessage(new ChatComponentText("Syntax: " + this.getCommandUsage(sender)));
        } else {
            ChunkCoordinates pos;
            if (arguments.length == 1) {
                pos = sender.getPlayerCoordinates();
                this.executeCommand(sender, arguments[0], 1, 512, pos.posX, pos.posZ);
            } else {
                if (arguments.length == 2) {
                    throw new WrongUsageException("Not enough arguments.", new Object[0]);
                }

                if (arguments.length == 3) {
                    if (!StringUtils.isNumeric(arguments[1]) || !StringUtils.isNumeric(arguments[2])) {
                        throw new WrongUsageException("Scale and radius have to be numeric!", new Object[0]);
                    }

                    if (arguments[1].equals("1") || arguments[1].equals("2")) {
                        pos = sender.getPlayerCoordinates();
                        this.executeCommand(sender, arguments[0], Integer.parseInt(arguments[1]), Integer.parseInt(arguments[2]), pos.posX, pos.posZ);
                    } else {
                        throw new WrongUsageException("Scale can only be 1 or 2!", new Object[0]);
                    }
                } else {
                    if (arguments.length == 4) {
                        throw new WrongUsageException("Not enough arguments.", new Object[0]);
                    }

                    if (arguments.length != 5) {
                        throw new WrongUsageException("Too many arguments!", new Object[0]);
                    }

                    if (!StringUtils.isNumeric(arguments[1]) && !StringUtils.isNumeric(arguments[2]) && !StringUtils.isNumeric(arguments[3]) && !StringUtils.isNumeric(arguments[4])) {
                        throw new WrongUsageException("Scale, radius and center coords have to be numeric!", new Object[0]);
                    }

                    if (arguments[1].equals("1") || arguments[1].equals("2")) {
                        this.executeCommand(sender, arguments[0], Integer.parseInt(arguments[1]), Integer.parseInt(arguments[2]), Integer.parseInt(arguments[3]), Integer.parseInt(arguments[4]));
                    } else {
                        throw new WrongUsageException("Scale can only be 1 or 2!", new Object[0]);
                    }
                }
            }
        }
    }

    private void executeCommand(ICommandSender sender, String name, int scale, int radius, int x, int z) throws WrongUsageException {
        if (name.length() == 0) {
            throw new WrongUsageException("Invalid map name.", new Object[0]);
        } else if (radius < 1 || scale < 1) {
            throw new WrongUsageException("Invalid radius or scale.", new Object[0]);
        } else {
            GMBCore.generate(sender.getEntityWorld().getWorldChunkManager(), name, scale, radius, x, z, radius * 2 / scale, radius * 2 / scale, sender);
        }
    }
}
