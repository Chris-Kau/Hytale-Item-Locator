package com.MysticalAsian.ItemLocator;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState;

import java.util.*;

public class ContainerHelper {


    public static List<List<Vector3d>> getContainersWithItem(World world, Vector3d playerPos, String itemId, int radius){
        List<ItemContainerState> containers = getContainersInRadius(world, playerPos, radius);
        // stores [block1, block2] if double chest, otherwise [block1]
        List<List<Vector3d>> validContainerPositions = new ArrayList<>();
        for(ItemContainerState c : containers){
            if(searchContainerForItem(c, itemId)){
                List<Vector3d> chestParts = new ArrayList<>();
                Vector3d originalPos = c.getBlockPosition().toVector3d();
                int x = (int)originalPos.x;
                int y = (int)originalPos.y;
                int z = (int)originalPos.z;
                // double chests
                if(c.getItemContainer().getCapacity() > 18){
                    if(c.getRotationIndex() == 0){
                        chestParts.add(new Vector3d(x-1,y,z));
                    }else if(c.getRotationIndex() == 1){
                        chestParts.add(new Vector3d(x,y,z+1));
                    }else if(c.getRotationIndex() == 2){
                        chestParts.add(new Vector3d(x+1,y,z));
                    }else if(c.getRotationIndex() == 3){
                        chestParts.add(new Vector3d(x,y,z-1));
                    }
                }
                chestParts.add(originalPos);
                validContainerPositions.add(chestParts);
            }
        }
        return validContainerPositions;
    }
    private static List<ItemContainerState> getContainersInRadius(World world, Vector3d playerPos, int radius){
        int playerXPos = (int)playerPos.x;
        int playerYPos = (int)playerPos.y;
        int playerZPos = (int)playerPos.z;
        List<ItemContainerState> containers = new ArrayList<>();
        for(int x = playerXPos - radius; x < playerXPos + radius; x++){
            for(int y = playerYPos - radius; y < playerYPos + radius; y++){
                for(int z = playerZPos - radius; z < playerZPos + radius; z++){
                    WorldChunk chunk = world.getChunk(ChunkUtil.indexChunkFromBlock(x,z));
                    assert chunk != null;
                    BlockState blockState = chunk.getState(x,y,z);
                    if(blockState instanceof ItemContainerState containerState){
                        containers.add(containerState);
                    }
                }
            }
        }
        return containers;
    }

    private static boolean searchContainerForItem(ItemContainerState c, String searchedItemId){
        var container = c.getItemContainer();
        var numSlots = container.getCapacity();
        for(short i = 0; i < numSlots; i++){
            ItemStack itemStack = container.getItemStack(i);
            if(itemStack == null) continue;
            Item item = itemStack.getItem();
            var itemId = item.getId();
            if(itemId.equalsIgnoreCase(searchedItemId)) return true;
        }
        return false;
    }

}
