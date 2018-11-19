package btooom;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class Commands implements CommandExecutor {
	private GameManager GameManager;

	Commands(GameManager instance) {
		GameManager = instance;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		FileConfiguration config = GameManager.getConfig();

		if (cmd.getName().equalsIgnoreCase("start")) {
			if (config.get("CenterLocation") == null) {
				sender.sendMessage(GameManager.getHeader() + "CenterLocationが設定されていません。");
				sender.sendMessage(GameManager.getHeader() + "座標に立ち、/set CenterLocation で設定できます");
			} else if (config.get("WorldRange") == null) {
				sender.sendMessage(GameManager.getHeader() + "WorldRangeが設定されていません。");
				sender.sendMessage(GameManager.getHeader() + "/set WorldRange <範囲> で設定できます");
			} else {
				GameManager.start();
			}
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("set")) {
			if (args.length == 0)
				return false;

			if (args[0].equalsIgnoreCase("CenterLocation")) {
				Location loc = ((Player) sender).getLocation().clone();
				loc.setPitch(0);

				config.set("CenterLocation", loc);
				GameManager.saveConfig();
				sender.sendMessage("CenterLocationを設定しました");

				return true;
			} else if (args[0].equalsIgnoreCase("WorldRange")) {
				if (args.length < 2) {
					return false;
				}

				config.set("WorldRange", args[1]);
				GameManager.saveConfig();
				sender.sendMessage("WorldRangeを設定しました");

				return true;
			}

			return false;
		}
		if (cmd.getName().equalsIgnoreCase("spawn")) {
			if ((GameManager.getBoard().getPlayerTeam((Player) sender)) == null) {
				sender.sendMessage(GameManager.getHeader() + ChatColor.RED + "準備時間中のみ使用可能です");
				return true;
			}
			if (GameManager.isStart()) {
				sender.sendMessage(GameManager.getHeader() + ChatColor.RED + "準備時間中のみ使用可能です");
				return true;
			}
			((Player) sender).teleport(GameManager.respawn());
			sender.sendMessage(GameManager.getHeader() + ChatColor.RED + "リスポーンしました");
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("s") || cmd.getName().equalsIgnoreCase("shop")) {
			if (((Player) sender).getGameMode() == GameMode.SPECTATOR) {
				sender.sendMessage(GameManager.getHeader() + ChatColor.GRAY + "観戦者は購入できません");
			}

			if (GameManager.isStart() || sender.isOp()) {
				((Player) sender).openInventory(GameManager.getItems().BuyBimInventory);
			} else {
				sender.sendMessage(GameManager.getHeader() + ChatColor.GRAY + "試合中のみ使用できます");
			}

			return true;
		}

		if (cmd.getName().equalsIgnoreCase("debug")) {
			GameManager.gameover(null, false);
			return true;
		}
		return false;
	}
}
