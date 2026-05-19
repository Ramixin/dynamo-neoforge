package net.ramixin.dynamo.neoforge.events.contexts;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.ramixin.stator.events.contexts.PlayerJoinedServerContext;

public record PlayerJoinedServerContextImpl(Player player) implements PlayerJoinedServerContext {

    public PlayerJoinedServerContextImpl(PlayerEvent.PlayerLoggedInEvent event) {
        this(event.getEntity());
    }

}
