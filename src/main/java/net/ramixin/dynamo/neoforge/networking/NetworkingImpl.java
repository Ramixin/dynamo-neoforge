package net.ramixin.dynamo.neoforge.networking;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.ramixin.stator.networking.NetworkingService;
import net.ramixin.stator.networking.PayloadHandlerContext;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public final class NetworkingImpl implements NetworkingService {

    private static final Queue<Consumer<PayloadRegistrar>> payloads = new ConcurrentLinkedQueue<>();
    private static boolean frozen = false;

    @Override
    public <T extends CustomPacketPayload> void registerClientbound(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        if (frozen)
            throw new IllegalStateException("Cannot register clientbound payload after freezing");
        payloads.add(registrar -> registrar.playToClient(type, codec));
    }

    @Override
    public <T extends CustomPacketPayload> void registerServerbound(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec, Consumer<PayloadHandlerContext<T>> handler) {
        if(frozen)
            throw new IllegalStateException("Cannot register serverbound payload after freezing");
        payloads.add(registrar -> registrar.playToServer(type, codec, (payload, ctx) -> handler.accept(new PayloadHandlerContextImpl<>(payload, ctx))));
    }

    @Override
    public void sendClientbound(ServerPlayer player, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    public static void freeze() {
        frozen = true;
    }

    public static void finalizePayloads(IEventBus bus) {
        bus.addListener((Consumer<RegisterPayloadHandlersEvent>) event -> {
            PayloadRegistrar registrar = event.registrar("1");
            while(!payloads.isEmpty()) {
                Consumer<PayloadRegistrar> payload = payloads.poll();
                payload.accept(registrar);
            }
        });
    }
}
