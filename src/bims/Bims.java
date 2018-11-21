package bims;

import org.bukkit.Material;

public enum Bims {
	TimerBim, CrackerBim, FlameBim, HomingBim, InstallationBim;

	public Material getMaterial() {
		if (this == TimerBim) {
			return Material.COAL;
		} else if (this == CrackerBim) {
			return Material.FLINT;
		} else if (this == FlameBim) {
			return Material.FIREBALL;
		} else if (this == HomingBim) {
			return Material.SLIME_BALL;
		} else if (this == InstallationBim) {
			return Material.SKULL_ITEM;
		} else {
			throw new RuntimeException("不正なBIMが指定されました");
		}
	}
}
