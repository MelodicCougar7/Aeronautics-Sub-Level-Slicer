package org.github.melodiccougar7.aeronautics_slicer.items;

import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import mod.azure.azurelib.AzureLib;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.github.melodiccougar7.aeronautics_slicer.client.animation.SLSDispatcher;
import org.github.melodiccougar7.aeronautics_slicer.util.SLSData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class SubLevelSlicerItem extends Item {

    private final int acceleration = 5;
    private final float speedCap = 100;

    public final SLSDispatcher dispatcher;

    public SubLevelSlicerItem(Properties properties) {
        super(properties);
        this.dispatcher = new SLSDispatcher();
    }


    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {

        if (stack.get(AzureLib.AZ_ID.get()) == null) {
            stack.set(AzureLib.AZ_ID.get(), UUID.randomUUID());
        }

        if (entity instanceof Player player && !level.isClientSide()) {
            float diskSpeed = stack.getOrDefault(SLSData.DISK_SPEED, 0f);
            if (diskSpeed < speedCap) {
                stack.set(SLSData.DISK_SPEED.get(), ((diskSpeed + (acceleration / 20))));
            }

            float progress = stack.getOrDefault(SLSData.PROGRESS, 0f);

            if (progress >= 25) { // go back and make the time it takes configurable, and block category based
                SingleSubLevelCreator(player);
                stack.set(SLSData.PROGRESS.get(), 0f);
            } else {
                stack.set(SLSData.PROGRESS.get(), stack.getOrDefault(SLSData.PROGRESS, 0f) + 1f);
            }
        }

        if (entity instanceof Player player && level.isClientSide()) {
            dispatcher.active(player, stack);

        }
    }

    @Override
    public void inventoryTick(
            @NotNull ItemStack stack,
            Level level,
            @NotNull Entity entity,
            int slotId,
            boolean isSelected
    ) {
        if (stack.get(AzureLib.AZ_ID.get()) == null) {
            stack.set(AzureLib.AZ_ID.get(), UUID.randomUUID());
        }


        if (
                !level.isClientSide() && stack.is(this) && entity instanceof LivingEntity livingEntity &&
                        !livingEntity.isUsingItem() && livingEntity instanceof Player player &&
                        !player.getCooldowns().isOnCooldown(stack.getItem())
        ) {
            dispatcher.idling(entity, stack);
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }


    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
        if (livingEntity instanceof Player player && level.isClientSide()) {
//            float diskSpeed = stack.getOrDefault(SLSData.DISK_SPEED, 0f);
//            if (diskSpeed < speedCap) {
//                stack.set(SLSData.DISK_SPEED.get(), ((diskSpeed + (acceleration / 20))));
//            }
            dispatcher.active(player, stack);
        }
    }

    public void SingleSubLevelCreator(Player player) {
        if (player.level() instanceof ServerLevel serverLevel) {
            Vec3 eyePos = player.getEyePosition();
            double reach = 5.0; // should be quite close to be able to angle grind, will shrink this to 2.0 if possible
            Vec3 lookVec = player.getLookAngle();
            Vec3 end = eyePos.add(lookVec.scale(reach));
            ClipContext ctx = new ClipContext(eyePos, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);

            BlockHitResult hit = serverLevel.clip(ctx);
            BlockPos blockPos = hit.getBlockPos();

            BoundingBox boundingBox = BoundingBox.fromCorners(blockPos.offset(0, 0, 0), blockPos.offset(0, 0, 0));
            List<BlockPos> blocks = BlockPos.betweenClosedStream(boundingBox).map(BlockPos::immutable).toList();
            BlockPos anchor = (BlockPos)blocks.getFirst();
            BoundingBox3i bounds = new BoundingBox3i(boundingBox);
            bounds.set(bounds.minX - 1, bounds.minY - 1, bounds.minZ - 1, bounds.maxX + 1, bounds.maxY + 1, bounds.maxZ + 1);
            ServerSubLevel subLevel = SubLevelAssemblyHelper.assembleBlocks(serverLevel, anchor, blocks, bounds);
        }
    }
}
