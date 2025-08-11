package farn.AdvanceTooltip.mixin;

import farn.AdvanceTooltip.AdvanceToolTipPlus;
import net.minecraft.client.gui.screen.inventory.menu.InventoryMenuScreen;
import net.minecraft.inventory.slot.InventorySlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryMenuScreen.class)
public abstract class InventoryMenuScreenMixin {

	@Shadow
	protected int backgroundWidth;
	@Shadow
	protected int backgroundHeight;

	@Shadow
	abstract InventorySlot getHoveredSlot(int mouseX, int mouseY);

	// Inject after vanilla draws hovered slot tooltip in render()
	@Inject(method = "render", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/render/TextRenderer;drawWithShadow(Ljava/lang/String;III)V", ordinal = 0), cancellable = true)
	private void afterRenderTooltips(int mouseX, int mouseY, float tickDelta, CallbackInfo ci) {
		InventoryMenuScreen screen = (InventoryMenuScreen) (Object) this;

		// Calculate gui left/top (matching vanilla code)
		int left = (screen.width - backgroundWidth) / 2;
		int right = (screen.height - backgroundHeight) / 2;

		InventorySlot hoveredSlot = getHoveredSlot(mouseX, mouseY);

		// Only render if hovering a slot with stack, and no cursor item
		if (hoveredSlot != null
			&& hoveredSlot.hasStack()
			&& AdvanceToolTipPlus.mc.player.inventory.getCursorStack() == null) {

			AdvanceToolTipPlus.renderTooltip(screen, hoveredSlot, mouseX, mouseY, left, right);
			ci.cancel();
		}
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/inventory/menu/InventoryMenuScreen;fillGradient(IIIIII)V", ordinal = 1))
	private void redirectFillGradient(InventoryMenuScreen instance, int x1, int y1, int x2, int y2, int color1, int color2) {
		return;
	}
}
