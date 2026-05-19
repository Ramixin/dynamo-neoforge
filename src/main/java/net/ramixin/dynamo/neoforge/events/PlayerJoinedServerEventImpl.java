package net.ramixin.dynamo.neoforge.events;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.ramixin.dynamo.neoforge.events.contexts.PlayerJoinedServerContextImpl;
import net.ramixin.stator.events.Event;
import net.ramixin.stator.events.EventDispatcher;
import net.ramixin.stator.events.annotations.PlayerJoinedServerEvent;
import net.ramixin.stator.events.contexts.Context;
import net.ramixin.stator.events.contexts.PlayerJoinedServerContext;

import java.lang.annotation.Annotation;

public class PlayerJoinedServerEventImpl implements Event<PlayerJoinedServerContext, Void> {
    @Override
    public Class<? extends Annotation> getAnnotationClass() {
        return PlayerJoinedServerEvent.class;
    }

    @Override
    public Class<? extends Context> getContextClass() {
        return PlayerJoinedServerContext.class;
    }

    @Override
    public Class<Void> getReturnClass() {
        return void.class;
    }

    @Override
    public void registerNativeListener(EventDispatcher<PlayerJoinedServerContext, Void> dispatcher) {
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> dispatcher.dispatch(new PlayerJoinedServerContextImpl(event)));
    }
}
