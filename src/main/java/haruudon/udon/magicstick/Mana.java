package haruudon.udon.magicstick;

import haruudon.udon.magicstick.events.FoodLevel;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Mana {
    public static HashMap<UUID, Integer> mana;

    public static void setupMana(){
        mana = new HashMap<>();
    }

    public static void setInitialMana(Player p){
        mana.put(p.getUniqueId(), 20);
        FoodLevel.reloadFoodLevel(p);
    }

    public static boolean checkMana(Player p){ //hashmapにプレイヤーが登録されているかチェック
        return mana.containsKey(p.getUniqueId());
    }

    public static boolean checkUseMana(Player p, int useMana){
        if (mana.containsKey(p.getUniqueId())){ //使用するマナの数が現在のマナの数に等しいもしくは少ないなら & hashmapにプレイヤーが登録されているか
            if (mana.get(p.getUniqueId()) >= useMana){
                return true; //正しければtrueを返す
            } else if (TypeEvent.CheckType(p, "vampire")){
                int needMana = useMana - getMana(p);
                if (p.getHealth() > needMana){
                    p.setHealth(p.getHealth() - needMana);
                    addMana(p, needMana);
                    p.sendMessage(ChatColor.WHITE + "パッシブ発動: " + ChatColor.DARK_RED + "ヴァンパイア");
                    return true;
                }
            }
        }
        return false; //そうでなければfalse
    }

    public static int getMana(Player p){
        return mana.get(p.getUniqueId());
    }

    public static void removeMana(Player p, int removeMana){ //先にcheckManaでマナが足りるかチェックする
        int m = mana.get(p.getUniqueId()) - removeMana; //所持しているマナから使用するマナを引いた結果を記録する
        if (removeMana > 1) {
            if (TypeEvent.CheckType(p, "witch")){
                m += 1;
                p.sendMessage(ChatColor.WHITE + "パッシブ発動: " + ChatColor.DARK_PURPLE + "ウィッチ");
            }
        }
        mana.put(p.getUniqueId(), m); //hashmapを上書きする
        FoodLevel.reloadFoodLevel(p);
    }

    public static void addMana(Player p, int addmana){
        if (mana.containsKey(p.getUniqueId())) {
            if (mana.get(p.getUniqueId()) + addmana > 20){ //所持しているマナと追加するマナを足したときに20を超えないか調べる
                int m = 20; //超えるなら20に固定する
                mana.put(p.getUniqueId(), m); //hashmapを上書きする
                FoodLevel.reloadFoodLevel(p);
            } else { //そうではない = 所持しているマナと追加するマナを足したときに20以下になるなら
                int m = mana.get(p.getUniqueId()) + addmana; //そのまま足した数を記録する
                mana.put(p.getUniqueId(), m); //記録した数でhashmapを上書きする
                FoodLevel.reloadFoodLevel(p);
            }
        }
    }
}
