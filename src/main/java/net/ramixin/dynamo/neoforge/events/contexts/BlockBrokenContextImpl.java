package net.ramixin.dynamo.neoforge.events.contexts;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.ramixin.stator.events.contexts.BlockBrokenContext;

public record BlockBrokenContextImpl(LevelAccessor level, Player player, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity) implements BlockBrokenContext {

}
