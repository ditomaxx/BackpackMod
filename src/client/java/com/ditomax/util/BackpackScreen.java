package com.ditomax.util;

import com.ditomax.util.gui.BackpackScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BackpackScreen extends HandledScreen<BackpackScreenHandler> {

    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/generic_54.png");

    public BackpackScreen(BackpackScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);

        this.backgroundWidth = 176;
        this.backgroundHeight = 168;
        this.titleX = 8;
        this.titleY = 6;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 0x404040, false);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);

        context.drawTexture(RenderLayer::getGuiTextured,TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, 71, 256, 256);
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, this.x, this.y + 71, 0, 125, this.backgroundWidth, 96, 256, 256);
    }
}