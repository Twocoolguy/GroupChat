package me.turtlesarehot.groupchat;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.ChatColor;

public class GCCommand extends Command {

    public GCCommand() {
        super("gc");
    }

    public void sendMsg(ProxiedPlayer player, String message) {
        player.sendMessage(player.getUniqueId(), new TextComponent(message));
    }

    public boolean hasPermissionMessage(ProxiedPlayer player, String permission) {
        boolean perm = player.hasPermission(permission);
        if (player.hasPermission("groupchat.*")) {
            perm = true;
        }
        if (perm == false) {
            sendMsg(player, ChatColor.YELLOW + "You must have the permission node " + ChatColor.GREEN + permission + ChatColor.YELLOW + " in order to perform this command.");
        }
        return perm;
    }

    public void execute(CommandSender sender, String[] args) {
        if ((sender instanceof ProxiedPlayer)) {
            ProxiedPlayer p = (ProxiedPlayer) sender;
            if (args.length > 0) {
                String subCommand = args[0].toLowerCase();
                switch (subCommand) {
                    case "create":
                        //create group
                        GroupCommand.createGroup(p);
                        break;
                    case "info":
                        //display group info if in one.
                        GroupCommand.info(p);
                        break;
                    case "leave":
                        //if the player is in a group and not the leader they will leave the group.
                        GroupCommand.leaveGroup(p);
                        break;
                    case "kick":
                        //If the player is the group leader they can kick a player with this.
                        if(args.length < 2) {
                            sendMsg(p, ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "Please provide a name.");
                            break;
                        }
                        GroupCommand.kickPlayer(p, args[1]);
                        break;
                    case "transfer":
                        //If the player is the group leader they can transfer leadership of the group to another person in the group.
                        if(args.length < 2) {
                            sendMsg(p, ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "Please provide a name.");
                            break;
                        }
                        GroupCommand.transferLeader(p, args[1]);
                        break;
                    case "disband":
                        //If the player is the group leader they can disband the group.
                        break;
                    case "chat":
                        //If the player is in a group they can use this to send a message.
                        break;
                    case "togglechat":
                        //If the player is in a group they can use this command to toggle sending messages in the group chat without typing the command.
                        break;
                    case "invite":
                        //If the player is the group leader they can attempt to invite players to their group.
                        break;
                    case "help":
                        //display help about groupchat commands.
                        sendMsg(p, ChatColor.GREEN + "" + ChatColor.BOLD + "--GroupChat Commands--");
                        sendMsg(p, ChatColor.GREEN + "<> = required argument, [] = optional argument");
                        sendMsg(p, ChatColor.GREEN + "/gc create " + ChatColor.YELLOW + "- creates a group chat.");
                        sendMsg(p, ChatColor.GREEN + "/gc info " + ChatColor.YELLOW + "- displays all users in the group and if they are online or not.");
                        sendMsg(p, ChatColor.GREEN + "/gc leave " + ChatColor.YELLOW + "- Leaves the group.");
                        sendMsg(p, ChatColor.GREEN + "/gc kick <user> " + ChatColor.YELLOW + "- This kicks users from the group.");
                        sendMsg(p, ChatColor.GREEN + "/gc transfer <user> " + ChatColor.YELLOW + "- This transfers leadership of the group.");
                        sendMsg(p, ChatColor.GREEN + "/gc disband " + ChatColor.YELLOW + "- If you are the group leader, you can disband the group with this command.");
                        sendMsg(p, ChatColor.GREEN + "/gc chat <message> " + ChatColor.YELLOW + "- This is the command to type a message into a group chat.");
                        sendMsg(p, ChatColor.GREEN + "/gc togglechat " + ChatColor.YELLOW + "- You can toggle on and off auto typing into the group chat.");
                        sendMsg(p, ChatColor.GREEN + "/gc invite <user> " + ChatColor.YELLOW + "- This attempts to invite someone to your group.");
                        sendMsg(p, ChatColor.GREEN + "/gc help " + ChatColor.YELLOW + "- This displays all of the commands and what they do.");
                        break;
                    default:
                        sendMsg(p, ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "Invalid command. Please type /gc help to list all of the commands.");
                        break;
                }
            } else {
                p.sendMessage(p.getUniqueId(), new TextComponent(ChatColor.GREEN + "" + ChatColor.BOLD + "GroupChat " + ChatColor.YELLOW + "v1.0"));
                p.sendMessage(p.getUniqueId(), new TextComponent(ChatColor.GREEN + "Created By: " + ChatColor.YELLOW + "TurtlesAreHot"));
                p.sendMessage(p.getUniqueId(), new TextComponent(ChatColor.GREEN + "To list all of the commands, type " + ChatColor.YELLOW + "/gc help"));
            }
        }
    }


}
