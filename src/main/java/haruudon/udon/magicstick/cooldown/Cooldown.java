package haruudon.udon.magicstick.cooldown;

import haruudon.udon.magicstick.TypeEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;


import java.util.*;

import static haruudon.udon.magicstick.MagicStick.*;

public class Cooldown {
    public static Map<UUID, Map<String, SecSlot>> Cooldowns;
    public static Map<UUID, ArrayList<String>> DownAbility;

    public static void setupCooldown(){
        Cooldowns = new HashMap<>();
        DownAbility = new HashMap<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player loop : Bukkit.getOnlinePlayers()){
                    UUID uuid = loop.getUniqueId();
                    if (DownAbility.containsKey(uuid) && Cooldowns.containsKey(uuid)) {
                        List<String> removed = new ArrayList<>();
                        for (String ability : DownAbility.get(uuid)) {//エラーはいてる。
                            if (Cooldowns.get(uuid).get(ability).sec() > 0) {
                                int sec = Cooldowns.get(uuid).get(ability).sec();
                                int slot = Cooldowns.get(uuid).get(ability).slot();
                                sec -= 1;
                                Cooldowns.get(uuid).put(ability, new SecSlot(sec, slot));
                                if (sec % 20 == 0) {
                                    ItemStack abilityItem = getAbilityData().getItemStack(ability + ".item1");
                                    String abilityName = abilityItem.getItemMeta().getDisplayName();
                                    ItemStack item = new ItemStack(Material.INK_SACK, sec / 20, (short) 8);
                                    ItemMeta meta = item.getItemMeta();
                                    meta.setDisplayName(abilityName);
                                    item.setItemMeta(meta);
                                    loop.getInventory().setItem(Cooldowns.get(uuid).get(ability).slot(), item);
                                }
                            } else if (Cooldowns.get(uuid).get(ability).sec() == 0) {
                                ItemStack abilityItem = getAbilityData().getItemStack(ability + ".item1");
                                loop.getInventory().setItem(Cooldowns.get(uuid).get(ability).slot(), abilityItem);
                                removed.add(ability);
                                Cooldowns.get(uuid).remove(ability);
                            }
                        }
                        DownAbility.get(uuid).removeAll(removed);
                    }
                }
            }
        }.runTaskTimer(getPlugin(), 0L, 1L);
    }

    public static void setCooldown(Player p, int sec, String ability, int slot){
        int reduction = 0;
        if (TypeEvent.CheckType(p, "creeper")){
            if (sec >= 20){
                reduction += 5;
                p.sendMessage(ChatColor.WHITE + "パッシブ発動: " + ChatColor.GREEN + "クリーパー");
            }
        }
        if (!(DownAbility.containsKey(p.getUniqueId()))) DownAbility.put(p.getUniqueId(), new ArrayList<>());
        if (!(Cooldowns.containsKey(p.getUniqueId()))) Cooldowns.put(p.getUniqueId(), new HashMap<>());
        DownAbility.get(p.getUniqueId()).add(ability);
        Cooldowns.get(p.getUniqueId()).put(ability, new SecSlot((sec * 20) - (reduction * 20), slot));
        ItemStack abilityItem = getAbilityData().getItemStack(ability + ".item1");
        String abilityName = abilityItem.getItemMeta().getDisplayName();
        ItemStack item = new ItemStack(Material.INK_SACK, sec - reduction, (short) 8);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(abilityName);
        item.setItemMeta(meta);
        p.getInventory().setItem(slot, item);
    }
}

