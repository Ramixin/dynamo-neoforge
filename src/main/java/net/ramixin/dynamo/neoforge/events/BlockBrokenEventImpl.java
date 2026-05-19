package net.ramixin.dynamo.neoforge.events;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.ramixin.dynamo.neoforge.events.contexts.BlockBrokenContextImpl;
import net.ramixin.stator.events.Event;
import net.ramixin.stator.events.EventDispatcher;
import net.ramixin.stator.events.annotations.BlockBrokenEvent;
import net.ramixin.stator.events.contexts.BlockBrokenContext;
import net.ramixin.stator.events.contexts.Context;

import java.lang.annotation.Annotation;

public final class BlockBrokenEventImpl implements Event<BlockBrokenContext, Void> {
    @Override
    public Class<? extends Annotation> getAnnotationClass() {
        return BlockBrokenEvent.class;
    }

    @Override
    public Class<? extends Context> getContextClass() {
        return BlockBrokenContext.class;
    }

    @Override
    public Class<Void> getReturnClass() {
        return void.class;
    }

    @Override
    public void registerNativeListener(EventDispatcher<BlockBrokenContext, Void> dispatcher) {
        NeoForge.EVENT_BUS.addListener((BlockEvent.BreakEvent event) ->
                dispatcher.dispatch(new BlockBrokenContextImpl(event.getLevel(), event.getPlayer(), event.getPos(), event.getState(), event.getLevel().getBlockEntity(event.getPos())))
        );
    }
}
