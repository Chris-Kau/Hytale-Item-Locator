package com.MysticalAsian.ItemLocator;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class OpenLocateItemUICommand extends AbstractPlayerCommand {
    public OpenLocateItemUICommand(@Nonnull String name, @Nonnull String description) {
        super(name, description, false);
        this.setPermissionGroup(GameMode.Adventure);
    }

    @Override
    protected void execute(
            @Nonnull CommandContext commandContext,
            @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef, @Nonnull World world) {
        Player player = store.getComponent(ref, Player.getComponentType());
        LocateItemUIPage page = new LocateItemUIPage(playerRef);
        player.getPageManager().openCustomPage(ref, store, page);

    }
}
