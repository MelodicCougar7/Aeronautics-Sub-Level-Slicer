package org.github.melodiccougar7.aeronautics_slicer.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.github.melodiccougar7.aeronautics_slicer.items.SubLevelSlicerItem;

import static org.github.melodiccougar7.aeronautics_slicer.ModClass.MODID;

public class ModRegistry {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);


    public static final DeferredItem<SubLevelSlicerItem> SUBLEVEL_SLICER = ITEMS.registerItem("sublevel_slicer", props -> new SubLevelSlicerItem(props.stacksTo(1).durability(0)));
    //public static final DeferredItem<SubLevelSlicerItem> SUBLEVEL_SLICER = ITEMS.registerItem();
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> AERO_SLICER_TAB = CREATIVE_MODE_TABS.register("aeronautics_slicer", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.aeronautics_slicer")).icon(() -> SUBLEVEL_SLICER.get().getDefaultInstance()).displayItems((parameters, output) -> {
    output.accept(SUBLEVEL_SLICER.get());
    }).build());

}
