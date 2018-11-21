package bims;

import org.bukkit.configuration.file.FileConfiguration;

import other.CustomConfig;

public class BimConfig {
	private static float defaultThrowRange;
	private static float runThrowRange;
	private static float shiftThrowRange;

	private static float timerBimDamage;
	private static float crackBimDamage;
	private static float homingBimDamage;
	private static float installationBimDamage;

	private static float homingSpeed;
	private static float installationRange;

	public BimConfig(CustomConfig config) {
		init(config);
	}

	private void init(CustomConfig configuration) {
		FileConfiguration config = configuration.getConfig();

		defaultThrowRange = (float) config.getDouble("ThrowRange.default");
		runThrowRange = (float) config.getDouble("ThrowRange.run");
		shiftThrowRange = (float) config.getDouble("ThrowRange.shift");

		timerBimDamage = (float) config.getDouble("Bims.TimerBim.damage");
		crackBimDamage = (float) config.getDouble("Bims.CrackerBim.damage");
		homingBimDamage = (float) config.getDouble("Bims.HomingBim.damage");
		installationBimDamage = (float) config.getDouble("Bims.InstallationBim.damage");

		homingSpeed = (float) config.getDouble("Bims.HomingBim.speed");
		installationRange = (float) config.getDouble("Bims.InstallationBim.range");
	}

	public static float getDamage(Bims bim) {
		if (bim == Bims.TimerBim) {
			return timerBimDamage;
		} else if (bim == Bims.CrackerBim) {
			return crackBimDamage;
		} else if (bim == Bims.HomingBim) {
			return homingBimDamage;
		} else if (bim == Bims.InstallationBim) {
			return installationBimDamage;
		} else {
			throw new RuntimeException("不明なBimが指定されました");
		}
	}

	public static float getHomingSpeed() {
		return homingSpeed;
	}

	public static float getInstallationRange() {
		return installationRange;
	}

	public static float getThrowRange(Status status) {
		if (status == bims.Status.Default) {
			return defaultThrowRange;
		} else if (status == bims.Status.Run) {
			return runThrowRange;
		} else if (status == bims.Status.Shift) {
			return shiftThrowRange;
		} else {
			throw new RuntimeException("不明なStatusが指定されました");
		}
	}
}
