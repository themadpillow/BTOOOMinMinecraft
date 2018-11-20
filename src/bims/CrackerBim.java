package bims;

import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;

import btooom.GameManager;

public class CrackerBim implements Listener {
	private GameManager GameManager;
	private Snowball bim = null;

	public CrackerBim(GameManager instance) {
		GameManager = instance;
	}

	public void Throw(Player p) {
		// 1つ手持ちからBIMを減らす
		if (p.getInventory().getItemInMainHand().getAmount() == 1) {
			p.getInventory().clear(p.getInventory().getHeldItemSlot());
		} else {
			p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
		}

		bim = p.launchProjectile(Snowball.class);
		bim.setMetadata("type", new FixedMetadataValue(GameManager, "crack"));
		if (p.isSneaking()) {
			bim.setVelocity(p.getLocation().getDirection().multiply(BimConfig.getThrowRange(Status.Shift)));
		} else if (p.isSprinting()) {
			bim.setVelocity(p.getLocation().getDirection().multiply(BimConfig.getThrowRange(Status.Run)));
		} else {
			bim.setVelocity(p.getLocation().getDirection().multiply(BimConfig.getThrowRange(Status.Default)));
		}
	}
}
