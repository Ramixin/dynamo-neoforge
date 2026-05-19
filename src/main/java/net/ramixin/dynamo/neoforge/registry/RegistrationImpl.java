package net.ramixin.dynamo.neoforge.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ramixin.stator.registry.Registrant;
import net.ramixin.stator.registry.RegistrationService;

import java.util.HashMap;
import java.util.function.Supplier;

public final class RegistrationImpl implements RegistrationService {

    private static final HashMap<Registry<?>, DeferredRegister<?>> deferredRegisters = new HashMap<>();
    private static boolean frozen = false;

    @SuppressWarnings("unchecked")
    @Override
    public <T, V extends T> Registrant<V> register(Registry<T> registry, Identifier id, Supplier<V> value) {
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

    public static void attachAllEntries(IEventBus bus) {
        for(DeferredRegister<?> register : deferredRegisters.values()) {
            register.register(bus);
        }
    }

    public static void freeze() {
        frozen = true;
    }
}
