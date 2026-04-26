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
import org.github.melodiccougar7.aeronautics_slicer.util.SLSDataComponents;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.logging.Logger;

public class SLSAnimator extends AzItemAnimator {

    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(
            ModClass.MODID,
            "animations/item/sublevel_slicer.animation.json"
    );

    public SLSAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    private static final Logger LOGGER = Logger.getLogger("aeronautics_slicer");

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

        float speed = animatable.getOrDefault(SLSDataComponents.DISK_SPEED, 0f);
        int animState = (int) (float) animatable.getOrDefault(SLSDataComponents.STATE, 0f);

        if (animState == 1 || animState == 4) {
            if (speed >= 0.05f) {
                animatable.set(SLSDataComponents.DISK_SPEED.get(), (speed - 0.05f));
            }
        } else if (animState == 2 || animState == 3) {
            if (speed < 2f) {
                animatable.set(SLSDataComponents.DISK_SPEED.get(), (speed + 0.3f));
            }
        }

        if (disk.isPresent()) {
            //disk.get().setRotY(Mth.cos(disk.get().getRotY() + speed));
            disk.get().setRotY(disk.get().getRotY() + animatable.getOrDefault(SLSDataComponents.DISK_SPEED, 0f));
        }
        //LOGGER.info("speed: " + speed + " disk RotY: " + disk.get().getRotY());
    }

}
