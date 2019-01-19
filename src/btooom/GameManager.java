package btooom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import bims.BimConfig;
import bims.Bims;
import bims.CrackerBim;
import bims.FlameBim;
import bims.HomingBim;
import bims.InstallationBim;
import bims.TimerBim;
import other.CustomConfig;
import other.Timer;
import other.TitleSender;

public class GameManager extends JavaPlugin implements Listener {
	private Items Items = new Items();
	private Timer Timer = new Timer(this);
	private TitleSender TitleSender;
	private FileConfiguration config;

	private HashMap<Player, TimerBim> HandTimerBim = new HashMap<Player, TimerBim>();

	private HashMap<Player, Boolean> canThrow = new HashMap<Player, Boolean>();
	private HashMap<Player, Boolean> canBuy = new HashMap<Player, Boolean>();

	private boolean isStart = false;
	private Scoreboard board = null;
	private Team team = null;
	private Objective info = null;

	private List<Player> alivelist = new ArrayList<Player>();

	private static final String header = ChatColor.GREEN + "§l[BTOOOM] " + ChatColor.RESET;

	public void onEnable() {
		TitleSender = new TitleSender();
		config = this.getConfig();

		new TimerBim(this);
		new CrackerBim(this);
		new FlameBim(this);
		new InstallationBim(this);
		new HomingBim(this);

		if (config.get("Timer") == null) {
			config.set("Timer", true);
			this.saveConfig();
		}

		getItems().setBuyBimInventory();

		setBoard(Bukkit.getScoreboardManager().getMainScoreboard());
		if (getBoard().getTeam("team") == null) {
			setTeam(getBoard().registerNewTeam("team"));
		} else {
			setTeam(getBoard().getTeam("team"));
		}

		getTeam().setAllowFriendlyFire(true);
		getTeam().setNameTagVisibility(NameTagVisibility.HIDE_FOR_OWN_TEAM);

		if ((boolean) config.get("Timer")) {
			if (getBoard().getObjective(ChatColor.DARK_GREEN + "BTOOOM!") == null)
				setInfo(getBoard().registerNewObjective(ChatColor.DARK_GREEN + "BTOOOM!", "info"));
			else {
				setInfo(getBoard().getObjective(ChatColor.DARK_GREEN + "BTOOOM!"));
			}
			getInfo().setDisplaySlot(DisplaySlot.SIDEBAR);

			Score score = getInfo().getScore("試合開始前です");
			score.setScore(0);
		}

		Commands Commands = new Commands(this);
		getCommand("start").setExecutor(Commands);
		getCommand("set").setExecutor(Commands);
		getCommand("s").setExecutor(Commands);
		getCommand("shop").setExecutor(Commands);
		getCommand("spawn").setExecutor(Commands);
		getCommand("debug").setExecutor(Commands);

		Bukkit.getPluginManager().registerEvents(new Events(this), this);

		for (Entity entity : Bukkit.getWorlds().get(0).getEntities()) {
			if (entity instanceof Player) {
				if (((Player) entity).getGameMode() != GameMode.CREATIVE) {
					((Player) entity).getInventory().clear();
					entity.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
					((Player) entity).setGameMode(GameMode.SURVIVAL);
				}
				getCanThrow().put((Player) entity, false);
				getCanBuy().put((Player) entity, false);
				((Player) entity).removePotionEffect(PotionEffectType.INVISIBILITY);

			}
		}

		bimDamageConfigCheck();
	}

	public void onDisable() {
		if (getInfo() != null) {
			getInfo().unregister();
		}
		if (getTeam() != null) {
			getTeam().unregister();
		}
	}

	private void bimDamageConfigCheck() {
		File bimconfigFile = new File(this.getDataFolder().getPath() + "\\bimConfig.yml");
		if (!bimconfigFile.exists()) {
			InputStream fis = getClass().getResourceAsStream("/bimConfig.yml");
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(bimconfigFile);

				byte[] buf = new byte[1024];
				int i = 0;
				while ((i = fis.read(buf)) != -1) {
					fos.write(buf, 0, i);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					fis.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		new BimConfig(new CustomConfig(this, "bimConfig.yml"));
	}

	public void start() {

		if ((boolean) config.get("Timer")) {
			Timer.start(20, 20);
		}

		for (Player p : Bukkit.getOnlinePlayers()) {
			getAlivelist().add(p);
			getCanThrow().put(p, false);
			getCanBuy().put(p, true);

			p.getInventory().clear();
			p.setGameMode(GameMode.SURVIVAL);
			p.getInventory().setItem(0, getItems().bims(Bims.TimerBim, (byte) 1));
			for (int i = 0; i < 9; i++) {
				p.getInventory().addItem(getItems().bims(Bims.TimerBim, (byte) 1));
			}
			p.getInventory().setItem(8, getItems().otherItem((byte) 0));
			getTeam().addPlayer(p);
			p.teleport(respawn());
			
			setMoney(p, 30);
		}

		new BukkitRunnable() {
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					addMoney(player, 5);
				}
			}
		}.runTaskTimer(this, 100L, 100L);

		Bukkit.broadcastMessage(getHeader() + ChatColor.RED + "20秒後に試合が開始します！");
		Bukkit.broadcastMessage(getHeader() + ChatColor.RED + "準備時間の間は/spawnでスポーンし直すことが出来ます");
		Bukkit.broadcastMessage(getHeader() + ChatColor.RED + "アイテムは試合開始後から使用可能です");

		Bukkit.getScheduler().runTaskLater(this, () -> {
			setStart(true);
			Bukkit.broadcastMessage(getHeader() + ChatColor.LIGHT_PURPLE + "試合開始です");
			for (Player p : Bukkit.getOnlinePlayers()) {
				getCanThrow().put(p, true);
				p.playSound(p.getLocation().add(0, 5, 0), Sound.ENTITY_FIREWORK_LARGE_BLAST, 1F, 1F);
				TitleSender.sendTitle(p, ChatColor.GREEN + "§lマイクラBTOOOM", ChatColor.RED+ "§lSTART");
			}
		}, 380);
		Bukkit.getScheduler().runTaskLater(this, () -> {
			for (Player p : Bukkit.getOnlinePlayers()) {
				TitleSender.resetTitle(p);
			}
		}, 440);
	}

	public Location respawn() {
		int range = Integer.parseInt((String) config.get("WorldRange"));
		Location loc = (Location) config.get("CenterLocation");

		for (int y = 0;; y++) {
			double x = (Math.random() * range) - (range / 2);
			double z = (Math.random() * range) - (range / 2);

			Location reloc = loc.clone().add(x, y, z);
			if (reloc.getBlock().getType() == Material.AIR
					&& reloc.clone().add(0, -1, 0).getBlock().getType() != Material.WATER
					&& reloc.clone().add(0, -1, 0).getBlock().getType() != Material.WATER_LILY
					&& reloc.clone().add(0, -1, 0).getBlock().getType() != Material.STATIONARY_WATER) {
				return reloc;
			} else {
				continue;
			}
		}
	}

	public void gameover(Player winner, boolean sixstar) {

		if ((boolean) config.get("Timer")) {
			Timer.getTimerTaskID().cancel();
		}
		setStart(false);

		int star = 0;
		if (winner == null) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.sendMessage(getHeader() + ChatColor.RED + "時間切れにより勝者が決定しました");
				if (p.getGameMode() == GameMode.SPECTATOR) {
					continue;
				}
				if (winner == null) {
					winner = p;
					//	continue;
				}
				for (ItemStack item : p.getInventory().getContents()) {
					if (item != null && item.getType() == Material.NETHER_STAR && item.getAmount() > star) {
						winner = p;
						star = item.getAmount();
					}
				}
				p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation(), TeleportCause.ENDER_PEARL);
				TitleSender.sendTitle(p, ChatColor.DARK_RED + "勝者:" + ChatColor.DARK_GREEN + winner.getName(),
						ChatColor.YELLOW + "獲得クリスタル数：" + star + "個");
				p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_LAUNCH, 1F, 1F);

				p.getInventory().clear();

				p.setGameMode(GameMode.SURVIVAL);
			}
		} else if (sixstar) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				for (ItemStack item : winner.getInventory().getContents()) {
					if (item != null && item.getType() == Material.COMPASS) {
						star = item.getAmount();
					}
				}
				for (Player all : Bukkit.getOnlinePlayers()) {
					all.sendMessage(getHeader() + ChatColor.RED + winner.getName() + "さんがクリスタルを6個以上集めました");
					all.teleport(Bukkit.getWorlds().get(0).getSpawnLocation(), TeleportCause.ENDER_PEARL);
					TitleSender.sendTitle(all, ChatColor.DARK_RED + "勝者:" + ChatColor.DARK_GREEN + winner.getName(),
							ChatColor.YELLOW + "獲得クリスタル数：" + star + "個");
					all.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_LAUNCH, 1F, 1F);
					all.getInventory().clear();
					all.setGameMode(GameMode.SURVIVAL);
				}
			}
		} else {
			for (Player all : Bukkit.getOnlinePlayers()) {
				all.sendMessage(getHeader() + ChatColor.RED + winner.getName() + "さんが最後の生存者です");
				all.teleport(Bukkit.getWorlds().get(0).getSpawnLocation(), TeleportCause.ENDER_PEARL);
				TitleSender.sendTitle(all, ChatColor.DARK_RED + "勝者:" + ChatColor.DARK_GREEN + winner.getName(),
						ChatColor.YELLOW + "獲得クリスタル数：" + star + "個");
				all.playSound(all.getLocation(), Sound.ENTITY_FIREWORK_LAUNCH, 1F, 1F);
				all.getInventory().clear();

				all.setGameMode(GameMode.SURVIVAL);
			}

		}

		for (Entity entity : Bukkit.getWorlds().get(0).getEntities()) {
			if (entity instanceof Player) {
				if (((Player) entity).getGameMode() != GameMode.CREATIVE) {
					((Player) entity).getInventory().clear();
					entity.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
					((Player) entity).setGameMode(GameMode.SURVIVAL);
				}
				getCanThrow().put((Player) entity, false);
				getCanBuy().put((Player) entity, false);
				((Player) entity).removePotionEffect(PotionEffectType.INVISIBILITY);

			} else if (entity instanceof Item) {
				entity.remove();
			}
		}
	}

	public HashMap<Player, TimerBim> getHandTimerBim() {
		return HandTimerBim;
	}

	public void setHandTimerBim(HashMap<Player, TimerBim> handTimerBim) {
		HandTimerBim = handTimerBim;
	}

	public String getHeader() {
		return header;
	}

	public boolean isStart() {
		return isStart;
	}

	private void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	public Scoreboard getBoard() {
		return board;
	}

	private void setBoard(Scoreboard board) {
		this.board = board;
	}

	public Items getItems() {
		return Items;
	}

	public HashMap<Player, Boolean> getCanThrow() {
		return canThrow;
	}

	public int getMoney(Player player) {
		if (!player.getMetadata("money").isEmpty()) {
			return player.getMetadata("money").get(0).asInt();
		} else {
			return -1;
		}
	}

	private void setMoney(Player player , int money) {
		player.setMetadata("money", new FixedMetadataValue(this, money));
	}
	
	public void addMoney(Player player, int addMoney) {
		setMoney(player, getMoney(player) + 5);
	}

	public Team getTeam() {
		return team;
	}

	private void setTeam(Team team) {
		this.team = team;
	}

	public List<Player> getAlivelist() {
		return alivelist;
	}

	public HashMap<Player, Boolean> getCanBuy() {
		return canBuy;
	}

	public Objective getInfo() {
		return info;
	}

	private void setInfo(Objective info) {
		this.info = info;
	}
}
