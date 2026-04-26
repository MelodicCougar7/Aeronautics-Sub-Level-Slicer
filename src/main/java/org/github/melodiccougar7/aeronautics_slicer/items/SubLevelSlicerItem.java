package org.github.melodiccougar7.aeronautics_slicer.items;

import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import mod.azure.azurelib.AzureLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.github.melodiccougar7.aeronautics_slicer.client.animation.SLSDispatcher;
import org.github.melodiccougar7.aeronautics_slicer.config.SlicerWorkConfig;
import org.github.melodiccougar7.aeronautics_slicer.util.SLSDataComponents;
import org.jetbrains.annotations.NotNull;

import javax.print.attribute.standard.Severity;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SubLevelSlicerItem extends Item {

    public final SLSDispatcher dispatcher;

    public SubLevelSlicerItem(Properties properties) {
        super(properties);
        this.dispatcher = new SLSDispatcher();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            if (entity instanceof Player player) {
                BlockPos blockPos = getTargetedBlock(player, serverLevel);
                SingleSubLevelCreator(blockPos, serverLevel);
                animChanger(4, stack, (Player) entity); // might want to remove this
            }
        }
        return stack;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int integer) {
        animChanger(4, stack, (Player) entity, 5);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        super.onUseTick(level, livingEntity, stack, remainingUseDuration);

        if (livingEntity instanceof Player player && !level.isClientSide()) {
            // every half second, relative to actual progress and not the server time
            if ((stack.getOrDefault(SLSDataComponents.PROGRESS, 0f) % 10) == 0) {
                BlockPos blockPos = getTargetedBlock(player, (ServerLevel) level);
                BlockPos targetPos = stack.getOrDefault(SLSDataComponents.TARGET, new BlockPos(0,0,0));
                // if the player begins to grind another block, zero out progress and update target
                if (blockPos != targetPos) {
                    stack.set(SLSDataComponents.PROGRESS.get(), 0f);
                    stack.set(SLSDataComponents.TARGET.get(), blockPos);
                }
            }

            // increase progress by 1 every tick
            stack.set(SLSDataComponents.PROGRESS.get(),stack.getOrDefault(SLSDataComponents.PROGRESS, 0f) + 1);



            // idle animation handler
            float animState = stack.getOrDefault(SLSDataComponents.STATE, 0f);
            if (animState == 1f) {
                animChanger(2, stack, player,5); // necessary to prevent the following line from overriding this the subsequent tick
            } else {
                animChanger(3, stack, player);
            }


        }
    }

    // The placement of this method is important as the other methods should be computed first for the debug log here to work
    @Override
    public void inventoryTick(@NotNull ItemStack stack, Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide()) {
            Player player = (Player) entity;

            // critical to functionality, do not touch
            if (stack.get(AzureLib.AZ_ID.get()) == null) {
                stack.set(AzureLib.AZ_ID.get(), UUID.randomUUID());
            }

            // set animation to idle or reduce cooldown
            float animCooldown = stack.getOrDefault(SLSDataComponents.ANIM_COOLDOWN, 0f);
            if (animCooldown > 0) {
                stack.set(SLSDataComponents.ANIM_COOLDOWN.get(), animCooldown - 1);
            }

            // animChanger detects if the requested state is the same as the current state and returns early if that's the case, spamming is not a concern
            LivingEntity livingEntity = (LivingEntity) entity;
            if (!livingEntity.isUsingItem() && livingEntity instanceof Player player2 && !player2.getCooldowns().isOnCooldown(stack.getItem())) {
                animChanger(1, stack, player2);
            }

            // debug log, comment out before release
            float animState = stack.getOrDefault(SLSDataComponents.STATE, 1f);
            player.displayClientMessage(Component.literal("anim state: " + String.valueOf(animState) + ", cd: " + animCooldown + ", spd: " + String.valueOf(stack.getOrDefault(SLSDataComponents.DISK_SPEED, 0f))), true);
        }

        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }



    public BlockPos getTargetedBlock(Player player, ServerLevel serverLevel) {
        Vec3 eyePos = player.getEyePosition();
        double reach = 5.0; // should be quite close to be able to angle grind, will shrink this to 2.0 if possible
        Vec3 lookVec = player.getLookAngle();
        Vec3 end = eyePos.add(lookVec.scale(reach));
        ClipContext ctx = new ClipContext(eyePos, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);
        BlockHitResult hit = serverLevel.clip(ctx);
        return hit.getBlockPos();
    }

    public void SingleSubLevelCreator(BlockPos blockPos, ServerLevel serverLevel) {
            BoundingBox boundingBox = BoundingBox.fromCorners(blockPos.offset(0, 0, 0), blockPos.offset(0, 0, 0));
            List<BlockPos> blocks = BlockPos.betweenClosedStream(boundingBox).map(BlockPos::immutable).toList();
            BlockPos anchor = (BlockPos)blocks.getFirst();
            BoundingBox3i bounds = new BoundingBox3i(boundingBox);
            bounds.set(bounds.minX - 1, bounds.minY - 1, bounds.minZ - 1, bounds.maxX + 1, bounds.maxY + 1, bounds.maxZ + 1);
            ServerSubLevel subLevel = SubLevelAssemblyHelper.assembleBlocks(serverLevel, anchor, blocks, bounds);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        BlockPos targetPos = stack.getOrDefault(SLSDataComponents.TARGET, new BlockPos(0,0,0));
        BlockState state = entity.level().getBlockState(targetPos);
        Block block = state.getBlock();
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        Map<String, Integer> durationConfigID = SlicerWorkConfig.WORK_BY_ID.get();
        if (durationConfigID.containsKey(id)) {
            return durationConfigID.get(id);
        } else {
            return durationConfigID.get("fallback");
        }
//        Map<String, Integer> durationConfigTAG = SlicerWorkConfig.WORK_BY_TAG.get();
//        if (durationConfigTAG.containsKey(id)) {
//            return durationConfigTAG.get(id);
//        }
    }

    /* This method will return if there is an ongoing cooldown */
    private void animChanger(float state, ItemStack stack, Player player, float cooldown) {
        float animState = stack.getOrDefault(SLSDataComponents.STATE, 1f);

        if (animState == state) {return;}
        float animCooldown = stack.getOrDefault(SLSDataComponents.ANIM_COOLDOWN, 0f);

        if (animCooldown > 0) {return;}
        switch ((int) state) {
            case 1:
                dispatcher.idling(player, stack);
                break;
            case 2:
                dispatcher.windUp(player, stack);
                break;
            case 3:
                dispatcher.active(player, stack);
                break;
            case 4:
                dispatcher.windDown(player, stack);
                break;
            default:
                dispatcher.idling(player, stack);
        }
        stack.set(SLSDataComponents.STATE.get(), state);
        stack.set(SLSDataComponents.ANIM_COOLDOWN.get(), animCooldown + cooldown);
    }

    private void animChanger(int state, ItemStack stack, Player player) {
        animChanger((float) state, stack, player, 0f);
    }
}
