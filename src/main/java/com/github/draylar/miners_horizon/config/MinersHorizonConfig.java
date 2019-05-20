package com.github.draylar.miners_horizon.config;

import me.sargunvohra.mcmods.autoconfig1.ConfigData;
import me.sargunvohra.mcmods.autoconfig1.annotation.Config;

@Config(name = "minershorizon")
public class MinersHorizonConfig implements ConfigData
{
    public int zone1Start = 100;
    public int zone2Start = 50;
    public int zone3Start = 25;

    public int worldMidHeight = 200;
    public double mountainHeight = 2.4;

    public boolean enableMineshafts = true;
    public double mineshaftRarity = 0.008d;

    public OreConfig[] oreConfigList  = new OreConfig[]
            {
                    new OreConfig(
                            "minecraft:coal_ore",
                            17,
                            20,
                            100,
                            0,
                            255
                    ),
                    new OreConfig(
                            "minecraft:iron_ore",
                            9,
                            20,
                            100,
                            0,
                            255
                    ),
                    new OreConfig(
                            "minecraft:gold_ore",
                            9,
                            2,
                            100,
                            0,
                            140
                    ),
                    new OreConfig(
                            "minecraft:redstone_ore",
                            8,
                            8,
                            100,
                            0,
                            120
                    ),
                    new OreConfig(
                            "minecraft:lapis_ore",
                            8,
                            2,
                            100,
                            0,
                            120
                    ),
                    new OreConfig(
                            "minecraft:diamond_ore",
                            8,
                            1,
                            100,
                            0,
                            120
                    )

            };
}
