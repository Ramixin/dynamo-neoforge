package net.ramixin.dynamo.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.moddiscovery.NightConfigWrapper;
import net.neoforged.neoforgespi.language.IConfigurable;
import net.ramixin.dynamo.neoforge.registry.ClientRegistrationImpl;
import net.ramixin.dynamo.neoforge.registry.RegistrationImpl;
import net.ramixin.stator.StatorClientInitializer;
import net.ramixin.stator.StatorInitializer;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Mod("dynamo")
public class Dynamo {

    private static final Map<String, StatorInitializer> mainInitializers = new LinkedHashMap<>();
    private static final Map<String, StatorClientInitializer> clientInitializers = new LinkedHashMap<>();

    public Dynamo(IEventBus modEventBus) {

        ModList.get().forEachModContainer((_, modContainer) -> {
            List<? extends IConfigurable> mixsonConfig = modContainer.getModInfo().getOwningFile().getConfig().getConfigList("stator");
            for(IConfigurable configurable : mixsonConfig) {
                if(!(configurable instanceof NightConfigWrapper wrapper)) continue;
                findEntrypoints("main", wrapper, modContainer.getModId(), obj -> {
                    if(!(obj instanceof StatorInitializer statorInitializer))
                        throw error(modContainer.getModId(), new IllegalStateException("main entrypoint must implement StatorInitializer"));
                    mainInitializers.put(modContainer.getModId(), statorInitializer);
                });
                findEntrypoints("client", wrapper, modContainer.getModId(), obj -> {
                    if(!(obj instanceof StatorClientInitializer statorClientInitializer))
                        throw error(modContainer.getModId(), new IllegalStateException("client entrypoint must implement StatorClientInitializer"));
                    clientInitializers.put(modContainer.getModId(), statorClientInitializer);
                });
            }
        });

        modEventBus.addListener(Dynamo::initEvent);
        modEventBus.addListener(Dynamo::clientInitEvent);
    }

    public static void initEvent(FMLCommonSetupEvent event) {
        IEventBus bus = event.getContainer().getEventBus();
        if(bus == null) throw new IllegalStateException("cannot init. EventBus is null");
        for(Map.Entry<String, StatorInitializer> entry : mainInitializers.entrySet()) {
            try {
                entry.getValue().initialize();
            } catch (Exception e) {
                throw error(entry.getKey(), new RuntimeException("Failed to run main initialization: ", e));
            }
        }
        RegistrationImpl.attachAllEntries(bus);
        RegistrationImpl.finalizePayloads(bus);
        RegistrationImpl.freeze();
    }

    private static void findEntrypoints(String entrypoint, NightConfigWrapper config, String modId, Consumer<Object> applicator) {
        Optional<Object> maybeEntries = config.getConfigElement(entrypoint);
        if(maybeEntries.isEmpty()) return;
        if(!(maybeEntries.get() instanceof List<?> list))
            throw error(modId, new IllegalStateException(String.format("'%s' field in [[stator]] must be a list of strings", entrypoint)));
        for(Object entry : list) {
            if(!(entry instanceof String className))
                throw error(modId, new IllegalStateException(String.format("'%s' field in [[stator]] must be a list of strings", entrypoint)));
            try {
                Class<?> modClass = Class.forName(className);
                Constructor<?> constructor = modClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                Object modClassInstance = modClass.getDeclaredConstructor().newInstance();
                applicator.accept(modClassInstance);
            } catch (NoSuchMethodException e) {
                throw error(modId, new IllegalStateException("failed to locate constructor: ", e));
            } catch (InstantiationException e) {
                throw error(modId, new IllegalStateException("failed to call no-arg constructor: ", e));
            } catch (Exception e) {
                throw error(modId, e);
            }
        }
    }

    private static RuntimeException error(String modId, Exception e) {
        return new RuntimeException("Failed to process mod '"+modId+"': ", e);
    }

    public static void clientInitEvent(FMLClientSetupEvent event) {
        IEventBus bus = event.getContainer().getEventBus();
        if(bus == null) throw new IllegalStateException("cannot client init. EventBus is null");
        for(Map.Entry<String, StatorClientInitializer> entry : clientInitializers.entrySet()) {
            try {
                entry.getValue().initializeClient();
            } catch (Exception e) {
                throw error(entry.getKey(), new RuntimeException("Failed to run client initialization: ", e));
            }
        }
        ClientRegistrationImpl.finalizeHandlers(bus);
        ClientRegistrationImpl.finalizeScreens(bus);
        ClientRegistrationImpl.freeze();
    }

}
