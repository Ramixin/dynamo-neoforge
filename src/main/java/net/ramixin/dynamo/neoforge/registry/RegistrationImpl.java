package net.ramixin.dynamo.neoforge.registry;

import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ramixin.dynamo.neoforge.networking.PayloadHandlerContextImpl;
import net.ramixin.stator.networking.PayloadHandlerContext;
import net.ramixin.stator.registry.Registrant;
import net.ramixin.stator.registry.RegistrationService;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class RegistrationImpl implements RegistrationService {

    private static final HashMap<Registry<?>, DeferredRegister<?>> deferredRegisters = new HashMap<>();
    private static final Queue<Consumer<PayloadRegistrar>> payloads = new ConcurrentLinkedQueue<>();
    private static boolean frozen = false;

    @SuppressWarnings("unchecked")
    @Override
    public <T, V extends T> Registrant<V> entry(Registry<T> registry, Identifier id, Supplier<V> value) {
        if(frozen)
            throw new IllegalStateException("Cannot register entries after init");
        if(!deferredRegisters.containsKey(registry)) {
            DeferredRegister<?> register = DeferredRegister.create(registry, id.getNamespace());
            deferredRegisters.put(registry, register);
        }
        DeferredRegister<T> deferredRegister = (DeferredRegister<T>) deferredRegisters.get(registry);
        DeferredHolder<T, V> holder = deferredRegister.register(id.getPath(), value);
        return new RegistrantImpl<>(holder);
    }

    @Override
    public <T extends CustomPacketPayload> void clientboundPayload(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        if (frozen)
            throw new IllegalStateException("Cannot register clientbound payload after freezing");
        payloads.add(registrar -> registrar.playToClient(type, codec));
    }

    @Override
    public <T extends CustomPacketPayload> void serverboundPayload(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec, Consumer<PayloadHandlerContext<T>> handler) {
        if(frozen)
            throw new IllegalStateException("Cannot register serverbound payload after freezing");
        payloads.add(registrar -> registrar.playToServer(type, codec, (payload, ctx) -> handler.accept(new PayloadHandlerContextImpl<>(payload, ctx))));
    }

    public static void attachAllEntries(IEventBus bus) {
        for(DeferredRegister<?> register : deferredRegisters.values()) {
            register.register(bus);
        }
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

    public static void freeze() {
        frozen = true;
    }
}
