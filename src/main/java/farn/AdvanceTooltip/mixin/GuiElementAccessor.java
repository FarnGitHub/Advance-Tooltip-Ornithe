package farn.AdvanceTooltip.mixin;

import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.render.TextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.w3c.dom.Text;

@Mixin(GuiElement.class)
public interface GuiElementAccessor {

	@Invoker("fillGradient")
	public void invokeFillGradient(int x1, int y1, int x2, int y2, int color1, int color2);
}
