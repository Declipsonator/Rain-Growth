package me.declipsonator.raingrow;

import net.fabricmc.api.ModInitializer;
import net.minecraft.world.GameRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RainGrow implements ModInitializer {
    public static final Logger LOG = LogManager.getLogger();
    public static GameRules.Key<GameRules.IntRule> RANDOM_RAIN_GROWTH_TICK_SPEED;


    @Override
    public void onInitialize() {
        LOG.info("Initialized RainGrow");
    }
}
