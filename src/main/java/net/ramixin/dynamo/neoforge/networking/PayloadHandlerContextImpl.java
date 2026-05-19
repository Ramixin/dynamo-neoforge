package net.ramixin.dynamo.neoforge.networking;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.ramixin.stator.networking.PayloadHandlerContext;

public record PayloadHandlerContextImpl<T>(T payload, Player player) implements PayloadHandlerContext<T> {

    public PayloadHandlerContextImpl(T payload, IPayloadContext context) {
        this(payload, context.player());
    }
}
