package org.github.melodiccougar7.aeronautics_slicer.client.sounds;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.github.melodiccougar7.aeronautics_slicer.ModClass;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, ModClass.MODID);

    public static final Holder<SoundEvent> GRINDER_METAL = SOUND_EVENTS.register(
            "grinder_metal",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ModClass.MODID, "grinder_metal"))
    );

    public static final Holder<SoundEvent> GRINDER_STONE = SOUND_EVENTS.register(
            "grinder_stone",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ModClass.MODID, "grinder_stone"))
    );

    public static final Holder<SoundEvent> GRINDER_DIRT = SOUND_EVENTS.register(
            "grinder_dirt",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ModClass.MODID, "grinder_dirt"))
    );

    public static final Holder<SoundEvent> GRINDER_LEAVES = SOUND_EVENTS.register(
            "grinder_leaves",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ModClass.MODID, "grinder_leaves"))
    );

    public static final Holder<SoundEvent> GRINDER_SPIN = SOUND_EVENTS.register(
            "grinder_spin",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ModClass.MODID, "grinder_spin"))
    );

    public static final Holder<SoundEvent> GRINDER_SPIN_DOWN = SOUND_EVENTS.register(
            "grinder_spin_down",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ModClass.MODID, "grinder_spin_down"))
    );

    public static final Holder<SoundEvent> GRINDER_SPIN_UP = SOUND_EVENTS.register(
            "grinder_spin_up",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ModClass.MODID, "grinder_spin_up"))
    );

}
