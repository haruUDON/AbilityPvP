package haruudon.udon.magicstick.events;

import haruudon.udon.magicstick.Join;
import haruudon.udon.magicstick.Scoreboard;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static haruudon.udon.magicstick.MagicStick.*;
import static haruudon.udon.magicstick.Join.Alive;

public class JoinEvent implements Listener {
    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        final Location loc = (Location) getData("map").get("MainLobby.location");
        p.teleport(loc);
        p.getInventory().clear();
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setGameMode(GameMode.SURVIVAL);
        p.getInventory().setItem(0, getData("item").getItemStack("Custom"));
        p.getInventory().setItem(4, getData("item").getItemStack("Join"));
        p.getInventory().setItem(8, getData("item").getItemStack("Shop"));
        getData("player").set(p.getUniqueId().toString() + ".name", p.getName());
        if (!getData("player").getBoolean(p.getUniqueId().toString() + ".firstjoin")) {
            String[] listOfKilleffect = {"NullEffect"};
            getData("player").set(p.getUniqueId().toString() + ".firstjoin", true);
            getData("player").set(p.getUniqueId().toString() + ".coin", 0);
            getData("player").set(p.getUniqueId().toString() + ".magicore", 0);
            getData("player").set(p.getUniqueId().toString() + ".magicdust", 0);
            getData("player").set(p.getUniqueId().toString() + ".killcount", 0);
            getData("player").set(p.getUniqueId().toString() + ".wincount", 0);
            getData("player").set(p.getUniqueId().toString() + ".customkit.select", "null");
            getData("player").set(p.getUniqueId().toString() + ".killeffect", "NullEffect");
            getData("player").set(p.getUniqueId().toString() + ".have.killeffect", Arrays.asList(listOfKilleffect));
            for (int i = 0; i < 3; i++){
                List<String> list = new ArrayList<>();
                list.add("first");
                list.add("second");
                list.add("third");
                String[] listOfType = {"NullType", "NullType"};
                String[] listOfAbility = {"NullAbility", "NullAbility", "NullAbility", "NullAbility"};
                getData("player").set(p.getUniqueId().toString() + ".customkit." + list.get(i) + ".name", "§fカスタムキット" + (i + 1));
                getData("player").set(p.getUniqueId().toString() + ".customkit." + list.get(i) + ".use", null);
                getData("player").set(p.getUniqueId().toString() + ".customkit." + list.get(i) + ".cost", 0);
                getData("player").set(p.getUniqueId().toString() + ".customkit." + list.get(i) + ".type", Arrays.asList(listOfType));
                getData("player").set(p.getUniqueId().toString() + ".customkit." + list.get(i) + ".ability", Arrays.asList(listOfAbility));
                getData("player").set(p.getUniqueId().toString() + ".customkit." + list.get(i) + ".weapon", "NullWeapon");
            }
        }
        savePlayerData();
        Scoreboard.Create(p);
    }

    @EventHandler
    public void QuitEvent(PlayerQuitEvent e){
        Player p = e.getPlayer();
        Join.JoinPlayer.remove(p);
        Join.GamePlayer.remove(p);
        Join.Spectator.remove(p);
        Join.Alive.remove(p);
        if (Alive.size() == 1) {
            Join.GameEnd("Normal");
        }
    }
}
