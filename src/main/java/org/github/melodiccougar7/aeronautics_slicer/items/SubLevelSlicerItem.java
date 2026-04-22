package org.github.melodiccougar7.aeronautics_slicer.items;

import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import mod.azure.azurelib.AzureLib;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
    private int animState = 1;
    private int animCooldown = 0;

    public final SLSDispatcher dispatcher;

    public SubLevelSlicerItem(Properties properties) {
        super(properties);
        this.dispatcher = new SLSDispatcher();
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        if (stack.get(AzureLib.AZ_ID.get()) == null) {
            stack.set(AzureLib.AZ_ID.get(), UUID.randomUUID());
        }

        if (!level.isClientSide() && stack.is(this) && entity instanceof LivingEntity livingEntity &&
                        !livingEntity.isUsingItem() && livingEntity instanceof Player player &&
                        !player.getCooldowns().isOnCooldown(stack.getItem())
        ) {
            animChanger(1, stack, player, 0);
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
        return stack;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int integer) {
        animChanger(4, stack, (Player) entity, 10);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
        if (livingEntity instanceof Player player && !level.isClientSide()) {
//            float diskSpeed = stack.getOrDefault(SLSData.DISK_SPEED, 0f);
//            if (diskSpeed < speedCap) {
//                stack.set(SLSData.DISK_SPEED.get(), ((diskSpeed + (acceleration / 20))));
//            }
            animChanger(3, stack, player, 0);
        }

        if (livingEntity instanceof Player player && level.isClientSide()) {
            player.displayClientMessage(Component.literal("grinding"), true);
        }

        if (stack.get(AzureLib.AZ_ID.get()) == null) {
            stack.set(AzureLib.AZ_ID.get(), UUID.randomUUID());
        }

        if (livingEntity instanceof Player player && !level.isClientSide()) {
            float diskSpeed = stack.getOrDefault(SLSData.DISK_SPEED, 0f);
            if (diskSpeed < speedCap) {
                stack.set(SLSData.DISK_SPEED.get(), ((diskSpeed + (acceleration / 20))));
            }
            animChanger(3, stack, player,0);
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
        return 50; // tweak to depend on whatever block is being looked at
    }

    private void animChanger(int state, ItemStack stack, Player player, int cooldown) {
        if (animState == state) {return;}
        if (animCooldown > 0) {return;}
        switch (state) {
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
        animState = state;
        animCooldown = cooldown;
    }
}
