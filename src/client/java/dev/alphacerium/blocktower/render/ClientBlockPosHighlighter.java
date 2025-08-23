package dev.alphacerium.blocktower.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.alphacerium.blocktower.Blocktower;
import dev.alphacerium.blocktower.BlocktowerClient;
import dev.alphacerium.blocktower.records.BlockPosColor;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.OptionalDouble;

public class ClientBlockPosHighlighter {
    public static RenderPipeline LINES_NO_DEPTH = RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
            .withLocation("pipeline/debug_lines")
            .withVertexShader("core/rendertype_lines")
            .withFragmentShader(Identifier.of(Blocktower.MOD_ID, "frag/rendertype_lines"))
            .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINES)
            .withCull(false)
            .withBlend(BlendFunction.TRANSLUCENT)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .build();

    private static final RenderLayer renderLayer = RenderLayer.of(
            "debug_lines",
            1536,
            LINES_NO_DEPTH,
            RenderLayer.MultiPhaseParameters.builder().lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(1))).build(false)
    );

    public static synchronized void render(WorldRenderContext context) {
        if (BlocktowerClient.RENDER_QUEUE.isEmpty()) {
            return;
        }

        MatrixStack matrixStack = context.matrixStack();
        if (matrixStack == null) return;

        matrixStack.push();
        Tessellator tess = Tessellator.getInstance();

        Vec3d cameraPos = context.camera().getPos();
        matrixStack.translate(-cameraPos.getX(), -cameraPos.getY(), -cameraPos.getZ());

        for (BlockPosColor blockPosColor : BlocktowerClient.RENDER_QUEUE) {
            BufferBuilder buff = tess.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
            renderBlock(buff, matrixStack.peek().getPositionMatrix(), blockPosColor);
            var builtBuff = buff.endNullable();
            if (builtBuff != null) renderLayer.draw(builtBuff);
        }

        matrixStack.pop();
    }

    private static void renderBlock(BufferBuilder buffer, Matrix4f matrix, BlockPosColor blockPosColor) {
        final float padding = 0.001f;
        final float size = 1.0f;
        final int x = blockPosColor.pos().getX(), y = blockPosColor.pos().getY(), z = blockPosColor.pos().getZ();

        final float minBlockX = x - padding, minBlockY = y - padding, minBlockZ = z - padding;
        final float maxBlockX = x + padding + size, maxBlockY = y + padding + size, maxBlockZ = z + padding + size;

        // TOP FACE (maxBlockY)
        buffer.vertex(matrix, minBlockX, maxBlockY, minBlockZ).color(blockPosColor.color());
        buffer.vertex(matrix, maxBlockX, maxBlockY, minBlockZ).color(blockPosColor.color());
        buffer.vertex(matrix, maxBlockX, maxBlockY, minBlockZ).color(blockPosColor.color());
        buffer.vertex(matrix, maxBlockX, maxBlockY, maxBlockZ).color(blockPosColor.color());
        buffer.vertex(matrix, maxBlockX, maxBlockY, maxBlockZ).color(blockPosColor.color());
        buffer.vertex(matrix, minBlockX, maxBlockY, maxBlockZ).color(blockPosColor.color());
        buffer.vertex(matrix, minBlockX, maxBlockY, maxBlockZ).color(blockPosColor.color());
        buffer.vertex(matrix, minBlockX, maxBlockY, minBlockZ).color(blockPosColor.color());

        // EDGE 1
        buffer.vertex(matrix, maxBlockX, minBlockY, maxBlockZ).color(blockPosColor.color());
        buffer.vertex(matrix, maxBlockX, maxBlockY, maxBlockZ).color(blockPosColor.color());

        // EDGE 2
        buffer.vertex(matrix, maxBlockX, minBlockY, minBlockZ).color(blockPosColor.color());
        buffer.vertex(matrix, maxBlockX, maxBlockY, minBlockZ).color(blockPosColor.color());

        // EDGE 3
        buffer.vertex(matrix, minBlockX, minBlockY, maxBlockZ).color(blockPosColor.color());
        buffer.vertex(matrix, minBlockX, maxBlockY, maxBlockZ).color(blockPosColor.color());

        // EDGE 4
        buffer.vertex(matrix, minBlockX, minBlockY, minBlockZ).color(blockPosColor.color());
        buffer.vertex(matrix, minBlockX, maxBlockY, minBlockZ).color(blockPosColor.color());

        // BOTTOM FACE (minBlockY)
        buffer.vertex(matrix, maxBlockX, minBlockY, minBlockZ).color(blockPosColor.color());
        buffer.vertex(matrix, maxBlockX, minBlockY, maxBlockZ).color(blockPosColor.color());
        buffer.vertex(matrix, maxBlockX, minBlockY, maxBlockZ).color(blockPosColor.color());
        buffer.vertex(matrix, minBlockX, minBlockY, maxBlockZ).color(blockPosColor.color());
        buffer.vertex(matrix, minBlockX, minBlockY, maxBlockZ).color(blockPosColor.color());
        buffer.vertex(matrix, minBlockX, minBlockY, minBlockZ).color(blockPosColor.color());
        buffer.vertex(matrix, minBlockX, minBlockY, minBlockZ).color(blockPosColor.color());
        buffer.vertex(matrix, maxBlockX, minBlockY, minBlockZ).color(blockPosColor.color());
    }
}
