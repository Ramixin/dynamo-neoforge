package net.ramixin.dynamo.neoforge.registry;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.ramixin.stator.registry.Registrant;

public final class RegistrantImpl<T, V extends T> implements Registrant<V> {

    private final DeferredHolder<T, V> deferredHolder;

    public RegistrantImpl(DeferredHolder<T ,V> deferredHolder) {
        this.deferredHolder = deferredHolder;
    }


    @Override
    public V get() {
        return deferredHolder.get();
    }
}
