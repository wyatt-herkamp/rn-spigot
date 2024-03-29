package me.kingtux.redditnobility;

import dev.nitrocommand.bukkit.BukkitCommandCore;
import me.kingtux.redditnobility.auth.AuthCommand;
import me.kingtux.redditnobility.auth.AuthManager;
import me.kingtux.tuxcore.TuxCore;
import me.kingtux.tuxorm.TOConnection;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class RedditNobility extends JavaPlugin {
    private BukkitCommandCore bukkitCommandCore;
    private TuxCore tuxCore;

    @Override
    public void onEnable() {

        saveDefaultConfig();
        InfiniteLight infiniteLight = new InfiniteLight(this);

        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new BlockListeners(this), this);
        getServer().getPluginManager().registerEvents(new EntityListeners(), this);
        getServer().getPluginManager().registerEvents(infiniteLight, this);
        loadSpawners();
        bukkitCommandCore = new BukkitCommandCore(this);
        bukkitCommandCore.registerCommand(infiniteLight);
        tuxCore = (TuxCore) getServer().getPluginManager().getPlugin("TuxCore");
        if (getConfig().getBoolean("require-auth", true)) {
            var auth = new AuthManager(this);
            bukkitCommandCore.registerCommand(new AuthCommand(this, auth));
            getServer().getPluginManager().registerEvents(auth, this);
        }
    }

    public TOConnection getConnection() {
        return tuxCore.getCommonConnection();
    }

    private void loadSpawners() {
        getServer().addRecipe(createSpawnerRecipe(EntityType.CHICKEN, Material.EGG));
        getServer().addRecipe(createSpawnerRecipe(EntityType.PIG, Material.PORKCHOP));
        getServer().addRecipe(createSpawnerRecipe(EntityType.SKELETON, Material.BONE));
        getServer().addRecipe(createSpawnerRecipe(EntityType.ZOMBIE, Material.ROTTEN_FLESH));
        getServer().addRecipe(createSpawnerRecipe(EntityType.ENDERMAN, Material.ENDER_PEARL));
        getServer().addRecipe(createSpawnerRecipe(EntityType.BLAZE, Material.BLAZE_ROD));
        getServer().addRecipe(createSpawnerRecipe(EntityType.CREEPER, Material.GUNPOWDER));
        getServer().addRecipe(createSpawnerRecipe(EntityType.SPIDER, Material.STRING));
        getServer().addRecipe(createSpawnerRecipe(EntityType.RABBIT, Material.RABBIT));
        getServer().addRecipe(createSpawnerRecipe(EntityType.SHEEP, Material.WHITE_WOOL));
        getServer().addRecipe(createSpawnerRecipe(EntityType.COW, Material.BEEF));
        getServer().addRecipe(createSpawnerRecipe(EntityType.MUSHROOM_COW, Material.SUSPICIOUS_STEW));
        getServer().addRecipe(createSpawnerRecipe(EntityType.ZOMBIFIED_PIGLIN, Material.GOLDEN_SWORD));
        getServer().addRecipe(createSpawnerRecipe(EntityType.WITHER_SKELETON, Material.WITHER_SKELETON_SKULL));
        getServer().addRecipe(createSpawnerRecipe(EntityType.VILLAGER, Material.TOTEM_OF_UNDYING));
        getServer().addRecipe(createSpawnerRecipe(EntityType.ZOMBIE_VILLAGER, Material.EMERALD));
        getServer().addRecipe(createSpawnerRecipe(EntityType.SLIME, Material.SLIME_BALL));
        getServer().addRecipe(createSpawnerRecipe(EntityType.WITCH, Material.GLOWSTONE_DUST));
        getServer().addRecipe(createSpawnerRecipe(EntityType.DROWNED, Material.HEART_OF_THE_SEA));

    }

    public ShapedRecipe createSpawnerRecipe(EntityType type, Material core) {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(this, type.getKey().getKey().toLowerCase() + "_spawner"), createSpawner(type));
        recipe.shape("III", "ISI", "III");
        recipe.setIngredient('I', Material.IRON_BLOCK);
        recipe.setIngredient('S', core);
        return recipe;
    }

    public static ItemStack createSpawner(EntityType type) {
        ItemStack spawner = new ItemStack(Material.SPAWNER, 1);
        ItemMeta itemMeta = spawner.getItemMeta();
        itemMeta.displayName(Component.text(type.name() + " Spawner"));

        spawner.setItemMeta(itemMeta);
        BlockStateMeta bsm = (BlockStateMeta) spawner.getItemMeta();
        CreatureSpawner cs = (CreatureSpawner) bsm.getBlockState();
        cs.setSpawnedType(type);
        bsm.setBlockState(cs);
        spawner.setItemMeta(bsm);
        return spawner;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
