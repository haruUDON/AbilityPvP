package haruudon.udon.abilitypvp;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;

import static haruudon.udon.abilitypvp.AbilityPvP.getData;

public class Decoration {
    public static void KillEffect(Player attacker, Player victim){
        String effect = getData("player").getString(attacker.getUniqueId().toString() + ".killeffect");
        switch (effect){
            case "Cloud" -> victim.getWorld().spawnParticle(Particle.CLOUD, victim.getLocation().add(0, 1, 0), 50, 0.2, 0.5, 0.2, 1);
            case "Blood" -> victim.getWorld().spawnParticle(Particle.BLOCK_CRACK, victim.getLocation().add(0, 1, 0), 200, 0.2, 0.5, 0.2, 0, new MaterialData(Material.REDSTONE_BLOCK));
            case "Flame" -> victim.getWorld().spawnParticle(Particle.FLAME, victim.getLocation().add(0, 1, 0), 50, 0, 2, 0, 0);
            case "Lava" -> victim.getWorld().spawnParticle(Particle.LAVA, victim.getLocation().add(0, 1, 0), 30, 0.2, 0.5, 0.2, 0);
            case "DripLava" -> victim.getWorld().spawnParticle(Particle.DRIP_LAVA, victim.getLocation().add(0, 1, 0), 50, 0.3, 0.4, 0.3, 0);
            case "DripWater" -> victim.getWorld().spawnParticle(Particle.DRIP_WATER, victim.getLocation().add(0, 1, 0), 50, 0.3, 0.4, 0.3, 0);
            case "Droplet" -> victim.getWorld().spawnParticle(Particle.WATER_DROP, victim.getLocation().add(0, 1, 0), 100, 0.3, 0.6, 0.3, 0);
            case "Enchant" -> victim.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, victim.getLocation().add(0, 2, 0), 100, 0.2, 0.2, 0.2, 1);
            case "Death" -> victim.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, victim.getLocation().add(0, 1, 0), 50, 0.2, 0.4, 0.2, 0);
            case "Ash" -> victim.getWorld().spawnParticle(Particle.FALLING_DUST, victim.getLocation().add(0, 1, 0), 100, 0.3, 0.4, 0.3, 1);
            case "CrushBone" -> victim.getWorld().spawnParticle(Particle.BLOCK_CRACK, victim.getLocation().add(0, 1, 0), 50, 0.2, 0.3, 0.2, 0.3, new MaterialData(Material.BONE_BLOCK));
            default -> {}
        }
    }

    public static void KillMassage(Player attacker, Player victim) {
        String message = getData("player").getString(attacker.getUniqueId().toString() + ".killmessage");
        String send = getData("killmessage").getString(message + ".message");
        String title = getData("killmessage").getString(message + ".title");
        ArrayList<Player> game = GameMain.GamePlayer;
        game.forEach(player -> player.sendMessage(ChatColor.GRAY + victim.getName() + ChatColor.YELLOW + " は " + ChatColor.GRAY + attacker.getName() + ChatColor.YELLOW + " " + send));
        victim.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + title, ChatColor.GRAY + "あなたは現在観戦者です", 0, 100, 20);
    }
}
