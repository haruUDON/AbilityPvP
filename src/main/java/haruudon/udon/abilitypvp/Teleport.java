package haruudon.udon.abilitypvp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Teleport {
    public static void Teleport(Player player, Location location){
        double X = location.getX();
        double Y = location.getY();
        double Z = location.getZ();
        float Pitch = location.getPitch();
        float Yaw = location.getYaw();
        World d = AbilityPvP.MultiverseWorld("map1");
        Location teleport = new Location(d, X, Y, Z, Yaw, Pitch);
        player.teleport(teleport);
    }
}
