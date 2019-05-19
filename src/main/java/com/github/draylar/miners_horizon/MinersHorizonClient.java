package com.github.draylar.miners_horizon;

import com.github.draylar.miners_horizon.config.MinersHorizonConfig;
import me.sargunvohra.mcmods.autoconfig1.AutoConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.impl.client.render.ColorProviderRegistryImpl;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.awt.*;

public class MinersHorizonClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        for(Block newBlock : Registry.BLOCK)
        {
            if(newBlock.getTranslationKey().contains("ore") || newBlock.getTranslationKey().contains("stone"))
            {
                // TODO: MOVE TO CLIENT
                ColorProviderRegistryImpl.BLOCK.register((block, view, pos, layer) ->
                        {
                            MinersHorizonConfig config = AutoConfig.getConfigHolder(MinersHorizonConfig.class).getConfig();

                            World world = MinecraftClient.getInstance().world;
                            if (world != null)
                            {
                                if (world.getDimension().getType() == MinersHorizon.FABRIC_WORLD)
                                {
                                    int y = pos.getY();

                                    Color returnColor = Color.getHSBColor(0, 0, 100 / 100f);


                                    // bottom zone
                                    if (y < config.zone3Start)
                                    {
                                        returnColor = Color.getHSBColor(0, 0, 40 / 100f);
                                    }

                                    // bottom zone smoothing
                                    else if (y < config.zone3Start + 5)
                                    {
                                        int difference = (config.zone3Start + 5) - y;
                                        returnColor = Color.getHSBColor(0, 0, (40 + (20f / difference)) / 100f);
                                    }

                                    // middle zone
                                    else if (y < config.zone2Start)
                                    {
                                        returnColor = Color.getHSBColor(0, 0, 60 / 100f);
                                    }

                                    // middle zone smoothing
                                    else if (y < config.zone2Start + 5)
                                    {
                                        int difference = (config.zone2Start + 5) - y;
                                        returnColor = Color.getHSBColor(0, 0, (60 + (20f / difference)) / 100f);
                                    }

                                    // first zone
                                    else if (y < config.zone1Start)
                                    {
                                        returnColor = Color.getHSBColor(0, 0, 80 / 100f);
                                    } else if (y < config.zone1Start + 5)
                                    {
                                        int difference = (config.zone1Start + 5) - y;
                                        returnColor = Color.getHSBColor(0, 0, (80 + (20f / difference)) / 100f);
                                    }

                                    return returnColor.getRGB();
                                }
                            }

                            // return white, nothing happens
                            return 16777215;
                        },
                        newBlock

                );
            }
        }
    }
}
