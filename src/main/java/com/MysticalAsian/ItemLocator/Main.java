package com.MysticalAsian.ItemLocator;

import com.hypixel.hytale.assetstore.event.LoadedAssetsEvent;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;


public class Main extends JavaPlugin {
    public static Map<String, Item> ITEMS = new HashMap<>();
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public static Config<PluginConfig> Config;

    public Main(@Nonnull JavaPluginInit init) {
        super(init);
        Config = this.withConfig("ItemLocator", PluginConfig.CODEC);
    }

    @Override
    protected void setup(){
        super.setup();
        Config.save();
        this.getCommandRegistry().registerCommand(new OpenLocateItemUICommand("locateitem", "opens the ui"));
        this.getEventRegistry().register(LoadedAssetsEvent.class, Item.class, Main::onItemAssetLoad);
    }

    private static void onItemAssetLoad(LoadedAssetsEvent<String, Item, DefaultAssetMap<String, Item>> event){
        ITEMS = event.getAssetMap().getAssetMap();
    }
}
