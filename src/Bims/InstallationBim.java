package Bims;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import btooom.GameManager;

public class InstallationBim {
	public static GameManager GameManager;
	Block bim = null;
	Location loc;

	public void set(Player p, Block block){
		bim = block;
		search(p);
		new BukkitRunnable(){public void run(){
			if(bim != null){
				loc = bim.getLocation();
				Bukkit.getWorlds().get(0).createExplosion(loc.getX(),loc.getY(),loc.getZ(), 2.5F, false, false);
				bim.setType(Material.AIR);
				bim = null;
			}
		}}.runTaskLater(GameManager, 1200L);
	}
	public void search(Player p){
		new BukkitRunnable(){public void run(){
			if(bim == null){
				this.cancel();
				return;
			}
			Location loc = bim.getLocation();
			for(Entity e : Bukkit.getWorlds().get(0).getNearbyEntities(loc, 2F, 2F, 2F)){
				if((e instanceof Player && ((Player)e).getGameMode() == GameMode.SPECTATOR)
						||e == p)
					continue;
				Bukkit.getWorlds().get(0).createExplosion(loc.getX(),loc.getY(),loc.getZ(), 2.5F, false, false);
				bim.setType(Material.AIR);
				bim = null;
				this.cancel();
				break;
			}
		}}.runTaskTimer(GameManager, 0L, 1L);
	}
}
