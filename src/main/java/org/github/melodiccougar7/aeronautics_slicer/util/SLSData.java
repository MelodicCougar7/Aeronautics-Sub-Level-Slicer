package org.github.melodiccougar7.aeronautics_slicer.util;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.github.melodiccougar7.aeronautics_slicer.ModClass;

import java.util.function.Supplier;

public class SLSData {

    public static final DeferredRegister<DataComponentType<?>> SLSDATA =
            DeferredRegister.createDataComponents(ModClass.MODID);

    public static final Supplier<DataComponentType<Float>> DISK_SPEED =
            SLSDATA.register("disk_rot", () ->
                    DataComponentType.<Float>builder()
                            .persistent(Codec.FLOAT)
                            .networkSynchronized(ByteBufCodecs.FLOAT)
                            .build()
            );
    public static final Supplier<DataComponentType<Float>> PROGRESS = // should be an int but was uncooperative
            SLSDATA.register("progress", () ->
                    DataComponentType.<Float>builder()
                            .persistent(Codec.FLOAT)
                            .networkSynchronized(ByteBufCodecs.FLOAT)
                            .build()
            );
}
