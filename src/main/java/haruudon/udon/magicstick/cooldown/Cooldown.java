package haruudon.udon.magicstick.cooldown;

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
    public static Map<UUID, Map<String, SecSlot>> cooldowns;
    public static Map<UUID, ArrayList<String>> downAbility;

    public static void setupCooldown(){
        cooldowns = new HashMap<>();
        downAbility = new HashMap<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player loop : Bukkit.getOnlinePlayers()){
                    UUID uuid = loop.getUniqueId();
                    if (downAbility.containsKey(uuid) && cooldowns.containsKey(uuid)){
                        try{
                            for (String ability : downAbility.get(uuid)){
                                if (cooldowns.get(uuid).get(ability).getSec() > 0){
                                    int sec = cooldowns.get(uuid).get(ability).getSec();
                                    int slot = cooldowns.get(uuid).get(ability).getSlot();
                                    sec -=1;
                                    cooldowns.get(uuid).put(ability, new SecSlot(sec, slot));
                                    if (sec % 20 == 0) {
                                        ItemStack abilityItem = getAbilityData().getItemStack(ability + ".item1");
                                        String abilityName = abilityItem.getItemMeta().getDisplayName();
                                        ItemStack item = new ItemStack(Material.INK_SACK, sec / 20, (short) 8);
                                        ItemMeta meta = item.getItemMeta();
                                        meta.setDisplayName(abilityName);
                                        item.setItemMeta(meta);
                                        loop.getInventory().setItem(cooldowns.get(uuid).get(ability).getSlot(), item);
                                    }
                                } else if (cooldowns.get(uuid).get(ability).getSec() == 0){ //エラーはいてる。どれかがnullらしい。remove処理の部分かも
                                    ItemStack abilityItem = getAbilityData().getItemStack(ability + ".item1");
                                    loop.getInventory().setItem(cooldowns.get(uuid).get(ability).getSlot(), abilityItem);
                                    downAbility.get(uuid).remove(ability);
                                    cooldowns.get(uuid).remove(ability);
                                }
                            }
                        } catch (ConcurrentModificationException e){
                            if (!(downAbility.get(uuid).isEmpty()) && !(cooldowns.get(uuid).isEmpty())){
                                loop.sendMessage(ChatColor.RED + "エラー");
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(getPlugin(), 0L, 1L);
    }

    public static void setCooldown(Player p, int sec, String ability, int slot){
        if (!(downAbility.containsKey(p.getUniqueId()))) downAbility.put(p.getUniqueId(), new ArrayList<>());
        if (!(cooldowns.containsKey(p.getUniqueId()))) cooldowns.put(p.getUniqueId(), new HashMap<>());
        downAbility.get(p.getUniqueId()).add(ability);
        cooldowns.get(p.getUniqueId()).put(ability, new SecSlot(sec * 20, slot));
        ItemStack abilityItem = getAbilityData().getItemStack(ability + ".item1");
        String abilityName = abilityItem.getItemMeta().getDisplayName();
        ItemStack item = new ItemStack(Material.INK_SACK, cooldowns.get(p.getUniqueId()).get(ability).getSec() / 20, (short) 8);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(abilityName);
        item.setItemMeta(meta);
        p.getInventory().setItem(slot, item);
    }
}

