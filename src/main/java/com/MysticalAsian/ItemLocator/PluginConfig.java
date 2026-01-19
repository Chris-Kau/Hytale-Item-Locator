package com.MysticalAsian.ItemLocator;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class PluginConfig {
    private int SearchRadius = 16;
    private float HighlightTimeout = 7f;
    public int getSearchRadius(){return SearchRadius;}
    public float getHighlightTimeout(){return HighlightTimeout;}
    public static final BuilderCodec<PluginConfig> CODEC = BuilderCodec.builder(PluginConfig.class, PluginConfig::new)
            .append(new KeyedCodec<>("SearchRadius", Codec.INTEGER),
                    (c, v, nah) -> c.SearchRadius = v,
                    (c,v) -> c.SearchRadius).add()
            .append(new KeyedCodec<>("HighlightTimeout", Codec.FLOAT),
                    (c, v, nah) -> c.HighlightTimeout = v,
                    (c,v) -> c.HighlightTimeout).add()
            .build();
}
