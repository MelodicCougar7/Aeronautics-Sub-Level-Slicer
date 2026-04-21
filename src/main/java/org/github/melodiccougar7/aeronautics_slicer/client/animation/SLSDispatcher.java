package org.github.melodiccougar7.aeronautics_slicer.items.animation;

import mod.azure.azurelib.common.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.common.animation.play_behavior.AzPlayBehaviors;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class SLSDispatcher {
    private static final AzCommand FIRING_COMMAND = AzCommand.create("base_controller", "firing", AzPlayBehaviors.PLAY_ONCE);

    public void firing(Entity entity, ItemStack itemStack) {
        FIRING_COMMAND.sendForItem(entity, itemStack);
    }
}
