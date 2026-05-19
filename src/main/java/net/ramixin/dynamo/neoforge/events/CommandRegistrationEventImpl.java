package net.ramixin.dynamo.neoforge.events;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.ramixin.dynamo.neoforge.events.contexts.CommandRegistrationContextImpl;
import net.ramixin.stator.events.Event;
import net.ramixin.stator.events.EventDispatcher;
import net.ramixin.stator.events.annotations.CommandRegistrationEvent;
import net.ramixin.stator.events.contexts.CommandRegistrationContext;
import net.ramixin.stator.events.contexts.Context;

import java.lang.annotation.Annotation;

public class CommandRegistrationEventImpl implements Event<CommandRegistrationContext, Void> {
    @Override
    public Class<? extends Annotation> getAnnotationClass() {
        return CommandRegistrationEvent.class;
    }

    @Override
    public Class<? extends Context> getContextClass() {
        return CommandRegistrationContext.class;
    }

    @Override
    public Class<Void> getReturnClass() {
        return void.class;
    }

    @Override
    public void registerNativeListener(EventDispatcher<CommandRegistrationContext, Void> dispatcher) {
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> dispatcher.dispatch(new CommandRegistrationContextImpl(event)));
    }
}
