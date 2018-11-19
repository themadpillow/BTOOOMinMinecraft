package bims;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

import btooom.GameManager;

public class TimerBim {
	public static GameManager GameManager;

	int timer = 5;
	public Item bim = null;

	public void count(Player p) {
		new BukkitRunnable() {
			public void run() {
				timer--;
				if (bim == null) {
					if (p.getItemInHand() == null
							|| p.getItemInHand().getType() != Material.COAL) {
						this.cancel();
						GameManager.HandTimerBim.put(p, null);
						return;
					}
					p.playSound(p.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 0.5F, 3F);
					//		String s = "";
					//		for(int i = 0; i < timer; i ++){
					//			s = s.concat("⬛");
					//		}
					//		for(int i = 0; s.length() < 5; i ++){
					//			s = s.concat("⃞");
					//	}
					ActionBarAPI.sendActionBar(p, ChatColor.RED + "§l" + timer);
				}
				if (timer < 1) {
					this.cancel();
					GameManager.HandTimerBim.put(p, null);
					if (bim == null) {
						p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1, (byte) 4);
						Bukkit.getScheduler().runTaskLater(GameManager, () -> {
							p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1, (byte) 4);
						}, 1L);
						return;
					} else {
						bim.remove();
						Bukkit.getWorlds().get(0).createExplosion(bim.getLocation().getX(), bim.getLocation().getY(),
								bim.getLocation().getZ(), BimConfig.getDamage(Bims.TimerBim), false, false);
						bim = null;
					}
				}
			}
		}.runTaskTimer(GameManager, 0L, 20L);
	}

	public void Throw(Player p) {
		if (p.getInventory().getItemInHand().getAmount() == 1) {
			p.getInventory().clear(p.getInventory().getHeldItemSlot());
		} else {
			p.getInventory().getItemInHand().setAmount(p.getInventory().getItemInHand().getAmount() - 1);
		}

		bim = Bukkit.getWorlds().get(0).dropItem(p.getEyeLocation(), new ItemStack(Material.COAL));
		bim.setMetadata("nopickup", new FixedMetadataValue(GameManager, true));
		if (p.isSneaking()) {
			bim.setVelocity(p.getLocation().getDirection().multiply(BimConfig.getThrowRange(Status.Shift)));
		} else if (p.isSprinting()) {
			bim.setVelocity(p.getLocation().getDirection().multiply(BimConfig.getThrowRange(Status.Run)));
		} else {
			bim.setVelocity(p.getLocation().getDirection().multiply(BimConfig.getThrowRange(Status.Default)));
		}
		if (timer == 5) {
			count(p);
		}
	}
}
