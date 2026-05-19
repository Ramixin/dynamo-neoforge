package net.ramixin.dynamo.neoforge.platform;

import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.ramixin.stator.platform.PlatformService;

import java.nio.file.Path;

public final class PlatformImpl implements PlatformService {

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public Path getGameDirectory() {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public boolean isDevEnv() {
        return !FMLLoader.getCurrent().isProduction();
    }
}
