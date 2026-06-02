package net.ramixin.dynamo.neoforge.networking;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.ramixin.stator.networking.NetworkingService;

public final class NetworkingImpl implements NetworkingService {

    @Override
    public void sendClientbound(ServerPlayer player, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }
}
