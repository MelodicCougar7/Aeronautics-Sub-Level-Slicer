package org.github.melodiccougar7.aeronautics_slicer.client.animation;

import mod.azure.azurelib.common.animation.AzAnimatorConfig;
import mod.azure.azurelib.common.animation.cache.AzBoneCache;
import mod.azure.azurelib.common.animation.controller.AzAnimationController;
import mod.azure.azurelib.common.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.common.animation.controller.keyframe.AzKeyframeCallbacks;
import mod.azure.azurelib.common.animation.impl.AzItemAnimator;
import mod.azure.azurelib.common.model.AzBone;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.github.melodiccougar7.aeronautics_slicer.ModClass;
import org.github.melodiccougar7.aeronautics_slicer.util.SLSData;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SLSAnimator extends AzItemAnimator {

    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(
            ModClass.MODID,
            "animations/item/sublevel_slicer.animation.json"
    );

    public SLSAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<ItemStack> animationControllerContainer) {
        animationControllerContainer.add(
                AzAnimationController.builder(this, "base_controller")
                        .setTransitionLength(10) //give this a shot when everything finally works...
                        .setKeyframeCallbacks(
                                AzKeyframeCallbacks.<ItemStack>builder()
                                        .setCustomInstructionKeyframeHandler(
                                                event -> {
                                                    if (event.getKeyframeData().getInstructions().equals("sparks")) {
                                                        // see what we can do with Photon
                                                    }
                                                }
                                        )
                                        .setSoundKeyframeHandler(
                                                event -> {
                                                    if (event.getKeyframeData().getSound().equals("grind")) {
                                                        event.getAnimatable()
                                                                .getEntityRepresentation().level()
                                                                .playSound(
                                                                        (Player) event.getAnimatable().getEntityRepresentation(),
                                                                        event.getAnimatable().getEntityRepresentation().getX(),
                                                                        event.getAnimatable().getEntityRepresentation().getY(),
                                                                        event.getAnimatable().getEntityRepresentation().getZ(),
                                                                        SoundEvents.METAL_HIT,
                                                                        SoundSource.PLAYERS,
                                                                        1.0F, // volume
                                                                        1.0F // pitch
                                                                        //true // Should have distance delay
                                                                );
                                                    }
                                                    if (event.getKeyframeData().getSound().equals("wind_up")) {
                                                        event.getAnimatable()
                                                                .getEntityRepresentation().level()
                                                                .playSound(
                                                                        (Player) event.getAnimatable().getEntityRepresentation(),
                                                                        event.getAnimatable().getEntityRepresentation().getX(),
                                                                        event.getAnimatable().getEntityRepresentation().getY(),
                                                                        event.getAnimatable().getEntityRepresentation().getZ(),
                                                                        SoundEvents.BEE_LOOP_AGGRESSIVE,
                                                                        SoundSource.PLAYERS,
                                                                        1.0F, // volume
                                                                        1.0F // pitch
                                                                        //true // Should have distance delay
                                                                );
                                                    }
                                                    if (event.getKeyframeData().getSound().equals("wind_down")) {
                                                        event.getAnimatable()
                                                                .getEntityRepresentation().level()
                                                                .playSound(
                                                                        (Player) event.getAnimatable().getEntityRepresentation(),
                                                                        event.getAnimatable().getEntityRepresentation().getX(),
                                                                        event.getAnimatable().getEntityRepresentation().getY(),
                                                                        event.getAnimatable().getEntityRepresentation().getZ(),
                                                                        SoundEvents.BEE_LOOP,
                                                                        SoundSource.PLAYERS,
                                                                        1.0F, // volume
                                                                        1.0F // pitch
                                                                        //true // Should have distance delay
                                                                );
                                                    }
                                                }
                                        )
                                        .build()
                        )
                        .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(ItemStack animatable) {
        return ANIMATIONS;
    }

    @Override
    public void setCustomAnimations(ItemStack animatable, float partialTicks) {
        super.setCustomAnimations(animatable, partialTicks);
        AzBoneCache boneCache = this.context().boneCache();
        Optional<AzBone> disk = boneCache.getBakedModel().getBone("disk");

        float speed = animatable.getOrDefault(SLSData.DISK_SPEED, 0f);
        disk.get().setRotY(Mth.cos(disk.get().getRotY() + speed));

    }

}
