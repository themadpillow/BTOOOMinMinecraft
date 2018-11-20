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
	private GameManager GameManager;
	private static int timer = 5;
	private Item bim = null;

	public TimerBim(GameManager instance) {
		GameManager = instance;
	}

	public void count(Player p) {
		new BukkitRunnable() {
			public void run() {
				timer--;
				if (getBim() == null) {
					if (p.getItemInHand() == null
							|| p.getItemInHand().getType() != Material.COAL) {
						this.cancel();
						GameManager.getHandTimerBim().put(p, null);
						return;
					}
					p.playSound(p.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 0.5F, 3F);

					StringBuilder s = new StringBuilder();
					for (int i = 0; i < timer; i++) {
						s.append("■");
					}
					for (int i = s.length(); i < 5; i++) {
						s.append("□");
					}
					ActionBarAPI.sendActionBar(p, ChatColor.RED + "§l" + s);
				}
				if (timer < 1) {
					this.cancel();
					GameManager.getHandTimerBim().put(p, null);
					if (getBim() == null) {
						p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1, (byte) 4);
						Bukkit.getScheduler().runTaskLater(GameManager, () -> {
							p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1, (byte) 4);
						}, 1L);
						return;
					} else {
						getBim().remove();
						Bukkit.getWorlds().get(0).createExplosion(getBim().getLocation().getX(),
								getBim().getLocation().getY(),
								getBim().getLocation().getZ(), BimConfig.getDamage(Bims.TimerBim), false, false);
						setBim(null);
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

		setBim(Bukkit.getWorlds().get(0).dropItem(p.getEyeLocation(), new ItemStack(Material.COAL)));
		getBim().setMetadata("nopickup", new FixedMetadataValue(GameManager, true));
		if (p.isSneaking()) {
			getBim().setVelocity(p.getLocation().getDirection().multiply(BimConfig.getThrowRange(Status.Shift)));
		} else if (p.isSprinting()) {
			getBim().setVelocity(p.getLocation().getDirection().multiply(BimConfig.getThrowRange(Status.Run)));
		} else {
			getBim().setVelocity(p.getLocation().getDirection().multiply(BimConfig.getThrowRange(Status.Default)));
		}
		if (timer == 5) {
			count(p);
		}
	}

	public Item getBim() {
		return bim;
	}

	private void setBim(Item bim) {
		this.bim = bim;
	}
}
