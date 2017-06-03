package com.phanaticmc.chunkspawnerlimit.listeners;
import static com.phanaticmc.chunkspawnerlimit.ChunkSpawnerLimit.cleanOnChunkLoad;
import static com.phanaticmc.chunkspawnerlimit.ChunkSpawnerLimit.instance;
import static com.phanaticmc.chunkspawnerlimit.ChunkSpawnerLimit.limit;
import com.phanaticmc.chunkspawnerlimit.utils;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.Chunk;
import org.bukkit.Location;
import static org.bukkit.Material.AIR;
import static org.bukkit.Material.MOB_SPAWNER;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;

public class ChunkLoad implements Listener {
    
    @EventHandler(ignoreCancelled = true)
    public void onChunkLoad(ChunkLoadEvent e){
        Chunk c = e.getChunk();
        int spawnercount = 0;
        for (BlockState block : c.getTileEntities()) {
            if(block instanceof CreatureSpawner){
                spawnercount++;
                if(spawnercount > limit){
                    Location cloc = new Location(c.getWorld(),c.getX() * 16,64,c.getZ() * 16);
                    if(cleanOnChunkLoad){
                        ItemStack drop = new ItemStack(MOB_SPAWNER);
                        CreatureSpawner existing = (CreatureSpawner) block;
                        utils.setSpawnerMob(drop, existing.getSpawnedType());
                        cloc.getWorld().dropItem(block.getLocation().add(0.5, 0.5, 0.5), drop);
                        block.setType(AIR);
                        block.update(true);
                    }
                    getServer().getScheduler().scheduleSyncDelayedTask(instance, () -> {
                        cloc.getWorld().getNearbyEntities(cloc, 100,100,100).stream().filter((ent) -> (ent instanceof Player)).map((ent) -> (Player)ent).forEach((player) -> {
                            player.sendMessage("Too many Spawners in this chunk, " + limit + " is the max! x:" + block.getLocation().getBlockX() + " y:" + block.getLocation().getBlockY() + " z:" + block.getLocation().getBlockZ());
                        });
                    }, 20L);
                }
            }
        }
    }
}
