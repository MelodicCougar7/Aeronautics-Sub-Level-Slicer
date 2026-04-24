package org.github.melodiccougar7.aeronautics_slicer.items;

import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import mod.azure.azurelib.AzureLib;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.github.melodiccougar7.aeronautics_slicer.client.animation.SLSDispatcher;
import org.github.melodiccougar7.aeronautics_slicer.util.SLSDataComponents;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class SubLevelSlicerItem extends Item {

    private final int acceleration = 5;
    private final float speedCap = 100;
    private boolean animActiveSent = false;
    //private int animState;
    //private int animCooldown;

    public SubLevelSlicerItem(Properties properties) {
        super(properties);
        //this.animState = 1;
        //this.animCooldown = 0;
    }

    @Override public @NotNull UseAnim getUseAnimation(@NotNull ItemStack s) { return UseAnim.NONE; }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide()) {
            Player player = (Player) entity;
            // fetch cooldown stat from item itself
            // set animation to idle or reduce cooldown
            float animCooldown = stack.getOrDefault(SLSDataComponents.ANIM_COOLDOWN, 0f);
            if (animCooldown > 0) {
                stack.set(SLSDataComponents.ANIM_COOLDOWN.get(), animCooldown - 1);

                // skip this check if the cooldown would prevent it
                LivingEntity livingEntity = (LivingEntity) entity;
                if (!livingEntity.isUsingItem() && livingEntity instanceof Player player2 && !player2.getCooldowns().isOnCooldown(stack.getItem())) {
                    animChanger(1, stack, player2, 0);
                }
            }

            float animState = stack.getOrDefault(SLSDataComponents.STATE, 1f);

            player.displayClientMessage(Component.literal("anim state: " + String.valueOf(animState) + ", cd: " + animCooldown + ", spd: " + String.valueOf(stack.getOrDefault(SLSDataComponents.DISK_SPEED, 0f))), true);



        }

        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        SingleSubLevelCreator((Player) entity);
        animActiveSent = false;
        float animCooldown = stack.getOrDefault(SLSDataComponents.ANIM_COOLDOWN, 0f);
        stack.set(SLSDataComponents.ANIM_COOLDOWN.get(), animCooldown + 10);
        animChanger(4, stack, (Player) entity);
        return stack;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int integer) {
        animActiveSent = false;
        animChanger(4, stack, (Player) entity, 10);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        super.onUseTick(level, livingEntity, stack, remainingUseDuration);

        if (livingEntity instanceof Player player && !level.isClientSide()) {
            float diskSpeed = stack.getOrDefault(SLSDataComponents.DISK_SPEED, 0f);
            if (diskSpeed < speedCap) {
                stack.set(SLSDataComponents.DISK_SPEED.get(), ((diskSpeed + (acceleration / 20))));
            }

            if (!animActiveSent) {
                float animState = stack.getOrDefault(SLSDataComponents.STATE, 1f);
                if (animState == 1f) {
                    animChanger(2, stack, player, 10);
                } else {
                    animChanger(3, stack, player);
                }
                animActiveSent = true;
            }
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

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }


    private void animChanger(float state, ItemStack stack, Player player, float cooldown) {
        float animState = stack.getOrDefault(SLSDataComponents.STATE, 1f);

        if (animState == state) {return;}
        switch ((int) state) {
            case 1:
                SLSDispatcher.idling(player, stack);
                break;
            case 2:
                SLSDispatcher.windUp(player, stack);
                break;
            case 3:
                SLSDispatcher.active(player, stack);
                break;
            case 4:
                SLSDispatcher.windDown(player, stack);
                break;
            default:
                SLSDispatcher.idling(player, stack);
        }
        stack.set(SLSDataComponents.STATE.get(), state);
    }

    private void animChanger(int state, ItemStack stack, Player player) {
        animChanger((float) state, stack, player, 0f);
    }
}