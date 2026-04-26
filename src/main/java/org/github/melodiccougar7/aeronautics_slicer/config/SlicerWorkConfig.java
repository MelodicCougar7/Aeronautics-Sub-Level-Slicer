package org.github.melodiccougar7.aeronautics_slicer.config;

import com.corrinedev.jsconf.api.Config;
import com.corrinedev.jsconf.api.ConfigValue;
import com.google.common.reflect.TypeToken;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.github.melodiccougar7.aeronautics_slicer.ModClass.MODID;

public class SlicerWorkConfig {
    public static final Config WORK_CONFIG = new Config(MODID + "-slicer-work-duration");

    public static final ConfigValue<Map<String, Integer>> WORK_BY_ID
            = new ConfigValue<>(new LinkedHashMap<>(Map.of(
            "fallback", 50,
            "minecraft:iron_bars", 50,
            "minecraft:iron_block", 100,
            "create:industrial_iron_block", 75
    )),
            "Slicer Work Duration in ticks by block ID",
            WORK_CONFIG,
            new TypeToken<Map<String, Integer>>(){}.getType()
    );

//    public static final ConfigValue<Map<String, Integer>> WORK_BY_TAG
//            = new ConfigValue<>(new LinkedHashMap<>(Map.of(
//            "#forge:storage_blocks/gold", 50
//    )),
//            "Slicer Work Duration in ticks by tag",
//            WORK_CONFIG,
//            new TypeToken<Map<String, Integer>>(){}.getType()
//    );


    public static void init() {
        WORK_CONFIG.register();
    }
}