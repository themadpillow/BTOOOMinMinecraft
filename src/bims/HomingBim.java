package bims;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

import btooom.GameManager;

public class HomingBim {
	private GameManager GameManager;
	private Snowball bim = null;

	public HomingBim(GameManager instance) {
		GameManager = instance;
	}

	public void Throw(Player p, Player target) {
		if (p.getInventory().getItemInMainHand().getAmount() == 1) {
			p.getInventory().clear(p.getInventory().getHeldItemSlot());
		} else {
			p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
		}

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

	public static Player getTargetedPlayer(Player player, int range) {
		// 視線の先にあるブロック一覧を取得する
		BlockIterator it = new BlockIterator(player, range);

		while (it.hasNext()) {
			Block block = it.next();

			if (block.getType() != Material.AIR) {
				// ブロックが見つかった(遮られている)、処理を終わってnullを返す
				return null;

			} else {
				// 位置が一致するPlayerがないか探す
				for (Player target : Bukkit.getOnlinePlayers()) {
					if (target == player
							|| target.getGameMode() == GameMode.SPECTATOR) {
						continue;
					}
					if (block.getLocation().distanceSquared(target.getLocation()) <= 3.0
							|| block.getLocation().distanceSquared(target.getEyeLocation()) <= 3.0) {
						// 見つかったPlayerを返す
						return target;
					}
				}
			}
		}

		// 何も見つからなかった
		return null;
	}
}
