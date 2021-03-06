package bims;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import btooom.GameManager;

public class InstallationBim {
	private GameManager GameManager;
	private Block bim = null;

	public InstallationBim(GameManager instance) {
		GameManager = instance;
	}

	public void set(Player p, Block block) {
		bim = block;
		search(p);
		Bukkit.getScheduler().runTaskLater(GameManager, () -> {
			if (bim != null) {
				Location loc = bim.getLocation();
				Bukkit.getWorlds().get(0).createExplosion(loc.getX(), loc.getY(), loc.getZ(), 2.5F, false, false);
				bim.setType(Material.AIR);
				bim = null;
			}
		}, 1200L);

	}

	private void search(Player p) {
		new BukkitRunnable() {
			public void run() {
				if (bim == null) {
					this.cancel();
					return;
				}
				Location loc = bim.getLocation();
				for (Entity e : Bukkit.getWorlds().get(0).getNearbyEntities(
						loc,
						BimConfig.getInstallationRange(),
						BimConfig.getInstallationRange(),
						BimConfig.getInstallationRange())) {
					if (e instanceof Player
							&& (((Player) e).getGameMode() == GameMode.SPECTATOR
									|| e == p)) {
						continue;
					}
					Bukkit.getWorlds().get(0).createExplosion(loc.getX(), loc.getY(), loc.getZ(),
							BimConfig.getDamage(Bims.InstallationBim), false, false);
					bim.setType(Material.AIR);
					bim = null;
					this.cancel();
					break;
				}
			}
		}.runTaskTimer(GameManager, 0L, 1L);
	}
}
