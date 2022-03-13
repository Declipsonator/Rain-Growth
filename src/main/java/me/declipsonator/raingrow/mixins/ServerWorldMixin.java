package me.declipsonator.raingrow.mixins;

import me.declipsonator.raingrow.RainGrow;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.GameRules;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {


    @Inject(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V"), cancellable = true)
    private void speedUpTicksIfRain(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        ServerWorld thisAsWorld = (ServerWorld) (Object) this;

        if(!thisAsWorld.isRaining()) return;

        ChunkPos chunkPos = chunk.getPos();
        int startX = chunkPos.getStartX();
        int startZ = chunkPos.getStartZ();
        Profiler profiler = thisAsWorld.getProfiler();

        profiler.swap("tickBlocks");
        int randomRainGrowth = thisAsWorld.getGameRules().getInt(RainGrow.RANDOM_RAIN_GROWTH_TICK_SPEED);
        if (randomTickSpeed > 0) {
            ChunkSection[] sectionArray = chunk.getSectionArray();

            for (ChunkSection chunkSection : sectionArray) {
                if (chunkSection.hasRandomTicks()) {
                    int yOffset = chunkSection.getYOffset();

                    for (int l = 0; l < Math.max(randomTickSpeed, randomRainGrowth); ++l) {
                        BlockPos blockPos = thisAsWorld.getRandomPosInChunk(startX, yOffset, startZ, 15);
                        profiler.push("randomTick");
                        BlockState blockState = chunkSection.getBlockState(blockPos.getX() - startX, blockPos.getY() - yOffset, blockPos.getZ() - startZ);
                        if (blockState.hasRandomTicks() && shouldTreatAsCrop(blockState.getBlock()) && l < randomRainGrowth) {
                            boolean skylight = true;
                            for(int i = 1; i <= thisAsWorld.getTopY() - blockPos.getY(); i++) {
                                if(!thisAsWorld.getBlockState(blockPos.add(0, i, 0)).isAir()) {
                                    skylight = false;
                                    break;
                                }
                            }
                            if(skylight) blockState.randomTick(thisAsWorld, blockPos, thisAsWorld.random);
                            else if(l < randomTickSpeed) blockState.randomTick(thisAsWorld, blockPos, thisAsWorld.random);

                        } else if(blockState.hasRandomTicks() && !(blockState.getBlock() instanceof CropBlock) && l < randomTickSpeed) {
                            blockState.randomTick(thisAsWorld, blockPos, thisAsWorld.random);
                        }

                        FluidState fluidState = blockState.getFluidState();
                        if (fluidState.hasRandomTicks() && l < randomTickSpeed) {
                            fluidState.onRandomTick(thisAsWorld, blockPos, thisAsWorld.random);
                        }

                        profiler.pop();
                    }
                }
            }
        }

        profiler.pop();

        ci.cancel();
    }

    private boolean shouldTreatAsCrop(Block block) {
        return block instanceof CropBlock ||
                block instanceof SaplingBlock ||
                block instanceof SugarCaneBlock ||
                block instanceof BambooBlock ||
                block instanceof BambooSaplingBlock ||
                block instanceof CocoaBlock ||
                block instanceof SweetBerryBushBlock;
    }
}
