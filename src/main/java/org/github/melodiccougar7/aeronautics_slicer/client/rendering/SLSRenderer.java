package org.github.melodiccougar7.aeronautics_slicer.client.rendering;

import com.mojang.logging.LogUtils;
import mod.azure.azurelib.common.render.item.AzItemRenderer;
import mod.azure.azurelib.common.render.item.AzItemRendererConfig;
import net.minecraft.resources.ResourceLocation;
import org.github.melodiccougar7.aeronautics_slicer.client.animation.SLSAnimator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.github.melodiccougar7.aeronautics_slicer.ModClass.MODID;

public class SLSRenderer extends AzItemRenderer {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath(
            MODID,
            "geo/items/sublevel_slicer.geo.json"
    );

    private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath(
            MODID,
            "textures/item/sublevel_slicer.png"
    );

    public SLSRenderer() {
        super(
                AzItemRendererConfig.builder(GEO, TEX)
                        .setAnimatorProvider(SLSAnimator::new)
                        .useNewOffset(true) //per the wiki's instruction, will circle back once the BB plugin is fixed
                        .build()
        );
    }

}
