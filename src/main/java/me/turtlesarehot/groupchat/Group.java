package me.turtlesarehot.groupchat;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;


public class Group {

    private UUID leader;
    private ArrayList<UUID> members;
    private int id;

    public Group(UUID leader, int id) {
        this.leader = leader;
        this.members = new ArrayList<UUID>();
        this.id = id;
    }

    // Returns the UUID of the leader of the group.
    public UUID getLeader() { return this.leader; }

    // Returns the UUIDs of the members of the group (doesn't include leader).
    public ArrayList<UUID> getMembers() { return this.members; }

    // Gets the group ID.
    public int getId() { return this.id; }

    // Returns the total number of members
    public int memberCount() { return this.members.size() + 1; }

    // Sets the leader to this new one.
    public void setLeader(UUID leader) {
        this.leader = leader;
    }


    // Adds a member to the group.
    public void addMember(UUID member) { this.members.add(member); }

    // Removes a member from the group.
    public void removeMember(GroupPlayer member) {
        this.members.remove(member);
    }

    // Sending Group Messages to everyone online
    public void sendMessage(String message) {
        // Gets all online players
        ArrayList<ProxiedPlayer> allPlayers = new ArrayList<ProxiedPlayer>();
        Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();
        for (Map.Entry<String, ServerInfo> entry : servers.entrySet()) {
            allPlayers.addAll(entry.getValue().getPlayers());
        }
        for(int i = 0; i < allPlayers.size(); i++) {
            if(this.members.contains(allPlayers.get(i).getUniqueId())) {
                allPlayers.get(i).sendMessage(allPlayers.get(i).getUniqueId(), new TextComponent(message));
            }
        }
    }

}

