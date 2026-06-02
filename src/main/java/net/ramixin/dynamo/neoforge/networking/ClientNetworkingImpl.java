package net.ramixin.dynamo.neoforge.networking;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.ramixin.stator.networking.ClientNetworkingService;

public final class ClientNetworkingImpl implements ClientNetworkingService {

    @Override
    public void sendServerbound(CustomPacketPayload payload) {
        ClientPacketDistributor.sendToServer(payload);
    }
}
