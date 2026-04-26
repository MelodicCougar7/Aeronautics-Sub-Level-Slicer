package org.github.melodiccougar7.aeronautics_slicer.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;
import org.github.melodiccougar7.aeronautics_slicer.items.SubLevelSlicerItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Mixin by AzureDoom from HWG, licensed under Creative Commons (thanks a ton for your help, AzureDoom)
// https://github.com/cybercat-mods/HWG/blob/1.21.x/common/src/main/java/mod/azure/hwg/mixins/HeldItemRendererMixin.java

// This file is currently a developer test and should be reformatted and/or optimized before being included in a release.

@Mixin(value = ItemInHandRenderer.class)
public abstract class HeldItemRendererMixin {

    @Mutable
    @Shadow
    @Final
    private final Minecraft minecraft;

    @Shadow
    private float mainHandHeight;

    @Shadow
    private float offHandHeight;

    @Shadow
    private ItemStack mainHandItem;

    @Shadow
    private ItemStack offHandItem;

    protected HeldItemRendererMixin(Minecraft client) {
        this.minecraft = client;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void fguns$cancelAnimation(CallbackInfo ci) {
        var clientPlayerEntity = this.minecraft.player;
        var itemStack = clientPlayerEntity.getMainHandItem();
        var itemStack2 = clientPlayerEntity.getOffhandItem();
        if (
                (this.mainHandItem.getItem() instanceof SubLevelSlicerItem) && ItemStack.isSameItem(
                        mainHandItem,
                        itemStack
                )
        ) {
            this.mainHandHeight = 1;
            this.mainHandItem = itemStack;
        }
        if (this.mainHandItem.getItem() instanceof SubLevelSlicerItem && ItemStack.isSameItem(mainHandItem, itemStack)) {
            this.mainHandHeight = 1;
            this.mainHandItem = itemStack;
        }
        if (
                (this.offHandItem.getItem() instanceof SubLevelSlicerItem) && ItemStack.isSameItem(
                        offHandItem,
                        itemStack2
                )
        ) {
            this.offHandHeight = 1;
            this.offHandItem = itemStack2;
        }
        if (this.offHandItem.getItem() instanceof SubLevelSlicerItem && ItemStack.isSameItem(offHandItem, itemStack2)) {
            this.offHandHeight = 1;
            this.offHandItem = itemStack2;
        }
    }
}
