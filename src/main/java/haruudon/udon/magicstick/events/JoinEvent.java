package haruudon.udon.magicstick.events;

import haruudon.udon.magicstick.Join;
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
        final Location loc = (Location) getMapData().get("MainLobby.location");
        p.teleport(loc);
        p.getInventory().clear();
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setGameMode(GameMode.SURVIVAL);
        p.getInventory().setItem(0, getItem().getItemStack("Custom"));
        p.getInventory().setItem(4, getItem().getItemStack("Join"));
        p.getInventory().setItem(8, getItem().getItemStack("Shop"));
        getPlayerData().set(p.getUniqueId().toString() + ".name", p.getName());
        if (!getPlayerData().getBoolean(p.getUniqueId().toString() + ".firstjoin")) {
            getPlayerData().set(p.getUniqueId().toString() + ".firstjoin", true);
            getPlayerData().set(p.getUniqueId().toString() + ".coin", 0);
            getPlayerData().set(p.getUniqueId().toString() + ".magicore", 0);
            getPlayerData().set(p.getUniqueId().toString() + ".customkit.select", "null");
            for (int i = 0; i < 3; i++){
                List<String> list = new ArrayList<>();
                list.add("first");
                list.add("second");
                list.add("third");
                String[] listOfType = {"NullType", "NullType"};
                String[] listOfAbility = {"NullAbility", "NullAbility", "NullAbility", "NullAbility"};
                getPlayerData().set(p.getUniqueId().toString() + ".customkit." + list.get(i) + ".name", "§fカスタムキット" + (i + 1));
                getPlayerData().set(p.getUniqueId().toString() + ".customkit." + list.get(i) + ".use", null);
                getPlayerData().set(p.getUniqueId().toString() + ".customkit." + list.get(i) + ".cost", 0);
                getPlayerData().set(p.getUniqueId().toString() + ".customkit." + list.get(i) + ".type", Arrays.asList(listOfType));
                getPlayerData().set(p.getUniqueId().toString() + ".customkit." + list.get(i) + ".ability", Arrays.asList(listOfAbility));
                getPlayerData().set(p.getUniqueId().toString() + ".customkit." + list.get(i) + ".weapon", "NullWeapon");
            }
        }
        savePlayerData();
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
