package com.cyprias.chunkspawnerlimiter.listeners;

import com.cyprias.chunkspawnerlimiter.ChatUtil;
import com.cyprias.chunkspawnerlimiter.Config;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;

/**
 * Spawn Reasons at https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/entity/CreatureSpawnEvent.SpawnReason.html
 */
public class EntityListener implements Listener {
    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent e) {
        if (e.isCancelled() || !Config.Properties.WATCH_CREATURE_SPAWNS)
            return;

        final String reason = e.getSpawnReason().toString();

        if (!Config.isSpawnReason(reason)) {
            ChatUtil.debug("Ignoring " + e.getEntity().getType().toString() + " due to spawn-reason: " + reason);
            return;
        }

        Chunk chunk = e.getLocation().getChunk();
        WorldListener.checkChunk(chunk);
        checkSurroundings(chunk);
    }

    @EventHandler
    public void onVehicleCreateEvent(VehicleCreateEvent e) {
        if (e.isCancelled() || !Config.Properties.WATCH_VEHICLE_CREATE_EVENT) return;

        Chunk chunk = e.getVehicle().getLocation().getChunk();
        WorldListener.checkChunk(chunk);
        checkSurroundings(chunk);
    }

    private void checkSurroundings(Chunk chunk) {
        int surrounding = Config.Properties.CHECK_SURROUNDING_CHUNKS;
        if (surrounding > 0) {
            for (int x = chunk.getX() + surrounding; x >= (chunk.getX() - surrounding); x--) {
                for (int z = chunk.getZ() + surrounding; z >= (chunk.getZ() - surrounding); z--) {
                    WorldListener.checkChunk(chunk.getWorld().getChunkAt(x, z));
                }
            }
        }
    }
}
