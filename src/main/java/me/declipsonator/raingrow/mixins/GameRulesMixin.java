package me.declipsonator.raingrow.mixins;

import me.declipsonator.raingrow.RainGrow;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameRules.class)
public class GameRulesMixin {
    @Shadow
    private static <T extends GameRules.Rule<T>> GameRules.Key<T> register(String name, GameRules.Category category, GameRules.Type<T> type) {
        return null;
    }


    static{
        RainGrow.RANDOM_RAIN_GROWTH_TICK_SPEED = register("randomCropRainTickSpeed",GameRules.Category.MOBS, IntegerRuleAccessor.invokeCreate(4));
    }
}
