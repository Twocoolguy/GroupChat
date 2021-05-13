package me.turtlesarehot.groupchat;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;


public class Group {

    private GroupPlayer leader;
    private ArrayList<GroupPlayer> members;
    private int id;

    public Group(GroupPlayer leader, int id) {
        this.leader = leader;
        this.members = new ArrayList<GroupPlayer>();
        this.id = id;
    }

    // Returns the UUID of the leader of the group.
    public GroupPlayer getLeader() { return this.leader; }

    // Returns the UUIDs of the members of the group (doesn't include leader).
    public ArrayList<GroupPlayer> getMembers() { return this.members; }

    // Gets the group ID.
    public int getId() { return this.id; }

    // Returns the total number of members
    public int memberCount() { return this.members.size() + 1; }

    // Sets the leader to this new one.
    public void setLeader(GroupPlayer leader) {
        this.leader = leader;
    }


    // Adds a member to the group.
    public void addMember(GroupPlayer member) {
        this.members.add(member);
    }

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
        ArrayList<UUID> groupUUIDs = new ArrayList<UUID>();
        groupUUIDs.add(this.leader.getUUID());
        for (int h = 0; h < this.members.size(); h++) {
            groupUUIDs.add(this.members.get(h).getUUID());
        }
        for(int i = 0; i < allPlayers.size(); i++) {
            if(groupUUIDs.contains(allPlayers.get(i).getUniqueId())) {
                allPlayers.get(i).sendMessage(allPlayers.get(i).getUniqueId(), new TextComponent(message));
            }
        }
    }

}

