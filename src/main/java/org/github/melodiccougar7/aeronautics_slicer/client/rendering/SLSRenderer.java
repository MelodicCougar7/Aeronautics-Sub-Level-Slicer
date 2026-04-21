package org.github.melodiccougar7.aeronautics_slicer.client.rendering;

import mod.azure.azurelib.common.render.item.AzItemRenderer;
import mod.azure.azurelib.common.render.item.AzItemRendererConfig;
import net.minecraft.resources.ResourceLocation;
import org.github.melodiccougar7.aeronautics_slicer.client.animation.SLSAnimator;

import static org.github.melodiccougar7.aeronautics_slicer.ModClass.MODID;

public class SLSRenderer extends AzItemRenderer {

    private static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath(
            MODID,
            "geo/items/sublevelslicer.geo.json"
    );

    private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath(
            MODID,
            "textures/item/sublevelslicer.png"
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
