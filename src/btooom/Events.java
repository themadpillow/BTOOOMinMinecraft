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
import org.bukkit.event.block.Action;
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
import org.bukkit.scoreboard.DisplaySlot;

import bims.BimConfig;
import bims.Bims;
import bims.CrackerBim;
import bims.FlameBim;
import bims.HomingBim;
import bims.InstallationBim;
import bims.TimerBim;
import net.md_5.bungee.api.ChatColor;

public class Events implements Listener {
	private GameManager GameManager;

	public Events(GameManager instance) {
		GameManager = instance;
	}

	@EventHandler
	public void join(PlayerJoinEvent e) {
		if (GameManager.isStart()) {
			e.getPlayer().setGameMode(GameMode.SPECTATOR);
			e.getPlayer().teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
			e.getPlayer().sendMessage(GameManager.getHeader() + ChatColor.GRAY + "既に試合が開始されているため観戦者になりました");
		}
	}

	@EventHandler
	public void BimEvent(PlayerInteractEvent e) {
		if (e.getItem() == null) {
			return;
		}
		e.setCancelled(true);

		if (e.getAction() == Action.LEFT_CLICK_AIR
				|| e.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (e.getItem().getType() == Bims.TimerBim.getMaterial()) {
				if (GameManager.getHandTimerBim().get(e.getPlayer()) == null) {
					TimerBim TimerBim = new TimerBim(GameManager);
					GameManager.getHandTimerBim().put(e.getPlayer(), TimerBim);
					TimerBim.count(e.getPlayer());
				}
			} else if (e.getItem().getType() == Material.COMPASS) {
				e.getPlayer().openInventory(GameManager.getItems().getBuyBimInventory());
			}
		} else if (e.getAction() == Action.RIGHT_CLICK_AIR
				|| e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (GameManager.getCanThrow().get(e.getPlayer()) != null
					&& (GameManager.getCanThrow().get(e.getPlayer())
							|| e.getPlayer().getGameMode() == GameMode.CREATIVE)) {
				if (e.getItem().getType() == Bims.TimerBim.getMaterial()) {
					if (GameManager.getHandTimerBim().get(e.getPlayer()) != null
							&& ((TimerBim) GameManager.getHandTimerBim().get(e.getPlayer())).getBim() == null) {
						((TimerBim) GameManager.getHandTimerBim().get(e.getPlayer())).Throw(e.getPlayer());
					} else {
						TimerBim TimerBim = new TimerBim(GameManager);
						TimerBim.Throw(e.getPlayer());
					}
				} else if (e.getItem().getType() == Bims.CrackerBim.getMaterial()) {
					CrackerBim CrackerBim = new CrackerBim(GameManager);
					CrackerBim.Throw(e.getPlayer());
				} else if (e.getItem().getType() == Bims.HomingBim.getMaterial()) {
					Player target = HomingBim.getTargetedPlayer(e.getPlayer(), 30);
					if (target == null) {
						return;
					}
					HomingBim HomingBim = new HomingBim(GameManager);
					HomingBim.Throw(e.getPlayer(), target);
				} else if (e.getItem().getType() == Bims.FlameBim.getMaterial()) {
					FlameBim FlameBim = new FlameBim(GameManager);
					FlameBim.Throw(e.getPlayer());
				} else if (e.getItem().getType() == Material.COMPASS) {
					double distance = -1;
					Player nearPlayer = null;
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (player.getGameMode() == GameMode.SPECTATOR
								|| player == e.getPlayer()) {
							continue;
						}
						if (distance == -1 || player.getLocation().distanceSquared(e.getPlayer().getLocation()) < distance) {
							nearPlayer = player;
							distance = player.getLocation().distanceSquared(e.getPlayer().getLocation());
						}
					}
					if (nearPlayer == null) {
						e.getPlayer().sendMessage("誰も見つかりませんでした");
					} else {
						e.getPlayer().sendMessage("" + nearPlayer.getName() + "が見つかりました");
						e.getPlayer().setCompassTarget(nearPlayer.getLocation());
						e.getPlayer().updateInventory();
						e.getPlayer().sendMessage(""+e.getPlayer().getCompassTarget());
					}
				}
			}
			GameManager.getCanThrow().put(e.getPlayer(), false);
			Bukkit.getScheduler().runTaskLater(GameManager, () -> {
				GameManager.getCanThrow().put(e.getPlayer(), true);
			}, 20L);
		}
	}

	@EventHandler
	public void installationBim(BlockPlaceEvent e) {
		if (e.getBlock().getType() != Material.SKULL
				&& e.getPlayer().getGameMode() != GameMode.CREATIVE) {
			e.setCancelled(true);
			return;
		}

		Block block = e.getBlockPlaced();
		InstallationBim InstallationBim = new InstallationBim(GameManager);
		InstallationBim.set(e.getPlayer(), block);
	}

	@EventHandler
	public void damage(EntityDamageEvent e) {
		if (e.getCause() == DamageCause.CUSTOM) {
			return;
		}
		if (GameManager.isStart() == false) {
			e.setCancelled(true);
			return;
		}
		if (e.getEntity() instanceof Player) {
			e.setCancelled(true);
			((Player) e.getEntity()).damage(e.getDamage() * 0.7);
		} else if (e.getEntity() instanceof Item) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void damage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Snowball) {
			e.setCancelled(true);
			if (e.getEntity().getMetadata("type") != null) {
				if (e.getEntity().getMetadata("type").get(0).asString().equalsIgnoreCase("crack")) {
					Bukkit.getWorlds().get(0).createExplosion(e.getEntity().getLocation().getX(),
							e.getEntity().getLocation().getY(), e.getEntity().getLocation().getZ(),
							BimConfig.getDamage(Bims.CrackerBim), false, false);
				} else {
					return;
				}
				e.getEntity().remove();
			}
		}
	}

	@EventHandler
	public void hitCrackerorHoming(ProjectileHitEvent e) {
		if (e.getEntity() instanceof Snowball) {
			if (e.getEntity().getMetadata("type") != null) {
				Location loc = e.getEntity().getLocation();

				String type = e.getEntity().getMetadata("type").get(0).asString();
				if (type.equals("crack")) {
					Bukkit.getWorlds().get(0).createExplosion(loc.getX(), loc.getY(), loc.getZ(),
							BimConfig.getDamage(Bims.CrackerBim), false, false);
				} else if (type.equals("homing")) {
					Bukkit.getWorlds().get(0).createExplosion(loc.getX(), loc.getY(), loc.getZ(),
							BimConfig.getDamage(Bims.HomingBim), false, false);
				} else {
					return;
				}
			}
			e.getEntity().remove();
		}
	}

	@EventHandler
	public void itemheld(PlayerItemHeldEvent e) {
		GameManager.getHandTimerBim().put(e.getPlayer(), null);
	}

	@EventHandler
	public void quit(PlayerQuitEvent e) {
		e.getPlayer().getInventory().clear();

		if (e.getPlayer().getScoreboard() != null) {
			e.getPlayer().getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();
		}
		if (GameManager.isStart()) {
			GameManager.getAlivelist().remove(e.getPlayer());
			Bukkit.broadcastMessage(GameManager.getHeader() + ChatColor.RED + e.getPlayer().getName() + "さんが死亡しました"
					+ ChatColor.YELLOW + "(ログアウト)");

			e.getPlayer().setGameMode(GameMode.SPECTATOR);

			if (GameManager.getAlivelist().size() == 1) {
				GameManager.gameover(GameManager.getAlivelist().get(0), false);
			}
		}
	}

	@EventHandler
	public void BuyBimEvent(InventoryClickEvent e) {
		if (e.getClickedInventory() == null) {
			return;
		}
		if (e.getClickedInventory().getName().contains("BIM購入")) {
			e.setCancelled(true);

			if (e.getCurrentItem() == null
					|| !GameManager.getCanBuy().get(e.getWhoClicked())) {
				return;
			}

			Player player = (Player) e.getWhoClicked();
			if (e.getCurrentItem().getType() == Bims.TimerBim.getMaterial()) {
				if (GameManager.getMoney(player) < BimConfig.getPrice(Bims.TimerBim)) {
					e.getWhoClicked().closeInventory();
					e.getWhoClicked().sendMessage(GameManager.getHeader() + ChatColor.LIGHT_PURPLE + "所持金が足りません");
				} else {
					GameManager.addMoney(player, -BimConfig.getPrice(Bims.TimerBim));
					e.getWhoClicked().getInventory().addItem(GameManager.getItems().bims(Bims.TimerBim, (byte) 1));
				}
			} else if (e.getCurrentItem().getType() == Bims.CrackerBim.getMaterial()) {
				if (GameManager.getMoney(player) < BimConfig.getPrice(Bims.CrackerBim)) {
					e.getWhoClicked().closeInventory();
					e.getWhoClicked().sendMessage(GameManager.getHeader() + ChatColor.LIGHT_PURPLE + "所持金が足りません");
				} else {
					GameManager.addMoney(player, -BimConfig.getPrice(Bims.CrackerBim));
					e.getWhoClicked().getInventory()
							.addItem(GameManager.getItems().bims(Bims.CrackerBim, (byte) 1));
				}
			} else if (e.getCurrentItem().getType() == Bims.FlameBim.getMaterial()) {
				if (GameManager.getMoney(player) < BimConfig.getPrice(Bims.FlameBim)) {
					e.getWhoClicked().closeInventory();
					e.getWhoClicked().sendMessage(GameManager.getHeader() + ChatColor.LIGHT_PURPLE + "所持金が足りません");
				} else {
					GameManager.addMoney(player, -BimConfig.getPrice(Bims.FlameBim));
					e.getWhoClicked().getInventory().addItem(GameManager.getItems().bims(Bims.FlameBim, (byte) 1));
				}
			} else if (e.getCurrentItem().getType() == Bims.HomingBim.getMaterial()) {
				if (GameManager.getMoney(player) < BimConfig.getPrice(Bims.HomingBim)) {
					e.getWhoClicked().closeInventory();
					e.getWhoClicked().sendMessage(GameManager.getHeader() + ChatColor.LIGHT_PURPLE + "所持金が足りません");
				} else {
					GameManager.addMoney(player, -BimConfig.getPrice(Bims.HomingBim));
					e.getWhoClicked().getInventory().addItem(GameManager.getItems().bims(Bims.HomingBim, (byte) 1));
				}
			} else if (e.getCurrentItem().getType() == Bims.InstallationBim.getMaterial()) {
				if (GameManager.getMoney(player) < BimConfig.getPrice(Bims.InstallationBim)) {
					e.getWhoClicked().closeInventory();
					e.getWhoClicked().sendMessage(GameManager.getHeader() + ChatColor.LIGHT_PURPLE + "所持金が足りません");
				} else {
					GameManager.addMoney(player, -BimConfig.getPrice(Bims.InstallationBim));
					e.getWhoClicked().getInventory()
							.addItem(GameManager.getItems().bims(Bims.InstallationBim, (byte) 1));
				}

				GameManager.getCanBuy().put((Player) e.getWhoClicked(), false);
				Bukkit.getScheduler().runTaskLater(GameManager, () -> {
					GameManager.getCanBuy().put((Player) e.getWhoClicked(), true);
				}, 3L);
			}
		}
	}

	@EventHandler
	public void Death(PlayerDeathEvent e) {
		GameManager.getAlivelist().remove(e.getEntity());
		e.setDeathMessage(GameManager.getHeader() + ChatColor.RED + e.getEntity().getName() + "さんが死亡しました");

		Bukkit.getScheduler().runTaskLater(GameManager, () -> {
			e.getEntity().getInventory().clear();
			e.getEntity().setBedSpawnLocation(e.getEntity().getLocation(), true);
			e.getEntity().spigot().respawn();
			e.getEntity().setGameMode(GameMode.SPECTATOR);
		}, 1L);

		if (GameManager.getAlivelist().size() == 1) {
			GameManager.gameover(GameManager.getAlivelist().get(0), false);
		}
	}

	@EventHandler
	public void drop(PlayerDropItemEvent e) {
		if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void pickup(PlayerPickupItemEvent e) {
		if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
			if (e.getItem().getMetadata("nopickup").size() != 0) {
				e.setCancelled(true);
			}
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
			e.getPlayer().sendMessage(GameManager.getHeader() + ChatColor.RED + "ブロックの破壊は許可されていません");
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