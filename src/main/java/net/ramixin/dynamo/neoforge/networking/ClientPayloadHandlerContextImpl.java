package net.ramixin.dynamo.neoforge.networking;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.ramixin.stator.networking.ClientPayloadHandlerContext;

public record ClientPayloadHandlerContextImpl<T>(T payload, Player player) implements ClientPayloadHandlerContext<T> {

    public ClientPayloadHandlerContextImpl(T payload, IPayloadContext ctx) {
        this(payload, ctx.player());
    }

}
