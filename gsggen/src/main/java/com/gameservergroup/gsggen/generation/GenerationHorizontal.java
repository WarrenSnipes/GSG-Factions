package com.gameservergroup.gsggen.generation;

import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import com.gameservergroup.gsggen.GSGGen;
import com.gameservergroup.gsggen.objs.Gen;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class GenerationHorizontal extends Generation {

    private BlockFace blockFace;

    public GenerationHorizontal(BlockPosition startingBlockPosition, Gen gen, BlockFace blockFace) {
        super(startingBlockPosition, gen);
        this.blockFace = blockFace;
    }

    @Override
    public boolean generate() {
        final Chunk chunk = getCurrentBlockPosition().getLocation().getChunk();
        if (chunk.isLoaded()) {
            chunk.load();
        }
        final Block relative = getCurrentBlockPosition().getBlock().getRelative(blockFace);
        if (getLength() == 0) {
            return false;
        }
        if (!relative.getChunk().isLoaded()) {
            relative.getChunk().load();
        }
        if (getStartingBlockPosition().getBlock().getType() != getMaterial()) {
            return false;
        }
        if (relative.getType() != Material.AIR && !getGen().isPatch()) {
            return false;
        }

        if (getGen().isPatch() && (relative.getType() != Material.WATER ||
                relative.getType() != Material.STATIONARY_WATER ||
                relative.getType() != Material.LAVA ||
                relative.getType() != Material.COBBLESTONE ||
                relative.getType() != Material.OBSIDIAN ||
                relative.getType() != Material.AIR)) {
            return false;
        }

        setLength(getLength() - 1);
        GSGGen.getInstance().getServer().getScheduler().runTask(GSGGen.getInstance(), () -> relative.setTypeIdAndData(getGen().getMaterial().getId(), (byte) 0, false));
        setCurrentBlockPosition(BlockPosition.of(relative));
        return true;
    }
}
