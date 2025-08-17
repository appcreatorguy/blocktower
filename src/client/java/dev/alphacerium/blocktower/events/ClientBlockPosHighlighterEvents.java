package dev.alphacerium.blocktower.events;

import dev.alphacerium.blocktower.render.ClientBlockPosHighlighter;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class ClientBlockPosHighlighterEvents {
    public static void register() {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(ClientBlockPosHighlighterEvents::onRenderEvent);
    }

    public static void onRenderEvent(WorldRenderContext context) {
        ClientBlockPosHighlighter.render(context);
    }
}
