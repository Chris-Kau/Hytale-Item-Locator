package com.MysticalAsian.ItemLocator;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.modules.i18n.I18nModule;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.*;

public class LocateItemUIPage extends InteractiveCustomUIPage<LocateItemUIPage.SearchQueryData> {
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private String searchQuery = "";
    private List<Map.Entry<String,Item>> items = new ArrayList<>();
    public LocateItemUIPage(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, SearchQueryData.CODEC);
    }

    @Override
    public void build(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull UICommandBuilder cmd,
            @Nonnull UIEventBuilder evt,
            @Nonnull Store<EntityStore> store
    ) {
        cmd.append("Pages/ItemLocatorPage.ui");
        cmd.set("#SearchInput.Value", this.searchQuery);

        evt.addEventBinding(
                CustomUIEventBindingType.ValueChanged,
                "#SearchInput",
                new EventData().append("@SearchQuery", "#SearchInput.Value"),
                false
        );
    }

    @Override
    public void handleDataEvent(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            SearchQueryData data
    ){
        String searchQuery = data.searchQuery != null ? data.searchQuery : "";
        String item = data.item != null ? data.item : "";
        this.searchQuery = searchQuery;
        if(!item.isEmpty()){
            Player player = playerRef.getReference().getStore().getComponent(playerRef.getReference(), Player.getComponentType());
            LocateItem.LocateSearchedItem(store, ref, playerRef, player.getWorld(), item);
            item = "";
            this.close();
        }
        UICommandBuilder cmd = new UICommandBuilder();
        UIEventBuilder evt = new UIEventBuilder();
        this.buildList(ref, cmd, evt, store);
        this.sendUpdate(cmd ,evt, false);
    }

    private void buildList(
            Ref<EntityStore> ref,
            UICommandBuilder cmd,
            UIEventBuilder evt,
            ComponentAccessor<EntityStore> ca
    ){
        HashMap<String, Item> itemList = new HashMap<>(Main.ITEMS);
        Player playerComponent = ca.getComponent(ref, Player.getComponentType());
        assert playerComponent != null;
        if(this.searchQuery.isEmpty()){
            this.items.clear();
            this.items.addAll(itemList.entrySet());
        }else{
            List<Map.Entry<String, Item>> startsWithMatches = new ArrayList<>();
            List<Map.Entry<String, Item>> containMatches = new ArrayList<>();
            for(Map.Entry<String, Item> entry : itemList.entrySet()){
                if(entry.getValue() != null){
                    var itemName = I18nModule.get()
                            .getMessage(this.playerRef.getLanguage(), entry.getValue().getTranslationKey());
                    if(itemName == null){
                        continue;
                    }
                    itemName = itemName.toLowerCase(Locale.ENGLISH);
                    String[] search_terms = this.searchQuery.toLowerCase(Locale.ENGLISH).split(" ");
                    if(itemName.startsWith(this.searchQuery.toLowerCase(Locale.ENGLISH))){
                        startsWithMatches.add(entry);
                    } else{
                        for(String term : search_terms){
                            if(itemName.contains(term)){
                                containMatches.add(entry);
                                break;
                            }
                        }
                    }
                }
            }
            Comparator<Map.Entry<String, Item>> byLength = Comparator.comparingInt(e -> {
                String name = I18nModule.get().getMessage(this.playerRef.getLanguage(), e.getValue().getTranslationKey());
                return name == null ? Integer.MAX_VALUE : name.length();
            });
            startsWithMatches.sort(byLength);
            LOGGER.atInfo().log(startsWithMatches.toString());
            containMatches.sort(byLength);
            List<Map.Entry<String,Item>> matchedResults = new ArrayList<>();
            matchedResults.addAll(startsWithMatches);
            matchedResults.addAll(containMatches);
            this.items.clear();
            this.items = matchedResults;
            this.items.addAll(matchedResults);
        }
        this.buildButtons(this.items, cmd, evt);
    }
    private void buildButtons(
            List<Map.Entry<String,Item>> items,
            UICommandBuilder cmd,
            UIEventBuilder evt
    ){
        cmd.clear("#ItemIconTable");
        int rowIdx = 0;
        int numIconsInRow = 0;

        for(var entry : items){
            Item item = entry.getValue();
            if(numIconsInRow == 0){
                cmd.appendInline("#ItemIconTable", "Group {LayoutMode: Left; Anchor: (Bottom: 0);}");
            }
            cmd.append("#ItemIconTable[" + rowIdx + "]", "Pages/SearchedItemIcon.ui");
            cmd.set("#ItemIconTable[" + rowIdx + "][" + numIconsInRow + "].TooltipTextSpans", Message.translation(item.getTranslationKey()));
            cmd.set("#ItemIconTable[" + rowIdx + "][" + numIconsInRow + "] #ItemIcon.ItemId", entry.getKey());
            cmd.set("#ItemIconTable[" + rowIdx + "][" + numIconsInRow + "] #ItemName.TextSpans", Message.translation(item.getTranslationKey()));
            evt.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    "#ItemIconTable[" + rowIdx + "][" + numIconsInRow + "]",
                    new EventData().append("ITEM", entry.getKey()));
            ++numIconsInRow;
            if(numIconsInRow >= 8){
                numIconsInRow = 0;
                ++rowIdx;
            }
        }
    }
    public static class SearchQueryData {
        public String searchQuery;
        public String item;
        public static final BuilderCodec<SearchQueryData> CODEC =
                BuilderCodec.builder(SearchQueryData.class, SearchQueryData::new)
                        .append(
                                new KeyedCodec<>("@SearchQuery", Codec.STRING),
                                (obj, val) -> obj.searchQuery = val,
                                obj -> obj.searchQuery
                        )
                        .add()
                        .append(
                                new KeyedCodec<>("ITEM", Codec.STRING),
                                (obj, val) -> obj.item = val,
                                obj -> obj.item
                        )
                        .add()
                        .build();
    }
}
