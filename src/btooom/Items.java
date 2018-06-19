package btooom;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import net.md_5.bungee.api.ChatColor;

public class Items {
	Chest chest = null;
	ItemStack skull = null;
	Inventory BuyBimInventory = null;
	public void setBuyBimInventory(){
		BuyBimInventory = Bukkit.createInventory(null, 9, ChatColor.DARK_GREEN+"§lBIM購入");
		BuyBimInventory.setItem(0, bims((byte)0, (byte)0));
		BuyBimInventory.setItem(2, bims((byte)1, (byte)0));
		BuyBimInventory.setItem(4, bims((byte)2, (byte)0));
		BuyBimInventory.setItem(6, bims((byte)3, (byte)0));
		BuyBimInventory.setItem(8, bims((byte)4, (byte)0));
	}

	public ItemStack bims(byte number, byte type){
		ItemStack item = null;
		ItemMeta meta = null;
		ArrayList<String> lores = new ArrayList<String>();
		switch(number){
		case 0:
			item = new ItemStack(Material.COAL);
			meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.RED+"§lタイマーBIM");
			if(type == 0)
			lores.add("6 yen");
			else{
				lores.add("左クリック  ：構える(カウントダウン)");
				lores.add("右クリック  ：投げる");
				lores.add("起爆カウント： 5sec");
			}
			break;
		case 1:
			item = new ItemStack(Material.FLINT);
			meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.BLUE+"§lクラッカーBIM");
			if(type == 0)
				lores.add("10 yen");
			else{
				lores.add("右クリック：投げる");
				lores.add("ブロックやプレイヤーに当たった瞬間爆発する");
			}
			break;
		case 2:
			item = new ItemStack(Material.FIREBALL);
			meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_RED+"フレイムBIM");
			if(type == 0)
				lores.add("15 yen");
			else{
				lores.add("右クリック：投げる");
				lores.add("着弾地点から十字に火を放つ");
			}
			break;
		case 3:
			item = new ItemStack(Material.SLIME_BALL);
			meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.LIGHT_PURPLE+"§lホーミングBIM");
			if(type == 0)
				lores.add("20 yen");
			else{
				lores.add("右クリック：投げる（前方にプレイヤーがいた場合）");
				lores.add("ロックオンしたプレイヤーを追尾する");
				lores.add("ブロックやプレイヤーにぶつかった瞬間爆発する");
			}
			break;
		case 4:
			item = skull;
			SkullMeta skullmeta =  (SkullMeta)item.getItemMeta();

					skullmeta.setDisplayName(ChatColor.GREEN+"§l設置BIM");
			if(type == 0)
				lores.add("30 yen");
			else{
				lores.add("右クリック：設置する");
				lores.add("設置者以外が近付いたとき爆発する");
				lores.add("１分後に自動的に爆発する");
			}
			skullmeta.setLore(lores);
			item.setItemMeta(skullmeta);
			return item;
	//		break;
		}

		meta.setLore(lores);
		item.setItemMeta(meta);
		return item;
	}

	public ItemStack otherItem(byte type){
		ItemStack item = null;
		ItemMeta meta = null;
		ArrayList<String> lores = new ArrayList<String>();
		switch(type){
		case 0:
			item = new ItemStack(Material.COMPASS);
			meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.GREEN+"レーダー");
			lores.add("手に埋め込まれたクリスタル");
			lores.add("右クリック：一番近いプレイヤーを探知して示す");
			break;
		}

		meta.setLore(lores);
		item.setItemMeta(meta);
		return item;
	}
}
