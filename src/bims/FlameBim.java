package bims;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import btooom.GameManager;

public class FlameBim {
	private GameManager GameManager;
	private Item bim = null;

	public FlameBim(GameManager instance) {
		GameManager = instance;
	}

	public void Throw(Player p) {
		if (p.getInventory().getItemInMainHand().getAmount() == 1) {
			p.getInventory().clear(p.getInventory().getHeldItemSlot());
		} else {
			p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
		}

		bim = Bukkit.getWorlds().get(0).dropItem(p.getEyeLocation(), new ItemStack(Material.FIREBALL));
		bim.setMetadata("nopickup", new FixedMetadataValue(GameManager, true));
		if (p.isSneaking()) {
			bim.setVelocity(p.getLocation().getDirection().multiply(BimConfig.getThrowRange(Status.Shift)));
		} else if (p.isSprinting()) {
			bim.setVelocity(p.getLocation().getDirection().multiply(BimConfig.getThrowRange(Status.Run)));
		} else {
			bim.setVelocity(p.getLocation().getDirection().multiply(BimConfig.getThrowRange(Status.Default)));
		}

		new BukkitRunnable() {
			public void run() {
				if (!bim.isOnGround()) {
					return;
				}

				Location loc = bim.getLocation();
				if (loc.getBlock().getType() == Material.AIR) {
					fire(loc);
				}

				for (int x = -1; x < 2; x++) {
					for (int z = -1; z < 2; z++) {
						for (int y = -1; y < 2; y++) {
							if (loc.clone().add(x, y, z).getBlock().getType() == Material.AIR) {
								fire(loc.clone().add(x, y, z));
								break;
							}
						}
					}
				}
				Bukkit.getWorlds().get(0).playSound(loc, Sound.BLOCK_FIRE_AMBIENT, 0.5F, 1);
				Bukkit.getScheduler().runTaskTimer(GameManager, () -> {
					for (int x = 2; x < 5; x++) {
						for (int y = -x; y < x; y++) {
							if (loc.clone().add(x, y, 0).getBlock().getType() == Material.AIR) {
								fire(loc.clone().add(x, y, 0));
								Bukkit.getWorlds().get(0).playSound(loc.clone().add(x, y, 0),
										Sound.BLOCK_FIRE_AMBIENT, 0.2F / x, 1);
								break;
							}
						}
						for (int y = -x; y < x; y++) {
							if (loc.clone().add(-x, y, 0).getBlock().getType() == Material.AIR) {
								fire(loc.clone().add(-x, y, 0));
								Bukkit.getWorlds().get(0).playSound(loc.clone().add(-x, y, 0),
										Sound.BLOCK_FIRE_AMBIENT, 0.2F / x, 1);
								break;
							}
						}
					}
				}, 15L, 15L);

				Bukkit.getScheduler().runTaskTimer(GameManager, () -> {
					for (int z = 2; z < 5; z++) {
						for (int y = -z; y < z; y++) {
							if (loc.clone().add(0, y, z).getBlock().getType() == Material.AIR) {
								fire(loc.clone().add(0, y, z));
								Bukkit.getWorlds().get(0).playSound(loc.clone().add(0, y, z),
										Sound.BLOCK_FIRE_AMBIENT, 0.2F / z, 1);
								break;
							}
						}
						for (int y = -z; y < z; y++) {
							if (loc.clone().add(0, y, z).getBlock().getType() == Material.AIR) {
								fire(loc.clone().add(0, y, z));
								Bukkit.getWorlds().get(0).playSound(loc.clone().add(0, y, z),
										Sound.BLOCK_FIRE_AMBIENT, 0.2F / z, 1);
								break;
							}
						}
					}
				}, 15L, 15L);

				bim.remove();
				this.cancel();
			}
		}.runTaskTimer(GameManager, 0L, 5L);
	}

	private void fire(Location loc) {
		loc.getBlock().setType(Material.FIRE);
		Bukkit.getScheduler().runTaskLater(GameManager, () -> {
			loc.getBlock().setType(Material.AIR);
		}, 100L);
	}
}
