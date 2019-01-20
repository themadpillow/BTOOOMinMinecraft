package other;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Score;

import btooom.GameManager;
import net.md_5.bungee.api.ChatColor;

public class Timer {
	private GameManager GameManager;

	private int MIN;
	private int SEC;
	private String s;
	private BukkitTask TimerTaskID;

	public Timer(GameManager instance) {
		GameManager = instance;
	}

	public void start(int min, int sec) {
		MIN = min;
		SEC = sec;
		s = (ChatColor.GREEN + "残り時間 : " + MIN + ":" + String.format("%1$02d", SEC));

		for (Player player : Bukkit.getOnlinePlayers()) {
			player.getScoreboard().resetScores("試合開始前です");
			Score timer = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(s);
			timer.setScore(0);
		}
		setTimerTaskID(new BukkitRunnable() {
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					player.getScoreboard().resetScores(s);
				}

				if (SEC != 0) {
					SEC--;
				} else {
					if (MIN != 0) {
						MIN--;
						SEC = 59;
					}
				}
				if (MIN == 0) {
					switch (SEC) {
					case 30:
						Bukkit.broadcastMessage(GameManager.getHeader() + ChatColor.LIGHT_PURPLE + "試合終了まで残り30秒です");
						break;
					case 5:
					case 4:
					case 3:
					case 2:
					case 1:
						for (Player p : Bukkit.getOnlinePlayers()) {
							p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1F, 1F);
						}
						Bukkit.broadcastMessage(
								GameManager.getHeader() + ChatColor.LIGHT_PURPLE + "試合終了まで " + SEC + "...");
						break;
					case 0:
						GameManager.gameover(null, false);
						this.cancel();
						break;
					}
				}
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (player.getScoreboard() != null
							&& player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) {
						s = (ChatColor.GREEN + "残り時間 : " + MIN + ":" + String.format("%1$02d", SEC));
						Score timer = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(s);
						timer.setScore(0);
						
						Score money = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(ChatColor.GOLD + "§l    所持金");
						money.setScore(GameManager.getMoney(player));
					}
				}
			}
		}.runTaskTimer(GameManager, 20L, 20L));
	}

	public BukkitTask getTimerTaskID() {
		return TimerTaskID;
	}

	private void setTimerTaskID(BukkitTask timerTaskID) {
		TimerTaskID = timerTaskID;
	}
}
