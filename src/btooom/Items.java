package btooom;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import bims.BimConfig;
import bims.Bims;
import net.md_5.bungee.api.ChatColor;

public class Items {
	private Inventory BuyBimInventory = null;

	public void setBuyBimInventory() {
		BuyBimInventory = Bukkit.createInventory(null, 9, ChatColor.DARK_GREEN + "§lBIM購入");
		BuyBimInventory.setItem(0, bims(Bims.TimerBim, (byte) 0));
		BuyBimInventory.setItem(2, bims(Bims.CrackerBim, (byte) 0));
		BuyBimInventory.setItem(4, bims(Bims.FlameBim, (byte) 0));
		BuyBimInventory.setItem(6, bims(Bims.HomingBim, (byte) 0));
		BuyBimInventory.setItem(8, bims(Bims.InstallationBim, (byte) 0));
	}

	public ItemStack bims(Bims bims, byte type) {
		ItemStack item = new ItemStack(bims.getMaterial());
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> lores = new ArrayList<String>();
		if (bims == Bims.TimerBim) {
			meta.setDisplayName(ChatColor.RED + "§lタイマーBIM");
			if (type == 0) {
				lores.add(BimConfig.getPrice(Bims.TimerBim) + " yen");
			} else {
				lores.add("左クリック  ：構える(カウントダウン)");
				lores.add("右クリック  ：投げる");
				lores.add("起爆カウント： 5sec");
			}
		} else if (bims == Bims.CrackerBim) {
			meta.setDisplayName(ChatColor.BLUE + "§lクラッカーBIM");
			if (type == 0) {
				lores.add(BimConfig.getPrice(Bims.CrackerBim) + " yen");
			} else {
				lores.add("右クリック：投げる");
				lores.add("ブロックやプレイヤーに当たった瞬間爆発する");
			}
		} else if (bims == Bims.FlameBim) {
			meta.setDisplayName(ChatColor.DARK_RED + "§lフレイムBIM");
			if (type == 0) {
				lores.add(BimConfig.getPrice(Bims.FlameBim) + " yen");
			} else {
				lores.add("右クリック：投げる");
				lores.add("着弾地点から十字に火を放つ");
			}
		} else if (bims == Bims.HomingBim) {
			meta.setDisplayName(ChatColor.LIGHT_PURPLE + "§lホーミングBIM");
			if (type == 0) {
				lores.add(BimConfig.getPrice(Bims.HomingBim) + " yen");
			} else {
				lores.add("右クリック：投げる（前方にプレイヤーがいた場合）");
				lores.add("ロックオンしたプレイヤーを追尾する");
				lores.add("ブロックやプレイヤーにぶつかった瞬間爆発する");
			}
		} else if (bims == Bims.InstallationBim) {
			item = new ItemStack(bims.getMaterial(), 1, (short) 3, (byte) SkullType.PLAYER.ordinal());
			SkullMeta skullmeta = (SkullMeta) item.getItemMeta();
			skullmeta.setOwner("MHF_TNT");
			skullmeta.setDisplayName(ChatColor.GREEN + "§l設置BIM");
			if (type == 0) {
				lores.add(BimConfig.getPrice(Bims.InstallationBim) + " yen");
			} else {
				lores.add("右クリック：設置する");
				lores.add("設置者以外が近付いたとき爆発する");
				lores.add("１分後に自動的に爆発する");
			}
			skullmeta.setLore(lores);
			item.setItemMeta(skullmeta);
			return item;
		} else {
			throw new RuntimeException("不正なBIMが指定されました");
		}

		meta.setLore(lores);
		item.setItemMeta(meta);
		return item;
	}

	public ItemStack otherItem(byte type) {
		ItemStack item = null;
		ItemMeta meta = null;
		ArrayList<String> lores = new ArrayList<String>();
		switch (type) {
		case 0:
			item = new ItemStack(Material.COMPASS);
			meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.GREEN + "レーダー");
			lores.add("手に埋め込まれたクリスタル");
			lores.add("左クリック：BIM購入画面を開く");
			lores.add("右クリック：一番近いプレイヤーを探知して示す");
			break;
		}

		meta.setLore(lores);
		item.setItemMeta(meta);
		return item;
	}

	public Inventory getBuyBimInventory() {
		return BuyBimInventory;
	}
}
