package farn.AdvanceTooltip;

import farn.AdvanceTooltip.mixin.GuiElementAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.item.*;
import net.minecraft.locale.LanguageManager;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.osl.lifecycle.api.MinecraftEvents;

import java.util.ArrayList;
import java.util.List;

public class AdvanceToolTipPlus implements ClientModInitializer {

	public static Minecraft mc;
	private static AdvanceToolTipPlusConfig config;
	public void onInitializeClient() {
		MinecraftEvents.START.register(minecraft -> {
			this.mc = minecraft;
			config = AdvanceToolTipPlusConfig.instance;
			config.readConfig();

		});
		MinecraftEvents.READY_WORLD.register(minecraft ->{
			if(config.giveDebugItem && minecraft.player != null) {
				minecraft.player.dropItem(new ItemStack(Item.CLOCK));
				minecraft.player.dropItem(new ItemStack(Item.COMPASS));
				minecraft.player.dropItem(new ItemStack(Item.PORKCHOP));
				minecraft.player.dropItem(new ItemStack(Item.RECORD_CAT));
				minecraft.player.dropItem(new ItemStack(Item.DIAMOND_AXE));
			}
		});
	}

	public static void renderTooltip(Screen screen, InventorySlot slot, int mouseX, int mouseY, int left, int right) {

		LanguageManager lang = LanguageManager.getInstance();
		ItemStack stack = slot.getStack();
		String name = lang.translateName(stack.getTranslationKey()).trim();
		List<String> lines = new ArrayList<>();
		List<Integer> colors = new ArrayList<>();
		addText(name, 0xFFFFFF, lines, colors);

		if (stack.getItem().id == Item.COMPASS.id) {
			String compassText;
			if (mc.world.dimension.isNether) {
				compassText = config.compassSpinning;
			} else {
				int spawnX = MathHelper.floor(mc.world.getSpawnPoint().x);
				int spawnZ = MathHelper.floor(mc.world.getSpawnPoint().z);
				compassText = String.format(config.compassPointing, spawnX, spawnZ);
			}
			addText(compassText, -6250336, lines, colors);

		} else if (stack.getItem().id == Item.CLOCK.id) {
			float time = (mc.world.getTimeOfDay(1.0F) * 24.0F + 12.0F) % 24.0F;
			int hours = (int) Math.floor(time);
			int minutes = (int) Math.floor(time * 60.0F) - hours * 60;
			addText(String.format(config.clockTime, hours, minutes), -6250336, lines, colors);
		} else if (stack.getItem() instanceof FoodItem) {
			addText(String.format(config.foodHeal, ((FoodItem) stack.getItem()).getHungerPoints()), 5592575, lines, colors);
		} else if (stack.getItem() instanceof MusicDiscItem) {
			String discName = capitalizeFirst(((MusicDiscItem) stack.getItem()).recordType);
			addText(String.format(config.music, discName), -6250336, lines, colors);
		} else if (stack.isDamageable()) {
			String durability = String.format(config.durability, config.formatDurability(stack.getMaxDamage() - stack.getDamage(), stack.getMaxDamage()));
			if (stack.getItem() instanceof SwordItem || stack.getItem() instanceof ToolItem) {
				addText(String.format(config.toolDamage, stack.getItem().getAttackDamage((Entity) null)), 5592575, lines, colors);
			}
			addText(durability, -6250336, lines, colors);
		}

		int tooltipX = mouseX - left + 12;
		int tooltipY = mouseY - right - 12;
		int[] colorsArray = IntegerListToIntArray(colors);
		drawTooltip(screen, lines.toArray(new String[0]), colorsArray, tooltipX, tooltipY);
	}

	public static void drawTooltip(Screen screen, String[] lines, int[] colors, int x, int y) {
		if (lines.length == 0) return;


		int width = 0;
		for (String line : lines) {
			int lineWidth = mc.textRenderer.getWidth(line);
			if (lineWidth > width) width = lineWidth;
		}

		int height = lines.length * 8 + (lines.length - 1) * 2;

		if (x + width > screen.width) {
			x -= 28 + width;
		}
		if (y + height + 6 > screen.height) {
			y = screen.height - height - 6;
		}

		if(config.modernStyle) {
			drawTooltipBackground(screen, x, y, width, height);
		} else {
			fillGradient( screen,x - 3, y - 3, x + width + 3, y + height + 3, -1073741824, -1073741824);
		}

		int yOffset = 0;
		for (int i = 0; i < lines.length; i++) {
			int color = (colors != null && i < colors.length) ? colors[i] : 0xFFFFFF;
			mc.textRenderer.drawWithShadow(lines[i], x, y + yOffset, color);
			yOffset += 8 + 2;
		}
	}

	public static void drawTooltipBackground(Screen screen, int x, int y, int width, int height) {
		int bgColor = 0xFE000000;
		int borderStart = 0x505050FF;
		int borderEnd = (borderStart & 0x00FFFFFE) >> 1 | (borderStart & 0xFF000000);

		fillGradient(screen, x - 3, y - 4, x + width + 3, y - 3, bgColor, bgColor);
		fillGradient(screen, x - 3, y + height + 3, x + width + 3, y + height + 4, bgColor, bgColor);
		fillGradient(screen, x - 3, y - 3, x + width + 3, y + height + 3, bgColor, bgColor);
		fillGradient(screen, x - 4, y - 3, x - 3, y + height + 3, bgColor, bgColor);
		fillGradient(screen, x + width + 3, y - 3, x + width + 4, y + height + 3, bgColor, bgColor);

		fillGradient(screen, x - 3, y - 2, x - 2, y + height + 2, borderStart, borderEnd);
		fillGradient(screen, x + width + 2, y - 2, x + width + 3, y + height + 2, borderStart, borderEnd);
		fillGradient(screen, x - 3, y - 3, x + width + 3, y - 2, borderStart, borderStart);
		fillGradient(screen, x - 3, y + height + 2, x + width + 3, y + height + 3, borderEnd, borderEnd);
	}

	public static void addText(String text, int color, List<String> lines, List<Integer> colors) {
		lines.add(text);
		colors.add(color);
	}

	public static int[] IntegerListToIntArray(List<Integer> integerList) {
		int[] colorsArray = new int[integerList.size()];
		for (int i = 0; i < integerList.size(); i++) {
			colorsArray[i] = integerList.get(i);
		}
		return colorsArray;
	}

	private static String capitalizeFirst(String s) {
		if (s == null || s.isEmpty()) return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}


	public static void fillGradient(Screen screen, int x1, int y1, int x2, int y2, int color1, int color2) {
		((GuiElementAccessor)screen).invokeFillGradient(x1, y1, x2, y2, color1, color2);
	}
}
