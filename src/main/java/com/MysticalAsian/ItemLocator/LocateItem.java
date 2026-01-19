package com.MysticalAsian.ItemLocator;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.matrix.Matrix4d;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.DebugShape;
import com.hypixel.hytale.protocol.Vector3f;
import com.hypixel.hytale.protocol.packets.player.DisplayDebug;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.i18n.I18nModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;

public class LocateItem {
    public static void LocateSearchedItem(
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world,
            @Nonnull String searchedItem
    ){
        String itemName = I18nModule.get()
                .getMessage(playerRef.getLanguage(), Main.ITEMS.get(searchedItem).getTranslationKey());
        int maxRadius = Main.Config.get().getSearchRadius();
        float highlightDuration = Main.Config.get().getHighlightTimeout();
        Vector3f highlightColor = new Vector3f(0.435f, 1.000f, 0.322f);
        var s = playerRef.getReference().getStore();
        Player player = s.getComponent(playerRef.getReference(), Player.getComponentType());
        var worldStore = world.getEntityStore().getStore();
        var playerTransform = worldStore.getComponent(playerRef.getReference(), TransformComponent.getComponentType());
        if (playerTransform == null) return;

        List<List<Vector3d>> containerPositions = ContainerHelper.getContainersWithItem(world, playerTransform.getPosition(), searchedItem, maxRadius);
        if(containerPositions.size() != 0){
            player.sendMessage(Message.raw(
                    String.format("Found %d chests containing %s", containerPositions.size(), itemName)
            ));
        }else{
            player.sendMessage(Message.raw("Found 0 chests containing " + itemName));
        }

        for(List<Vector3d> containers : containerPositions){
            // containers = [block1, block2] if double chest, otherwise [block1]
            for(Vector3d pos : containers){
                player.sendMessage(Message.raw(String.format("chests found at: %d %d %d", (int)pos.x, (int)pos.y, (int)pos.z)));
                Matrix4d matrix = new Matrix4d();
                matrix.identity();
                matrix.translate(pos);
                // slightly bigger so there's no buggy texture overlaps
                matrix.scale(1.05,1.05,1.05);
                matrix.translate(0.475,0.475,0.475);
                DisplayDebug dd = new DisplayDebug(
                        DebugShape.Cube,
                        matrix.asFloatData(),
                        highlightColor,
                        highlightDuration,
                        false,
                        null);
                playerRef.getPacketHandler().write(dd);
            }
        }
    }
}
