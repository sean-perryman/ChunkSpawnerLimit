package com.phanaticmc.chunkspawnerlimit.commands;

import static com.phanaticmc.chunkspawnerlimit.ChunkSpawnerLimit.limit;
import com.phanaticmc.chunkspawnerlimit.utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import static org.bukkit.Material.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DeleteCommand implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("spawnerdelete")) {
            Bukkit.getWorlds().stream().forEach((w) -> {
                for (Chunk c : w.getLoadedChunks()) {
                    int spawnercount = 0;
                    for (BlockState block : c.getTileEntities()) {
                        if(block instanceof CreatureSpawner){
                            spawnercount++;
                            if(spawnercount > limit){
                                sender.sendMessage("Deleting Spawner: " + block.getLocation().getBlockX() + " " + block.getLocation().getBlockY() + " " + block.getLocation().getBlockZ() + " " + block.getLocation().getWorld().getName());
                                ItemStack drop = new ItemStack(MOB_SPAWNER);
                                CreatureSpawner existing = (CreatureSpawner) block;
                                utils.setSpawnerMob(drop, existing.getSpawnedType());
                                ItemMeta itemMeta = drop.getItemMeta();
                                itemMeta.setDisplayName(ChatColor.RESET + existing.getSpawnedType().name());
                                drop.setItemMeta(itemMeta);
                                w.dropItem(block.getLocation().add(0.5, 0.5, 0.5), drop);
                                block.setType(AIR);
                                block.update(true);
                            }
                        }
                    }
                }   });
            return true;
        }
        return false;
    }
}
