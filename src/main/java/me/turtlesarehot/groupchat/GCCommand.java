package me.turtlesarehot.groupchat;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;


public class GCCommand extends Command {
    // On a bungee (proxy) restart this list would be reset.
    ArrayList<GroupPlayer> groupMembers = new ArrayList<GroupPlayer>();
    ArrayList<Group> groups = new ArrayList<Group>();

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
                        createGroup(p);
                        break;
                    case "info":
                        //display group info if in one.
                        info(p);
                        break;
                    case "leave":
                        //if the player is in a group and not the leader they will leave the group.
                        leaveGroup(p);
                        break;
                    case "kick":
                        //If the player is the group leader they can kick a player with this.
                        break;
                    case "transfer":
                        //If the player is the group leader they can transfer leadership of the group to another person in the group.
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

    // Gets the GroupPlayer object for this user.
    public GroupPlayer getGroupPlayer(UUID player) {
        for(int i = 0; i < this.groupMembers.size(); i++) {
            if(this.groupMembers.get(i).getUUID().equals(player)) {
                return this.groupMembers.get(i);
            }
        }
        // If unable to find the groupplayer, return null.
        return null;
    }

    public GroupPlayer getGroupPlayer(String player) {
        for(int i = 0; i < this.groupMembers.size(); i++) {
            if(this.groupMembers.get(i).getUsername().equals(player)) {
                return this.groupMembers.get(i);
            }
        }
        // If unable to find the groupplayer, return null.
        return null;
    }

    // Adds a GroupPlayer to the groupmembers list. This is any player who is in a group. If the player logs off, they will still be in this list if they were in a group.
    public GroupPlayer addGroupPlayer(UUID player, String user) {
        GroupPlayer p = new GroupPlayer(player, user);
        this.groupMembers.add(p);
        return p;
    }

    // Removes a groupplayer to the groupmembers list. This is any player that isn't in a group.
    public void removeGroupPlayer(GroupPlayer gp) {
        Group gr = gp.getGroup();
        gr.removeMember(gp);
        editGroup(null, gr);
        gp.setGroup(null);
        gp.setToggle(false);
        this.groupMembers.remove(gp);
    }

    // Edits a groupplayer in the groupmembers list.
    public void editGroupPlayer(GroupPlayer newgp) {
        GroupPlayer egp = getGroupPlayer(newgp.getUUID());
        this.groupMembers.set(this.groupMembers.indexOf(egp), newgp);
    }

    // Edits a existing group
    public void editGroup(Group oldGroup, Group newGroup) {
        if(oldGroup == null) {
            this.groups.set(this.groups.indexOf(newGroup.getLeader().getGroup()), newGroup);
        }
        else {
            this.groups.set(this.groups.indexOf(oldGroup.getLeader().getGroup()), newGroup);
        }
    }

    // Returns the group the player is in, if they are not in one it returns null.
    public Group getPlayerGroup(ProxiedPlayer player) {
        GroupPlayer gplayer = getGroupPlayer(player.getUniqueId());
        if(gplayer == null) { return null; }
        for(int i = 0; i < groupMembers.size(); i++) {
            if(this.groupMembers.get(i).equals(player.getUniqueId())) {
                return this.groupMembers.get(i).getGroup();
            }
        }
        return null;
    }

    // Creates a new group
    public boolean createGroup(ProxiedPlayer player) {
        Group playerGroup = getPlayerGroup(player);
        if(playerGroup != null) {
            player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You are already in a group. If you want to make one, please leave the group you are in."));
            return false;
        }
        GroupPlayer you = addGroupPlayer(player.getUniqueId(), player.getDisplayName());
        Group g = new Group(you);
        you.setGroup(g);
        editGroupPlayer(you);
        groups.add(g);
        groupMembers.add(you);
        player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "Created the group! Use /gc invite <user> to invite people."));
        return true;

    }

    // If the player is in a group we list information about the group.
    public boolean info(ProxiedPlayer player) {
        // Checking to make sure the player who performed the command is in a group.
        Group playerGroup = getPlayerGroup(player);
        if(playerGroup == null) {
            player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You cannot list information about a group if you are not in a group."));
            return false;
        }

        //First we get all of the online users and store their uuids in a list.
        ArrayList<ProxiedPlayer> allPlayers = new ArrayList<ProxiedPlayer>();
        Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();
        for (Map.Entry<String, ServerInfo> entry : servers.entrySet()) {
            allPlayers.addAll(entry.getValue().getPlayers());
        }
        ArrayList<UUID> onlinePlayers = new ArrayList<UUID>();
        for(int i = 0; i < allPlayers.size(); i++) {
            onlinePlayers.add(allPlayers.get(i).getUniqueId());
        }

        // Now we list out all the information about the group (sepcifically the members and if they are online)
        player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "Group Info"));

        // First, check for the party leader
        if(playerGroup.getLeader().isOnline(onlinePlayers)) {
            player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.YELLOW + "LEADER " + playerGroup.getLeader().getUsername() + " - " + ChatColor.GREEN + "ONLINE"));
        }
        else {
            player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.YELLOW + "LEADER " + playerGroup.getLeader().getUsername() + " - " + ChatColor.RED + "OFFLINE"));
        }

        // Second, check for the rest of the members.
        for(int i = 0; i < playerGroup.getMembers().size(); i++) {
            GroupPlayer gp = playerGroup.getMembers().get(i);
            if(gp.isOnline(onlinePlayers)) {
                player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.YELLOW + "MEMBER " + gp.getUsername() + " - " + ChatColor.GREEN + "ONLINE"));
            }
            else {
                player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.YELLOW + "MEMBER " + gp.getUsername() + " - " + ChatColor.RED + "OFFLINE"));
            }
        }
        return true;
    }

    // Leaves the group you are in.
    public boolean leaveGroup(ProxiedPlayer player) {
        Group playerGroup = getPlayerGroup(player);
        if(playerGroup == null) {
            player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You cannot leave a group if you are not in one."));
            return false;
        }
        GroupPlayer gp = getGroupPlayer(player.getUniqueId());
        if(playerGroup.getLeader().equals(gp)) {
            player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You cannot leave the group if you are the leader. You must disband or transfer leadership."));
            return false;
        }
        removeGroupPlayer(gp);
        player.sendMessage(player.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You left the group!"));
        return true;
    }

    // Kick a user from the group
    public boolean kickPlayer(ProxiedPlayer sender, String otherPlayer) {
        Group playerGroup = getPlayerGroup(sender);
        if(playerGroup == null) {
            sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You cannot kick a player from a group if you aren't in a group."));
            return false;
        }
        if(!(playerGroup.getLeader().getUUID().equals(sender.getUniqueId()))) {
            sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You are not the leader of your group so you cannot kick members."));
            return false;
        }
        GroupPlayer gp = getGroupPlayer(otherPlayer);
        if(gp == null) {
            sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "This player isn't in a group or doesn't exist."));
            return false;
        }
        if(playerGroup != gp.getGroup()) {
            sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "This player isn't in a group with you."));
            return false;
        }
        playerGroup.sendMessage(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + playerGroup.getLeader().getUsername() + " kicked " + otherPlayer + " from the group.");
        removeGroupPlayer(gp);
        return true;
    }

    // Transfers leadership of the group to another player
    public boolean transferLeader(ProxiedPlayer sender, String otherPlayer) {
        Group playerGroup = getPlayerGroup(sender);
        if(playerGroup == null) {
            sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You cannot transfer leadership if you aren't in a group."));
            return false;
        }
        if(!(playerGroup.getLeader().getUUID().equals(sender.getUniqueId()))) {
            sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "You are not the leader of your group so you cannot transfer leadership."));
            return false;
        }
        GroupPlayer gp = getGroupPlayer(otherPlayer);
        if(gp == null) {
            sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "This player isn't in a group or isn't online."));
            return false;
        }
        if(gp.getGroup() != playerGroup) {
            sender.sendMessage(sender.getUniqueId(), new TextComponent(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "This player is not in a group with you."));
            return false;
        }
        playerGroup.setLeader(gp);
        playerGroup.addMember(getGroupPlayer(sender.getDisplayName()));
        editGroup(gp.getGroup(), playerGroup);
        playerGroup.sendMessage(ChatColor.GREEN + "[GroupChat] " + ChatColor.YELLOW + "The leader transfered leadership to " + otherPlayer);
        return true;
    }

    public boolean disband(ProxiedPlayer sender) {
        
        return true;
    }

}
