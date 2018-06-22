package other;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Score;

import btooom.GameManager;
import net.md_5.bungee.api.ChatColor;

public class Timer {
	public static GameManager GameManager;
	public Timer(GameManager instance){
		GameManager = instance;
	}
	static int MIN;
	static int SEC;
	static String s;
	static Score timer;
	public static BukkitTask TimerTaskID;

	public static void timer(int min, int sec)
	{
		MIN = min;
		SEC = sec;
		s = (ChatColor.GREEN+"残り時間 : "+ MIN+":"+String.format("%1$02d", SEC));
		timer = GameManager.info.getScore(s);
		GameManager.board.resetScores("試合開始前です");
		TimerTaskID = new BukkitRunnable(){public void run(){

			GameManager.board.resetScores(s);

			if( SEC != 0)
				SEC --;
			else
			{
				if( MIN != 0)
				{
					MIN --;
					SEC = 59;
				}
			}
			if(MIN == 0){
				switch(SEC){
				case 30:
					Bukkit.broadcastMessage(GameManager.header+ChatColor.LIGHT_PURPLE+"試合終了まで残り30秒です");
					break;
				case 5:
				case 4:
				case 3:
				case 2:
				case 1:
					for(Player p : Bukkit.getOnlinePlayers())
						p.playSound(p.getLocation(), Sound.WOOD_CLICK, 1F, 1F);
					Bukkit.broadcastMessage(GameManager.header+ChatColor.LIGHT_PURPLE+"試合終了まで 5...");
					break;
				case 0:
					GameManager.gameover(null, false);
					this.cancel();
					break;
				}
			}

			s = (ChatColor.GREEN+"残り時間 : "+ MIN+":"+String.format("%1$02d", SEC));
			timer = GameManager.info.getScore(s);
			timer.setScore(0);
		}}.runTaskTimer(GameManager, 0L , 20L);
	}
}
