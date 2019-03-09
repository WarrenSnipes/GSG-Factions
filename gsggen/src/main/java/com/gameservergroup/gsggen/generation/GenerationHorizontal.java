package com.gameservergroup.gsggen.generation;

import com.gameservergroup.gsgcore.enums.Direction;
import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import org.bukkit.Material;

public class GenerationHorizontal extends Generation {

    public GenerationHorizontal(BlockPosition startingBlockPosition, Material material) {
        super(startingBlockPosition, material, Direction.HORIZONTAL);
    }

    @Override
    public boolean generate() {
        return false;
    }
}
