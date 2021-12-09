package net.iceblaze.ScoreboardTeamColor;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.awt.*;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScoreboardTeamColor extends JavaPlugin implements Listener {
    private final Pattern colorPattern = Pattern.compile("&\\{(#[0-9a-f]{3})}|&\\{(#[0-9a-f]{6})}", Pattern.CASE_INSENSITIVE);
    private String chatFormat;

    public void onEnable(){
        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        chatFormat = ChatColor.translateAlternateColorCodes('&', convertColors(Objects.requireNonNull(getConfig().getString("use.format"))
                .replace("%NAME%", "%1$s")
                .replace("%MESSAGE%", "%2$s")));
    }

    String convertColors(String input) {
        StringBuilder output = new StringBuilder();
        Matcher matcher = colorPattern.matcher(input);
        while (matcher.find()) {
            String rep = String.valueOf(ChatColor.of(Color.decode(matcher.group(1) == null ? matcher.group(2): matcher.group(1))));
            matcher.appendReplacement(output, rep);
        }
        matcher.appendTail(output);
        return output.toString();
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

        if(chatFormat.length() > 0){
            e.setFormat(chatFormat);
        }
    }
}
