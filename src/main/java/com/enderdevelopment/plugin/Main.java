package com.enderdevelopment.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Cat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class Main extends JavaPlugin {
    private static final String COCKROACH_NAME = "Cockroach";
    private BukkitTask catAnimationTask;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new InteractionListener(this), this);
        getLogger().info("Mariachi Menagerie enabled");
    }

    @Override
    public void onDisable() {
        if (catAnimationTask != null) {
            catAnimationTask.cancel();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command is for players only.", NamedTextColor.RED));
            return true;
        }
        switch (command.getName().toLowerCase()) {
            case "maraca" -> {
                player.getInventory().addItem(createMaraca());
                player.sendMessage(Component.text("You received a Mexican-style maraca.", NamedTextColor.GOLD));
            }
            case "cockroach" -> summonCockroach(player);
            case "persiancat" -> summonPersianCat(player);
            default -> { return false; }
        }
        return true;
    }

    private ItemStack createMaraca() {
        ItemStack maraca = new ItemStack(Material.BAMBOO);
        ItemMeta meta = maraca.getItemMeta();
        meta.displayName(Component.text("Mexican Maraca", NamedTextColor.GOLD));
        meta.lore(java.util.List.of(Component.text("Click a cockroach to start the fiesta.", NamedTextColor.YELLOW)));
        maraca.setItemMeta(meta);
        return maraca;
    }

    private ItemStack createAlbum() {
        ItemStack album = new ItemStack(Material.MUSIC_DISC_CAT);
        ItemMeta meta = album.getItemMeta();
        meta.displayName(Component.text("Mexican Music Album", NamedTextColor.RED));
        meta.lore(java.util.List.of(Component.text("A mariachi-inspired album.", NamedTextColor.YELLOW)));
        album.setItemMeta(meta);
        return album;
    }

    private void summonCockroach(Player player) {
        Silverfish cockroach = (Silverfish) player.getWorld().spawnEntity(player.getLocation(), EntityType.SILVERFISH);
        cockroach.customName(Component.text(COCKROACH_NAME, NamedTextColor.DARK_GRAY));
        cockroach.setCustomNameVisible(true);
        cockroach.getPersistentDataContainer().set(InteractionListener.COCKROACH_KEY,
                org.bukkit.persistence.PersistentDataType.INTEGER, 1);
        player.sendMessage(Component.text("A cockroach scurries in. Click it with the maraca.", NamedTextColor.GRAY));
    }

    private void summonPersianCat(Player player) {
        Cat cat = (Cat) player.getWorld().spawnEntity(player.getLocation(), EntityType.CAT);
        cat.setCatType(Cat.Type.PERSIAN);
        cat.customName(Component.text("Persian Cat", NamedTextColor.LIGHT_PURPLE));
        cat.setCustomNameVisible(true);
        cat.setSitting(true);
        startCatAnimation(cat);
        player.sendMessage(Component.text("The Persian cat lies down and starts scratching.", NamedTextColor.LIGHT_PURPLE));
    }

    private void startCatAnimation(Cat cat) {
        if (catAnimationTask != null) {
            catAnimationTask.cancel();
        }
        catAnimationTask = Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            private boolean scratching;

            @Override
            public void run() {
                if (!cat.isValid() || cat.isDead()) {
                    catAnimationTask.cancel();
                    return;
                }
                scratching = !scratching;
                cat.setSitting(!scratching);
                Location location = cat.getLocation().add(0, 0.15, 0);
                World world = cat.getWorld();
                if (scratching) {
                    world.spawnParticle(Particle.CRIT, location, 5, 0.18, 0.08, 0.18, 0.02);
                    world.playSound(location, Sound.ENTITY_CAT_PURR, SoundCategory.BLOCKS, 0.8f, 1.2f);
                } else {
                    world.playSound(location, Sound.ENTITY_CAT_AMBIENT, SoundCategory.AMBIENT, 0.6f, 0.9f);
                }
            }
        }, 20L, 40L);
    }
}
