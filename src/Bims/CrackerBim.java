package Bims;

import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;

import btooom.GameManager;

public class CrackerBim implements Listener {
	public static GameManager GameManager;
	Snowball bim = null;

	public void Throw(Player p) {
		if (p.getInventory().getItemInHand().getAmount() == 1) {
			p.getInventory().clear(p.getInventory().getHeldItemSlot());
		} else {
			p.getInventory().getItemInHand().setAmount(p.getInventory().getItemInHand().getAmount() - 1);
		}
		bim = p.launchProjectile(Snowball.class);
		bim.setMetadata("type", new FixedMetadataValue(GameManager, "crack"));
		if (p.isSneaking()) {
			bim.setVelocity(p.getLocation().getDirection().multiply(0.7));
		} else if (p.isSprinting()) {
			bim.setVelocity(p.getLocation().getDirection().multiply(1.5));
		} else {
			bim.setVelocity(p.getLocation().getDirection());
		}
	}
}
