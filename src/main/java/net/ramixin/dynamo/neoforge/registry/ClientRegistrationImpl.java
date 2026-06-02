package net.ramixin.dynamo.neoforge.registry;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.ramixin.dynamo.neoforge.networking.ClientPayloadHandlerContextImpl;
import net.ramixin.stator.networking.ClientPayloadHandlerContext;
import net.ramixin.stator.registry.ClientRegistrationService;
import net.ramixin.stator.registry.Registrant;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class ClientRegistrationImpl implements ClientRegistrationService {

    private static final Queue<Consumer<RegisterClientPayloadHandlersEvent>> payloads = new ConcurrentLinkedQueue<>();
    private static boolean frozen = false;
    private static final Queue<Consumer<RegisterMenuScreensEvent>> screens = new ConcurrentLinkedQueue<>();

    @Override
    public <T extends CustomPacketPayload> void clientboundHandler(CustomPacketPayload.Type<T> type, Consumer<ClientPayloadHandlerContext<T>> handler) {
        if(frozen)
            throw new IllegalStateException("Cannot register clientbound payload handler after freezing");
        payloads.add(registrar -> registrar.register(type, (payload, ctx) -> handler.accept(new ClientPayloadHandlerContextImpl<>(payload, ctx))));
    }

    @Override
    public <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void screen(Registrant<MenuType<M>> menuType, TriFunction<M, Inventory, Component, S> triFunction) {
        if(frozen)
            throw new IllegalStateException("Cannot register menu screen after freezing");
        screens.add(event -> event.register(menuType.get(), triFunction::apply));
    }

    public static void finalizeHandlers(IEventBus bus) {
        bus.addListener((RegisterClientPayloadHandlersEvent event) -> {
            while(!payloads.isEmpty()) {
                payloads.poll().accept(event);
            }
        });
    }

    public static void finalizeScreens(IEventBus bus) {
        bus.addListener((RegisterMenuScreensEvent event) -> {
            while(!screens.isEmpty()) {
                screens.poll().accept(event);
            }
        });
    }

    public static void freeze() {
        frozen = true;
    }
}
