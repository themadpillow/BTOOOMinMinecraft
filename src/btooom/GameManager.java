package btooom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BlockIterator;

import Bims.CrackerBim;
import Bims.FlameBim;
import Bims.HomingBim;
import Bims.InstallationBim;
import Bims.TimerBim;
import other.TitleSender;

public class GameManager extends JavaPlugin implements Listener {

	Items Items = new Items();
	other.Timer Timer = new other.Timer(this);
	TitleSender TitleSender = new TitleSender();

	public HashMap<Player, TimerBim> HandTimerBim = new HashMap<Player, TimerBim>();

	HashMap<Player, Boolean> canThrow = new HashMap<Player, Boolean>();
	HashMap<Player, Boolean> canBuy = new HashMap<Player, Boolean>();

	int money[];

	static boolean isStart = false;
	public static Scoreboard board = null;
	static Team team = null;
	public static Objective info = null;

	Location loc = null;

	static ArrayList<Player> alivelist = new ArrayList<Player>();

	public final String header = ChatColor.GREEN+"§l[BTOOOM] ";

	FileConfiguration config = this.getConfig();

	public void onEnable(){
		TimerBim TimerBim = new TimerBim();
		TimerBim.GameManager = this;
		CrackerBim CrackerBim = new CrackerBim();
		CrackerBim.GameManager = this;
		FlameBim FlameBim = new FlameBim();
		FlameBim.GameManager = this;
		InstallationBim InstallationBim = new InstallationBim();
		InstallationBim.GameManager = this;
		HomingBim HomingBim = new HomingBim();
		HomingBim.GameManager = this;

		if(config.get("Timer") == null){
			config.set("Timer", true);
			this.saveConfig();
		}

		Items.setBuyBimInventory();

		board = Bukkit.getScoreboardManager().getMainScoreboard();
		if(board.getTeam("team") == null)
			team = board.registerNewTeam("team");
		else
			team = board.getTeam("team");

		team.setAllowFriendlyFire(true);
		team.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OWN_TEAM);

		if((boolean)config.get("Timer")){
			if(board.getObjective(ChatColor.DARK_GREEN+"BTOOOM!") == null)
				info = board.registerNewObjective(ChatColor.DARK_GREEN+"BTOOOM!", "info");
			else
				info = board.getObjective(ChatColor.DARK_GREEN+"BTOOOM!");
			info.setDisplaySlot(DisplaySlot.SIDEBAR);

			Score score = info.getScore("試合開始前です");
			score.setScore(0);
		}

		loc = new Location(Bukkit.getWorlds().get(0), 0, 55, 0);

		Commands Commands = new Commands(this);
		getCommand("start").setExecutor(Commands);
		getCommand("set").setExecutor(Commands);
		getCommand("s").setExecutor(Commands);
		getCommand("shop").setExecutor(Commands);
		getCommand("spawn").setExecutor(Commands);
		getCommand("debug").setExecutor(Commands);

		Bukkit.getPluginManager().registerEvents(new Events(this), this);

		for(Entity entity : Bukkit.getWorlds().get(0).getEntities()){
			if(entity instanceof Player){
				if(((Player)entity).getGameMode() != GameMode.CREATIVE){
					((Player)entity).getInventory().clear();
					entity.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
					((Player)entity).setGameMode(GameMode.SURVIVAL);
				}
				canThrow.put((Player)entity, false);
				canBuy.put((Player)entity, false);
				((Player)entity).removePotionEffect(PotionEffectType.INVISIBILITY);

			}
		}

		for(Player p : Bukkit.getOnlinePlayers())
			p.getInventory().addItem(Items.bims((byte)4, (byte)1));
	}
	public void onDisable(){
		if(info != null)
			info.unregister();
		if(team != null)
			team.unregister();
	}

	public void start(){


		for(Player p : Bukkit.getOnlinePlayers()){

			if((boolean)config.get("Timer"))
				Timer.timer(20, 20);

			alivelist.add(p);
			canThrow.put(p, false);
			canBuy.put(p, true);

			p.getInventory().clear();
			p.setGameMode(GameMode.SURVIVAL);
			p.getInventory().setItem(0, Items.bims((byte)0, (byte)1));
			for(int i = 0; i < 9; i ++){
				p.getInventory().addItem(Items.bims((byte)0, (byte)1));
			}
			p.getInventory().setItem(8, Items.otherItem((byte)0));
			team.addPlayer(p);
			p.teleport(respawn());
		}

		money = new int[alivelist.size()];
		Arrays.fill(money, 30);
		new BukkitRunnable(){public void run(){
			for(int i = 0; i < money.length; i ++){
				money[i] += 5;
			}
		}}.runTaskTimer(this, 100L, 100L);

		Bukkit.broadcastMessage(header+ChatColor.RED+"20秒後に試合が開始します！");
		Bukkit.broadcastMessage(header+ChatColor.RED+"準備時間の間は/spawnでスポーンし直すことが出来ます");
		Bukkit.broadcastMessage(header+ChatColor.RED+"アイテムは試合開始後から使用可能です");

		new BukkitRunnable(){public void run(){
			isStart = true;
			Bukkit.broadcastMessage(header+ChatColor.LIGHT_PURPLE+"試合開始です");
			for(Player p : Bukkit.getOnlinePlayers()){
				canThrow.put(p, true);
				p.playSound(p.getLocation().add(0,5,0), Sound.FIREWORK_LARGE_BLAST, 1F, 1F);
				TitleSender.sendTitle(p, ChatColor.GREEN+"§lBTOOOM !", "");
			}
		}}.runTaskLater(this, 380);
		new BukkitRunnable(){public void run(){
			for(Player p : Bukkit.getOnlinePlayers())
				TitleSender.resetTitle(p);
		}}.runTaskLater(this, 440);
	}

	public Location respawn(){
		Random ran = new Random();
		int x = ran.nextInt(200);
		int z = ran.nextInt(200);
		if(ran.nextInt(2) == 0)
			x = -x;
		if(ran.nextInt(2) == 0)
			z = -z;
		for(int y = 0; ; y ++){
			Location reloc = loc.clone().add(x, y, z);
			if(reloc.getBlock().getType() == Material.STATIONARY_WATER){
				x = ran.nextInt(200);
				z = ran.nextInt(200);
				continue;
			}
			if(reloc.getBlock().getType() == Material.AIR){
				return reloc;
			}
		}
	}

	public Player getTargetedPlayer(Player player, int range) {

		// 視線の先にあるブロック一覧を取得する
		BlockIterator it = new BlockIterator(player, range);

		while ( it.hasNext() ) {
			Block block = it.next();

			if ( block.getType() != Material.AIR ) {
				// ブロックが見つかった(遮られている)、処理を終わってnullを返す
				return null;

			} else {
				// 位置が一致するPlayerがないか探す
				for ( Player target : Bukkit.getOnlinePlayers() ) {
					if(target == player
							||target.getGameMode() == GameMode.SPECTATOR){
						continue;
					}
					if ( block.getLocation().distanceSquared(target.getLocation()) <= 3.0
							|| block.getLocation().distanceSquared(target.getEyeLocation()) <= 3.0){
						// 見つかったPlayerを返す
						return target;
					}
				}
			}
		}

		// 何も見つからなかった
		return null;
	}


	public void gameover(Player winner, boolean sixstar){

		if((boolean)config.get("Timer"))
			Timer.TimerTaskID.cancel();
		GameManager.isStart = false;

		FileConfiguration config = this.getConfig();
		int star = 0;
		if(winner == null){
			for(Player p : Bukkit.getOnlinePlayers()){
				p.sendMessage(header+ChatColor.RED+"時間切れにより勝者が決定しました");
				if(p.getGameMode() == GameMode.SPECTATOR)
					continue;
				if(winner == null){
					winner = p;
					//	continue;
				}
				for(ItemStack item : p.getInventory().getContents()){
					if(item != null && item.getType() == Material.NETHER_STAR && item.getAmount() > star){
						winner = p;
						star = item.getAmount();
					}
				}
				p.teleport((Location) config.get("LobbyLocation"), TeleportCause.ENDER_PEARL);
				TitleSender.sendTitle(p, ChatColor.DARK_RED+"勝者:"+ChatColor.DARK_GREEN+winner.getName(), ChatColor.YELLOW+"獲得クリスタル数："+star+"個");
				p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 1F, 1F);

				p.getInventory().clear();

				p.setGameMode(GameMode.SURVIVAL);
			}
		}
		else if(sixstar){
			for(Player p : Bukkit.getOnlinePlayers()){
				for(ItemStack item : winner.getInventory().getContents()){
					if(item != null && item.getType() == Material.COMPASS){
						star = item.getAmount();
					}
				}
				for(Player all : Bukkit.getOnlinePlayers()){
					all.sendMessage(header+ChatColor.RED+winner.getName()+"さんがクリスタルを6個以上集めました");
					all.teleport((Location) config.get("LobbyLocation"), TeleportCause.ENDER_PEARL);
					TitleSender.sendTitle(all, ChatColor.DARK_RED+"勝者:"+ChatColor.DARK_GREEN+winner.getName(), ChatColor.YELLOW+"獲得クリスタル数："+star+"個");
					all.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 1F, 1F);
					all.getInventory().clear();
					all.setGameMode(GameMode.SURVIVAL);
				}
			}
		}
		else{
			for(Player all : Bukkit.getOnlinePlayers()){
				all.sendMessage(header+ChatColor.RED+winner.getName()+"さんが最後の生存者です");
				all.teleport((Location) config.get("LobbyLocation"), TeleportCause.ENDER_PEARL);
				TitleSender.sendTitle(all, ChatColor.DARK_RED+"勝者:"+ChatColor.DARK_GREEN+winner.getName(), ChatColor.YELLOW+"獲得クリスタル数："+star+"個");
				all.playSound(all.getLocation(), Sound.FIREWORK_LAUNCH, 1F, 1F);
				all.getInventory().clear();

				all.setGameMode(GameMode.SURVIVAL);
			}

		}

		for(Entity entity : Bukkit.getWorlds().get(0).getEntities()){
			if(entity instanceof Player){
				if(((Player)entity).getGameMode() != GameMode.CREATIVE){
					((Player)entity).getInventory().clear();
					entity.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
					((Player)entity).setGameMode(GameMode.SURVIVAL);
				}
				canThrow.put((Player)entity, false);
				canBuy.put((Player)entity, false);
				((Player)entity).removePotionEffect(PotionEffectType.INVISIBILITY);

			}
			else if(entity instanceof Item){
				entity.remove();
			}
		}
	}
}
