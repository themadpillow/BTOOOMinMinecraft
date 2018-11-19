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

	Item bim = null;
	int x = 0, y = 0, z = 0;

	public FlameBim(GameManager instance) {
		GameManager = instance;
	}

	public void Throw(Player p) {
		if (p.getInventory().getItemInHand().getAmount() == 1) {
			p.getInventory().clear(p.getInventory().getHeldItemSlot());
		} else {
			p.getInventory().getItemInHand().setAmount(p.getInventory().getItemInHand().getAmount() - 1);
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
				for (int x = -1; x < 2; x += 2) {
					for (int z = -1; z < 2; z += 2) {
						if (loc.clone().add(x, 0, z).getBlock().getType() == Material.AIR) {
							fire(loc.clone().add(x, 0, z));
						} else if (loc.clone().add(x, -1, z).getBlock().getType() == Material.AIR) {
							fire(loc.clone().add(x, -1, z));
						} else if (loc.clone().add(x, 1, z).getBlock().getType() == Material.AIR) {
							fire(loc.clone().add(x, 1, z));
						}
					}
				}
				Bukkit.getWorlds().get(0).playSound(loc, Sound.BLOCK_FIRE_AMBIENT, 0.5F, 1);

				new BukkitRunnable() {
					public void run() {
						x++;
						if (x > 4) {
							this.cancel();
						}
						if (loc.clone().add(x, 0, 0).getBlock().getType() == Material.AIR) {
							for (y = -1; -y < x + 1; y--) {
								if (loc.clone().add(x, y, 0).getBlock().getType() != Material.AIR) {
									break;
								}
							}
							fire(loc.clone().add(x, y + 1, 0));
							Bukkit.getWorlds().get(0).playSound(loc.clone().add(x, y, 0), Sound.BLOCK_FIRE_AMBIENT,
									1 / x * 0.2F, 1);
						} else {
							for (y = 1; y < x + 1; y++) {
								if (loc.clone().add(x, y, 0).getBlock().getType() == Material.AIR) {
									break;
								}
							}
							fire(loc.clone().add(x, y, 0));
							Bukkit.getWorlds().get(0).playSound(loc.clone().add(x, y, 0), Sound.BLOCK_FIRE_AMBIENT,
									1 / x * 0.2F, 1);
						}
						if (loc.clone().add(-x, 0, 0).getBlock().getType() == Material.AIR) {
							for (y = -1; -y < x + 1; y--) {
								if (loc.clone().add(-x, y, 0).getBlock().getType() != Material.AIR) {
									break;
								}
							}
							fire(loc.clone().add(-x, y + 1, 0));
							Bukkit.getWorlds().get(0).playSound(loc.clone().add(-x, y, 0), Sound.BLOCK_FIRE_AMBIENT,
									1 / x * 0.2F, 1);
						} else {
							for (y = 1; y < x + 1; y++) {
								if (loc.clone().add(-x, y, 0).getBlock().getType() == Material.AIR) {
									break;
								}
							}
							fire(loc.clone().add(-x, y, 0));
							Bukkit.getWorlds().get(0).playSound(loc.clone().add(x, y, 0), Sound.BLOCK_FIRE_AMBIENT,
									1 / x * 0.2F, 1);
						}

						z++;
						if (z > 4) {
							this.cancel();
						}
						if (loc.clone().add(0, 0, z).getBlock().getType() == Material.AIR) {
							for (y = -1; -y < z + 1; y--) {
								if (loc.clone().add(0, y, z).getBlock().getType() != Material.AIR) {
									break;
								}
							}
							fire(loc.clone().add(0, y + 1, z));
							Bukkit.getWorlds().get(0).playSound(loc.clone().add(0, y, z), Sound.BLOCK_FIRE_AMBIENT,
									1 / x * 0.2F, 1);
						} else {
							for (y = 1; y < z + 1; y++) {
								if (loc.clone().add(0, y, z).getBlock().getType() == Material.AIR) {
									break;
								}
							}
							fire(loc.clone().add(0, y, z));
							Bukkit.getWorlds().get(0).playSound(loc.clone().add(0, y, z), Sound.BLOCK_FIRE_AMBIENT,
									1 / x * 0.2F, 1);
						}
						if (loc.clone().add(0, 0, -z).getBlock().getType() == Material.AIR) {
							for (y = -1; -y < z + 1; y--) {
								if (loc.clone().add(0, y, -z).getBlock().getType() != Material.AIR) {
									break;
								}
							}
							fire(loc.clone().add(0, y + 1, -z));
							Bukkit.getWorlds().get(0).playSound(loc.clone().add(0, y, -z), Sound.BLOCK_FIRE_AMBIENT,
									1 / x * 0.2F, 1);
						} else {
							for (y = 1; y < z + 1; y++) {
								if (loc.clone().add(0, y, -z).getBlock().getType() == Material.AIR) {
									break;
								}
							}
							fire(loc.clone().add(0, y, -z));
							Bukkit.getWorlds().get(0).playSound(loc.clone().add(0, y, -z), Sound.BLOCK_FIRE_AMBIENT,
									1 / x * 0.2F, 1);
						}
					}
				}.runTaskTimer(GameManager, 0L, x * 15L);
				bim.remove();

				this.cancel();
			}
		}.runTaskTimer(GameManager, 0L, 5L);
	}

	public void fire(Location loc) {
		if (loc.getBlock().getType() == Material.AIR) {
			loc.getBlock().setType(Material.FIRE);
			Bukkit.getScheduler().runTaskLater(GameManager, () -> {
				loc.getBlock().setType(Material.AIR);
			}, 100L);
		}
	}
}
