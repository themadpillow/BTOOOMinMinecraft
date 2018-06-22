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
	public static GameManager GameManager;
	Commands(GameManager instance){
		this.GameManager = instance;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		FileConfiguration config = GameManager.getConfig();

		if(cmd.getName().equalsIgnoreCase("start"))
		{
			if(config.get("LobbyLocation") == null){
				sender.sendMessage(GameManager.header+"LobbyLocationが設定されていません。");
				sender.sendMessage(GameManager.header+"座標に立ち、/set LobbyLocation で設定できます");
			}
			else{
					GameManager.start();
			}
			return true;
		}

		if(cmd.getName().equalsIgnoreCase("set"))
		{
			if(args.length == 0)
				return false;

			Location loc = ((Player)sender).getLocation().clone();
			loc.setPitch(0);

			config.set(args[0], loc);
			GameManager.saveConfig();
			sender.sendMessage(args[0]+"を設定しました");

			return true;
		}
		if(cmd.getName().equalsIgnoreCase("spawn")){
			if((GameManager.board.getPlayerTeam((Player)sender)) == null){
				sender.sendMessage(GameManager.header+ChatColor.RED+"準備時間中のみ使用可能です");
				return true;
			}
			if(GameManager.isStart){
				sender.sendMessage(GameManager.header+ChatColor.RED+"準備時間中のみ使用可能です");
				return true;
			}
			((Player)sender).teleport(GameManager.respawn());
			sender.sendMessage(GameManager.header+ChatColor.RED+"リスポーンしました");
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("s")
				||cmd.getName().equalsIgnoreCase("shop")){
			if(((Player)sender).getGameMode() == GameMode.SPECTATOR)
				sender.sendMessage(GameManager.header+ChatColor.GRAY+"観戦者は購入できません");

			if(GameManager.isStart)
				((Player)sender).openInventory(GameManager.Items.BuyBimInventory);
			else
				sender.sendMessage(GameManager.header+ChatColor.GRAY+"試合中のみ使用できます");

			return true;
		}

		if(cmd.getName().equalsIgnoreCase("debug")){
			GameManager.gameover(null, false);
			return true;
		}
		return false;
	}
}
