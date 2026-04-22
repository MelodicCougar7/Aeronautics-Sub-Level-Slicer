package org.github.melodiccougar7.aeronautics_slicer.client.animation;

import mod.azure.azurelib.common.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.common.animation.play_behavior.AzPlayBehaviors;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class SLSDispatcher {
    private static final AzCommand IDLE_COMMAND = AzCommand.create("base_controller", "idle", AzPlayBehaviors.LOOP);
    private static final AzCommand ACTIVE_COMMAND = AzCommand.create("base_controller", "active", AzPlayBehaviors.LOOP);
    private static final AzCommand WIND_UP_COMMAND = AzCommand.create("base_controller", "windUpDown", AzPlayBehaviors.HOLD_ON_LAST_FRAME);
    private static final AzCommand WIND_DOWN_COMMAND = AzCommand.create("base_controller", "windUpDown", AzPlayBehaviors.HOLD_ON_LAST_FRAME, 0.0f, 1.0f, 0.0f,0.0f,0.0f, true);
    //private static final AzCommand idk = AzCommand.create();


    public void idling(Entity entity, ItemStack itemStack) {
        IDLE_COMMAND.sendForItem(entity, itemStack);
    }

    public void active(Entity entity, ItemStack itemStack) {
        ACTIVE_COMMAND.sendForItem(entity, itemStack);
    }

    public void windUp(Entity entity, ItemStack itemStack) {
        WIND_UP_COMMAND.sendForItem(entity, itemStack);
    }

    public void windDown(Entity entity, ItemStack itemStack) {
        WIND_DOWN_COMMAND.sendForItem(entity, itemStack);
    }

}
