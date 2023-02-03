package haruudon.udon.abilitypvp.commands;

import haruudon.udon.abilitypvp.CoinAndMagicOre;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Coin implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player p){
            if (!(p.isOp())) {
                p.sendMessage(ChatColor.RED + "あなたはこのコマンドを使う権限を持っていません。");
                return true;
            }

            if (label.equalsIgnoreCase("coin") || label.equalsIgnoreCase("magicore") || label.equalsIgnoreCase("magicdust")){
                if (args.length == 0){
                    p.sendMessage(ChatColor.GRAY + p.getName() + message(label) + ChatColor.WHITE + CoinAndMagicOre.getItems(p, label));
                    return true;
                } else if (args.length >= 2){
                    if (args[0].equalsIgnoreCase("add")){
                        if (StringUtils.isNumeric(args[1])){
                            if (args.length == 2){
                                p.sendMessage(ChatColor.GREEN + p.getName() + " のアイテムを増やしました。");
                                CoinAndMagicOre.addItems(p, label, Integer.parseInt(args[1]));
                                p.sendMessage(ChatColor.GRAY + p.getName() + message(label) + ChatColor.WHITE + CoinAndMagicOre.getItems(p, label));
                                return true;
                            } else if (args.length == 3){
                                Player target = Bukkit.getPlayerExact(args[2]);
                                if (target == null){
                                    p.sendMessage(ChatColor.RED + args[2] + " は現在オンラインではありません。");
                                } else {
                                    p.sendMessage(ChatColor.GREEN + args[2] + " のアイテムを増やしました。");
                                    CoinAndMagicOre.addItems(target, label, Integer.parseInt(args[1]));
                                    p.sendMessage(ChatColor.GRAY + target.getName() + message(label) + ChatColor.WHITE + CoinAndMagicOre.getItems(target, label));
                                }
                                return true;
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("remove")){
                        if (StringUtils.isNumeric(args[1])){
                            if (args.length == 2){
                                if (CoinAndMagicOre.checkUseItems(p, label, Integer.parseInt(args[1]))){
                                    p.sendMessage(ChatColor.GREEN + p.getName() + " のアイテムを減らしました。");
                                    CoinAndMagicOre.removeItems(p, label, Integer.parseInt(args[1]));
                                    p.sendMessage(ChatColor.GRAY + p.getName() + message(label) + ChatColor.WHITE + CoinAndMagicOre.getItems(p, label));
                                } else {
                                    p.sendMessage(ChatColor.RED + "所持アイテムをマイナスにすることはできません。");
                                }
                                return true;
                            } else if (args.length == 3){
                                Player target = Bukkit.getPlayerExact(args[2]);
                                if (target == null){
                                    p.sendMessage(ChatColor.RED + args[2] + " は現在オンラインではありません。");
                                } else {
                                    if (CoinAndMagicOre.checkUseItems(target, label, Integer.parseInt(args[1]))){
                                        p.sendMessage(ChatColor.GREEN + target.getName() + " のアイテムを減らしました。");
                                        CoinAndMagicOre.removeItems(target, label, Integer.parseInt(args[1]));
                                        p.sendMessage(ChatColor.GRAY + target.getName() + message(label) + ChatColor.WHITE + CoinAndMagicOre.getItems(target, label));
                                    } else {
                                        p.sendMessage(ChatColor.RED + "所持アイテムをマイナスにすることはできません。");
                                    }
                                }
                                return true;
                            }
                        }
                    }
                } else {
                    Player target = Bukkit.getPlayerExact(args[0]);
                    if (target == null){
                        p.sendMessage(ChatColor.RED + args[0] + " は現在オンラインではありません。");
                        return false;
                    } else {
                        p.sendMessage(ChatColor.GRAY + target.getName() + message(label) + ChatColor.WHITE + CoinAndMagicOre.getItems(target, label));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String message(String command){
        String m = null;
        switch (command){
            case "coin" -> m = ChatColor.GOLD + " の所持ゴールド: ";
            case "magicore" -> m = ChatColor.DARK_PURPLE + " の所持魔法の鉱石: ";
            case "magicdust" -> m = ChatColor.DARK_AQUA + " の所持魔法の粉: ";
        }
        return m;
    }
}
