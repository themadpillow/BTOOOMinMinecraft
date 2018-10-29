package bims;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import btooom.GameManager;

public class HomingBim {
	public static GameManager GameManager;

	Snowball bim = null;

	public void Throw(Player p, Player target) {
		if (p.getInventory().getItemInHand().getAmount() == 1) {
			p.getInventory().clear(p.getInventory().getHeldItemSlot());
		} else {
			p.getInventory().getItemInHand().setAmount(p.getInventory().getItemInHand().getAmount() - 1);
		}

		//bim = p.launchProjectile(Snowball.class);
		bim = (Snowball) Bukkit.getWorlds().get(0).spawnEntity(
				p.getEyeLocation().add(p.getEyeLocation().getDirection().multiply(BimConfig.getHomingSpeed())),
				EntityType.SNOWBALL);

		bim.setMetadata("type", new FixedMetadataValue(GameManager, "homing"));
		new BukkitRunnable() {
			public void run() {
				if (bim == null) {
					this.cancel();
					return;
				}
				bim.setVelocity(bim.getLocation().subtract(target.getEyeLocation()).toVector()
						.multiply(-0.3F / bim.getLocation().subtract(target.getEyeLocation()).toVector().length()));
				if (bim.getLocation().subtract(target.getEyeLocation()).toVector().length() < 1.5) {
					Bukkit.getWorlds().get(0).createExplosion(bim.getLocation().getX(), bim.getLocation().getY(),
							bim.getLocation().getZ(), BimConfig.getDamage(Bims.HomingBim), false, false);
					bim.remove();
					bim = null;
					this.cancel();
					return;
				}
			}
		}.runTaskTimer(GameManager, 0L, 1L);
	}
}
