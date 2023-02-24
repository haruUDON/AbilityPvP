package haruudon.udon.abilitypvp.events;

import haruudon.udon.abilitypvp.GameMain;
import haruudon.udon.abilitypvp.GameRoomManager;
import haruudon.udon.abilitypvp.Scoreboard;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static haruudon.udon.abilitypvp.AbilityPvP.*;

public class JoinEvent implements Listener {
    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        String uuid = p.getUniqueId().toString();
        Location loc = (Location) getData("map").get("MainLobby.location");
        p.teleport(loc);
        p.getInventory().clear();
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setGameMode(GameMode.ADVENTURE);
        p.getInventory().setItem(0, getData("item").getItemStack("Custom"));
        p.getInventory().setItem(4, getData("item").getItemStack("Join"));
        p.getInventory().setItem(8, getData("item").getItemStack("Shop"));
        getData("player").set(uuid + ".name", p.getName());
        if (!getData("player").getBoolean(uuid + ".firstjoin")) {
            Set<String> template = getData("template").getKeys(true);
            for (String s : template){
                Object value = getData("template").get(s);
                if (value instanceof String s1){
                    getData("player").set(uuid + "." + s, s1);
                } else if (value instanceof Integer i){
                    getData("player").set(uuid + "." + s, i);
                } else if (value instanceof List<?>){
                    List<String> list = getData("template").getStringList(s);
                    getData("player").set(uuid + "." + s, list);
                } else if (value instanceof Boolean t){
                    getData("player").set(uuid + "." + s, t);
                }
            }
        }
        savePlayerData();
        Scoreboard.Create(p);
    }

    @EventHandler
    public void QuitEvent(PlayerQuitEvent e){
        Player p = e.getPlayer();
        org.bukkit.scoreboard.Scoreboard score = Bukkit.getScoreboardManager().getMainScoreboard();
        Team t = score.getTeam("hide");
        Objects.requireNonNull(GameRoomManager.getRoom(p)).removePlayer(p);
        Objects.requireNonNull(GameRoomManager.getRoom(p)).removeAlive(p);
        Objects.requireNonNull(GameRoomManager.getRoom(p)).removeSpectator(p);
        if (Objects.requireNonNull(GameRoomManager.getRoom(p)).getAlive().size() == 1) {
            GameMain.GameEnd("Normal", Objects.requireNonNull(GameRoomManager.getRoom(p)));
        }
        t.removeEntry(p.getName());
    }
}
