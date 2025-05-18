package com.rokkhan.trackmon.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class TrackmonCheckbox extends AbstractWidget {

    // Whether the checkbox is currently checked
    private boolean checked;

    /**
     * Constructor for the custom checkbox widget.
     *
     * @param x            X position of the widget
     * @param y            Y position of the widget
     * @param width        Width of the widget
     * @param height       Height of the widget
     * @param message      Displayed label (e.g. Pokémon name)
     * @param initialState Whether the checkbox is initially checked
     */
    public TrackmonCheckbox(int x, int y, int width, int height, Component message, boolean initialState) {
        super(x, y, width, height, message);
        this.checked = initialState;
    }

    /**
     * Called when the checkbox is clicked.
     * Toggles the checked state.
     */
    @Override
    public void onClick(double mouseX, double mouseY) {
        this.checked = !this.checked;
    }

    /**
     * Renders the checkbox widget.
     */
    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int boxSize = 12; // Size of the checkbox square
        int boxX = getX(); // Left position of the checkbox
        int boxY = getY() + (height - boxSize) / 2; // Vertically centered

        // Draw background highlight when hovered or focused
        if (isHoveredOrFocused()) {
            graphics.fill(getX() - 2, getY() - 1, getX() + width + 2, getY() + height + 1, 0x552222FF);
        }

        // Draw the checkbox square
        graphics.fill(boxX, boxY, boxX + boxSize, boxY + boxSize, 0xFFAAAAAA);

        // If checked, draw a green checkmark
        if (checked) {
            graphics.drawString(Minecraft.getInstance().font, "✔", boxX + 2, boxY - 1, 0xFF00FF00, false);
        }

        // Draw the label text next to the checkbox
        graphics.drawString(Minecraft.getInstance().font, this.getMessage(), boxX + boxSize + 5, getY() + (height - 8) / 2, 0xFFFFFF, false);
    }

    /**
     * Optional: Narration support for accessibility (not implemented here).
     */
    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        // No narration implemented
    }

    /**
     * Returns whether the checkbox is currently checked.
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * Sets the checked state of the checkbox.
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
