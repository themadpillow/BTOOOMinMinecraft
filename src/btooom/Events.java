package btooom;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

import Bims.CrackerBim;
import Bims.FlameBim;
import Bims.HomingBim;
import Bims.InstallationBim;
import Bims.TimerBim;
import net.md_5.bungee.api.ChatColor;

public class Events implements Listener {
	public static GameManager GameManager;

	public Events(GameManager instance) {
		GameManager = instance;
	}

	@EventHandler
	public void join(PlayerJoinEvent e) {
		if (GameManager.isStart) {
			e.getPlayer().setGameMode(GameMode.SPECTATOR);
			e.getPlayer().teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
			e.getPlayer().sendMessage(GameManager.header + ChatColor.GRAY + "既に試合が開始されているため観戦者になりました");
		}
	}

	@EventHandler
	public void BimEvent(PlayerInteractEvent e) {
		if (e.getItem() == null) {
			return;
		}
		switch (e.getAction()) {
		case LEFT_CLICK_AIR:
		case LEFT_CLICK_BLOCK:
			switch (e.getItem().getType()) {
			case COAL: // TimerBim
				if (GameManager.HandTimerBim.get(e.getPlayer()) == null) {
					TimerBim TimerBim = new TimerBim();
					GameManager.HandTimerBim.put(e.getPlayer(), TimerBim);
					TimerBim.count(e.getPlayer());
				}
				break;
			}
			break;
		case RIGHT_CLICK_AIR:
		case RIGHT_CLICK_BLOCK:
			if (GameManager.canThrow.get(e.getPlayer()) != null
					&& (GameManager.canThrow.get(e.getPlayer()) || e.getPlayer().getGameMode() == GameMode.CREATIVE)) {
				switch (e.getItem().getType()) {
				case COAL: // TimerBim
					if (GameManager.HandTimerBim.get(e.getPlayer()) != null
							&& ((TimerBim) GameManager.HandTimerBim.get(e.getPlayer())).bim == null) {
						((TimerBim) GameManager.HandTimerBim.get(e.getPlayer())).Throw(e.getPlayer());
					} else {
						TimerBim TimerBim = new TimerBim();
						TimerBim.Throw(e.getPlayer());
					}
					break;
				case FLINT:
					CrackerBim CrackerBim = new CrackerBim();
					CrackerBim.Throw(e.getPlayer());
					break;
				case SLIME_BALL:
					Player target = GameManager.getTargetedPlayer(e.getPlayer(), 30);
					if (target == null) {
						return;
					}
					HomingBim HomingBim = new HomingBim();
					HomingBim.Throw(e.getPlayer(), target);
					break;
				case FIREBALL:
					FlameBim FlameBim = new FlameBim();
					FlameBim.Throw(e.getPlayer());
					break;
				case COMPASS:
					double distance = -1;
					Player nearPlayer = null;
					for (Player all : Bukkit.getOnlinePlayers()) {
						if (all.getGameMode() == GameMode.SPECTATOR || all == e.getPlayer()) {
							continue;
						}
						if (distance == -1) {
							nearPlayer = all;
							distance = all.getLocation().distanceSquared(e.getPlayer().getLocation());
							continue;
						}
						if (all.getLocation().distanceSquared(e.getPlayer().getLocation()) < distance) {
							nearPlayer = all;
							distance = all.getLocation().distanceSquared(e.getPlayer().getLocation());
						}
					}
					if (nearPlayer == null) {
						e.getPlayer().sendMessage("誰も見つかりませんでした");
						return;
					}
					e.getPlayer().sendMessage("" + nearPlayer.getName() + "が見つかりました");
					e.getPlayer().setCompassTarget(nearPlayer.getLocation());
					distance = 0;
					nearPlayer = null;
					return;
				default:
					return;
				}
				e.setCancelled(true);
				GameManager.canThrow.put(e.getPlayer(), false);
				new BukkitRunnable() {
					public void run() {
						GameManager.canThrow.put(e.getPlayer(), true);
					}
				}.runTaskLater(GameManager, 20L);
			}
			break;
		}
	}

	@EventHandler
	public void installationBim(BlockPlaceEvent e) {
		if (e.getBlock().getType() != Material.SKULL) {
			if (e.getPlayer().getGameMode() != GameMode.CREATIVE)
				e.setCancelled(true);
			return;
		}
		Block block = e.getBlockPlaced();
		InstallationBim InstallationBim = new InstallationBim();
		InstallationBim.set(e.getPlayer(), block);
	}

	@EventHandler
	public void damage(EntityDamageEvent e) {
		if (e.getCause() == DamageCause.CUSTOM) {
			return;
		}
		if (GameManager.isStart == false) {
			e.setCancelled(true);
			return;
		}
		if (e.getEntity() instanceof Player) {
			e.setCancelled(true);
			((Player) e.getEntity()).damage(e.getDamage() * 0.7);
		} else if (e.getEntity() instanceof Item)
			e.setCancelled(true);
	}

	@EventHandler
	public void damage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Snowball) {
			e.setCancelled(true);
			if (e.getEntity().getMetadata("type") != null) {
				switch (e.getEntity().getMetadata("type").get(0).asString()) {
				case "crack":
					Bukkit.getWorlds().get(0).createExplosion(e.getEntity().getLocation().getX(),
							e.getEntity().getLocation().getY(), e.getEntity().getLocation().getZ(), 1.4F, false, false);
					break;
				default:
					return;
				}
				e.getEntity().remove();
			}
		}
	}

	@EventHandler
	public void hitCrackororHoming(ProjectileHitEvent e) {
		if (e.getEntity() instanceof Snowball) {
			if (e.getEntity().getMetadata("type") != null) {
				Location loc = e.getEntity().getLocation();
				switch (e.getEntity().getMetadata("type").get(0).asString()) {
				case "crack":
					Bukkit.getWorlds().get(0).createExplosion(loc.getX(), loc.getY(), loc.getZ(), 1.4F, false, false);
					break;
				case "homing":
					Bukkit.getWorlds().get(0).createExplosion(loc.getX(), loc.getY(), loc.getZ(), 1.4F, false, false);
					break;
				default:
					return;
				}
			}
			e.getEntity().remove();
		}
	}

	@EventHandler
	public void itemheld(PlayerItemHeldEvent e) {
		if (e.getPlayer().getInventory().getItem(e.getNewSlot()) != null
				&& e.getPlayer().getInventory().getItem(e.getNewSlot()).getType() == Material.COMPASS) {
			ActionBarAPI.sendActionBar(e.getPlayer(),
					"所持金：" + GameManager.money[GameManager.alivelist.indexOf(e.getPlayer())]);
		}
		GameManager.HandTimerBim.put(e.getPlayer(), null);
	}

	@EventHandler
	public void queit(PlayerQuitEvent e) {
		e.getPlayer().getInventory().clear();
		if (GameManager.team.hasPlayer(e.getPlayer())) {
			GameManager.team.removePlayer(e.getPlayer());
		}
		if (GameManager.isStart) {
			GameManager.alivelist.remove(e.getPlayer());
			Bukkit.broadcastMessage(GameManager.header + ChatColor.RED + e.getPlayer().getName() + "さんが死亡しました"
					+ ChatColor.YELLOW + "(ログアウト)");

			e.getPlayer().setGameMode(GameMode.SPECTATOR);

			int i = 0;
			Player winner = null;
			for (Player p : GameManager.alivelist) {
				if (p != null) {
					i++;
					if (i > 1) {
						return;
					}
					winner = p;
				}
			}
			GameManager.gameover(winner, false);
		}
	}

	@EventHandler
	public void BuyBimEvent(InventoryClickEvent e) {
		if (e.getClickedInventory() == null) {
			return;
		}
		if (e.getClickedInventory().getName().contains("BIM購入")) {
			if (e.getCurrentItem() == null) {
				return;
			}
			e.setCancelled(true);
			if (GameManager.canBuy.get(e.getWhoClicked())) {
				switch (e.getCurrentItem().getType()) {
				case COAL:
					if (GameManager.money[GameManager.alivelist.indexOf(e.getWhoClicked())] < 6) {
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().sendMessage(GameManager.header + ChatColor.LIGHT_PURPLE + "所持金が足りません");
						break;
					}
					GameManager.money[GameManager.alivelist.indexOf(e.getWhoClicked())] -= 6;
					e.getWhoClicked().getInventory().addItem(GameManager.Items.bims((byte) 0, (byte) 1));
					break;
				case FLINT:
					if (GameManager.money[GameManager.alivelist.indexOf(e.getWhoClicked())] < 10) {
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().sendMessage(GameManager.header + ChatColor.LIGHT_PURPLE + "所持金が足りません");
						break;
					}
					GameManager.money[GameManager.alivelist.indexOf(e.getWhoClicked())] -= 10;
					e.getWhoClicked().getInventory().addItem(GameManager.Items.bims((byte) 1, (byte) 1));
					break;
				case FIREBALL:
					if (GameManager.money[GameManager.alivelist.indexOf(e.getWhoClicked())] < 15) {
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().sendMessage(GameManager.header + ChatColor.LIGHT_PURPLE + "所持金が足りません");
						break;
					}
					GameManager.money[GameManager.alivelist.indexOf(e.getWhoClicked())] -= 15;
					e.getWhoClicked().getInventory().addItem(GameManager.Items.bims((byte) 2, (byte) 1));
					break;
				case SLIME_BALL:
					if (GameManager.money[GameManager.alivelist.indexOf(e.getWhoClicked())] < 20) {
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().sendMessage(GameManager.header + ChatColor.LIGHT_PURPLE + "所持金が足りません");
						break;
					}
					GameManager.money[GameManager.alivelist.indexOf(e.getWhoClicked())] -= 20;
					e.getWhoClicked().getInventory().addItem(GameManager.Items.bims((byte) 3, (byte) 1));
					break;
				case SKULL_ITEM:
					if (GameManager.money[GameManager.alivelist.indexOf(e.getWhoClicked())] < 30) {
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().sendMessage(GameManager.header + ChatColor.LIGHT_PURPLE + "所持金が足りません");
						break;
					}
					GameManager.money[GameManager.alivelist.indexOf(e.getWhoClicked())] -= 30;
					e.getWhoClicked().getInventory().addItem(GameManager.Items.bims((byte) 4, (byte) 1));
					break;
				}
				GameManager.canBuy.put((Player) e.getWhoClicked(), false);
				new BukkitRunnable() {
					public void run() {
						GameManager.canBuy.put((Player) e.getWhoClicked(), true);
					}
				}.runTaskLater(GameManager, 3L);
			}
		}
	}

	@EventHandler
	public void Death(PlayerDeathEvent e) {
		GameManager.team.removePlayer(e.getEntity());
		GameManager.alivelist.remove(e.getEntity());
		e.setDeathMessage(GameManager.header + ChatColor.RED + e.getEntity().getName() + "さんが死亡しました");

		new BukkitRunnable() {
			public void run() {
				e.getEntity().setBedSpawnLocation(e.getEntity().getLocation(), true);
				e.getEntity().spigot().respawn();
				e.getEntity().setGameMode(GameMode.SPECTATOR);
			}
		}.runTaskLater(GameManager, 1L);

		if (GameManager.alivelist.size() == 1) {
			GameManager.gameover(GameManager.alivelist.get(0), false);
		}
	}

	/*
	 * @EventHandler public void CantHalfClick(InventoryClickEvent e){
	 * if(e.getAction() == InventoryAction.PICKUP_HALF){ e.setCancelled(true); }
	 * }
	 */
	@EventHandler
	public void drop(PlayerDropItemEvent e) {
		if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void pickup(PlayerPickupItemEvent e) {
		if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
			if (e.getItem().getMetadata("nopickup").size() != 0)
				e.setCancelled(true);
		}
		/*
		 * if (e.getItem().getItemStack().getType() == Material.COMPASS) { new
		 * BukkitRunnable() { public void run() { for (ItemStack item :
		 * e.getPlayer().getInventory().getContents()) { if (item != null &&
		 * item.getType() == Material.COMPASS) { if (item.getAmount() > 5) {
		 * GameManager.gameover(e.getPlayer(), true); } } } }
		 * }.runTaskLater(GameManager, 1L); }
		 */
	}

	@EventHandler
	public void BlockBreak(BlockBreakEvent e) {
		if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
			e.getPlayer().sendMessage(GameManager.header + ChatColor.RED + "ブロックの破壊は許可されていません");
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void food(FoodLevelChangeEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void burn(BlockSpreadEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void burn(BlockBurnEvent e) {
		e.setCancelled(true);
	}
}