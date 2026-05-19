package net.ramixin.dynamo.neoforge.networking;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.ramixin.stator.networking.ClientNetworkingService;
import net.ramixin.stator.networking.ClientPayloadHandlerContext;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public final class ClientNetworkingImpl implements ClientNetworkingService {

    private static final Queue<Consumer<RegisterClientPayloadHandlersEvent>> payloads = new ConcurrentLinkedQueue<>();
    private static boolean frozen = false;

    @Override
    public <T extends CustomPacketPayload> void registerClientboundHandler(CustomPacketPayload.Type<T> type, Consumer<ClientPayloadHandlerContext<T>> handler) {
        if(frozen)
            throw new IllegalStateException("Cannot register clientbound payload handler after freezing");
        payloads.add(registrar -> registrar.register(type, (payload, ctx) -> handler.accept(new ClientPayloadHandlerContextImpl<>(payload, ctx))));
    }

    @Override
    public void sendServerbound(CustomPacketPayload payload) {
        ClientPacketDistributor.sendToServer(payload);
    }

    public static void finalizeHandlers(IEventBus bus) {
        bus.addListener((RegisterClientPayloadHandlersEvent event) -> {
            while(!payloads.isEmpty()) {
                payloads.poll().accept(event);
            }
        });
    }

    public static void freeze() {
        frozen = true;
    }
}
