package com.enderdevelopment.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class InteractionListener implements Listener {
    public static NamespacedKey COCKROACH_KEY;
    private final JavaPlugin plugin;

    public InteractionListener(JavaPlugin plugin) {
        this.plugin = plugin;
        COCKROACH_KEY = new NamespacedKey(plugin, "cockroach");
    }

    @EventHandler
    public void onEntityClick(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || !(event.getRightClicked() instanceof LivingEntity target)) {
            return;
        }
        ItemStack held = event.getPlayer().getInventory().getItemInMainHand();
        if (!isMaraca(held) || !isCockroach(target)) {
            return;
        }
        event.setCancelled(true);
        putOnMariachiHat(event.getPlayer(), target);
    }

    private boolean isMaraca(ItemStack item) {
        if (item.getType() != Material.BAMBOO || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta.displayName() != null && meta.displayName().equals(Component.text("Mexican Maraca", NamedTextColor.GOLD));
    }

    private boolean isCockroach(Entity entity) {
        Integer marker = entity.getPersistentDataContainer().get(COCKROACH_KEY, PersistentDataType.INTEGER);
        return marker != null && marker == 1;
    }

    private void putOnMariachiHat(Player player, LivingEntity cockroach) {
        ItemStack hat = new ItemStack(Material.LEATHER_HELMET);
        ItemMeta meta = hat.getItemMeta();
        meta.displayName(Component.text("Mariachi Hat", NamedTextColor.RED));
        hat.setItemMeta(meta);
        if (cockroach.getEquipment() != null) {
            cockroach.getEquipment().setHelmet(hat, true);
        }
        cockroach.getWorld().spawnParticle(Particle.NOTE, cockroach.getLocation().add(0, 1, 0), 12, 0.35, 0.4, 0.35, 1);
        cockroach.getWorld().playSound(cockroach.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, SoundCategory.RECORDS, 1.2f, 1.1f);
        playMariachiTune(cockroach);
        player.getInventory().addItem(createAlbum());
        player.sendMessage(Component.text("The cockroach put on a mariachi hat. The Mexican music album was added to your inventory.", NamedTextColor.GOLD));
    }

    private ItemStack createAlbum() {
        ItemStack album = new ItemStack(Material.MUSIC_DISC_CAT);
        ItemMeta meta = album.getItemMeta();
        meta.displayName(Component.text("Mexican Music Album", NamedTextColor.RED));
        meta.lore(java.util.List.of(Component.text("A mariachi-inspired album.", NamedTextColor.YELLOW)));
        album.setItemMeta(meta);
        return album;
    }

    private void playMariachiTune(LivingEntity source) {
        for (int i = 0; i < 8; i++) {
            final float pitch = new float[] {1.0f, 1.25f, 1.5f, 1.25f, 1.0f, 1.5f, 1.75f, 1.5f}[i];
            long delay = i * 6L;
            plugin.getServer().getScheduler().runTaskLater(plugin,
                    () -> source.getWorld().playSound(source.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR,
                            SoundCategory.RECORDS, 1.0f, pitch), delay);
        }
    }
}
