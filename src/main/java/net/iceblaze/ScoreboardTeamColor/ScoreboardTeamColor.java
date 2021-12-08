package net.iceblaze.ScoreboardTeamColor;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

public class ScoreboardTeamColor extends JavaPlugin implements Listener {
    public void onEnable(){
        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Team team = e.getPlayer().getScoreboard().getEntryTeam(e.getPlayer().getName());
        if(team == null) return;

        //Color name
        String displayName = e.getPlayer().getName();
        if(getConfig().getBoolean("use.color")) displayName = team.getColor() + displayName;

        //Add appropriate resets around name
        if(getConfig().getBoolean("reset.beforeName"))
            displayName = ChatColor.RESET + displayName;
        if(getConfig().getBoolean("reset.afterName"))
            displayName = displayName + ChatColor.RESET;

        //Add prefix and appropriate reset
        if(team.getPrefix().length() != 0 && getConfig().getBoolean("use.prefix")){
            displayName = team.getPrefix() + getConfig().getString("separators.prefix") + displayName;
            if(getConfig().getBoolean("reset.beforePrefix"))
                displayName = ChatColor.RESET + displayName;
        }

        //Add suffix and appropriate reset
        if(team.getSuffix().length() != 0 && getConfig().getBoolean("use.suffix")) {
            displayName += getConfig().getString("separators.suffix") + team.getSuffix();
            if(getConfig().getBoolean("reset.afterSuffix"))
                displayName = displayName + ChatColor.RESET;
        }

        //Update display name
        e.getPlayer().setDisplayName(displayName);
    }
}
