package me.declipsonator.raingrow.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.GameRules;

@Mixin(GameRules.IntRule.class)
public interface IntegerRuleAccessor {

    @Invoker
    static GameRules.Type<GameRules.IntRule> invokeCreate(int initialValue) {
        return null;
    }

}