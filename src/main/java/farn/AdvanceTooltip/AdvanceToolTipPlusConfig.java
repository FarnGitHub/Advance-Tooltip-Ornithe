package farn.AdvanceTooltip;

import java.io.*;
import java.util.Properties;

public class AdvanceToolTipPlusConfig {
	public String compassPointing = "Pointing: X:%d / Z:%d";
	public String compassSpinning = "Spinning";
	public String clockTime = "Time: %02d:%02d";
	public String foodHeal = "+%d Health Point";
	public String durability = "Durability: %s";
	public String durabilityAsNormal = "%d/%d";
	public String durabilityAsPercentage = "%.2f%%";
	public String music = "Music: %s";
	public String toolDamage = "+%d Attack Damage";

	public boolean enabledDurabilityAsPercentage = false;
	public boolean modernStyle = true;
	public boolean giveDebugItem = false;

	public static final AdvanceToolTipPlusConfig instance = new AdvanceToolTipPlusConfig();

	private final File configFile;
	private final Properties prop;

	private AdvanceToolTipPlusConfig() {
		this.configFile = new File(AdvanceToolTipPlus.mc.getRunDirectory(), "AdvanceTooltipConfig.prop");
		this.prop = new Properties();
	}

	public void readConfig() {
		if (configFile.exists()) {
			try (FileInputStream in = new FileInputStream(configFile)) {
				prop.load(in);
				compassPointing = prop.getProperty("compassPointing", compassPointing);
				compassSpinning = prop.getProperty("compassSpinning", compassSpinning);
				clockTime = prop.getProperty("clockTime", clockTime);
				foodHeal = prop.getProperty("foodHeal", foodHeal);
				durability = prop.getProperty("durability", durability);
				durabilityAsNormal = prop.getProperty("durabilityAsNormal", durabilityAsNormal);
				durabilityAsPercentage = prop.getProperty("durabilityAsPercentage", durabilityAsPercentage);
				enabledDurabilityAsPercentage = Boolean.parseBoolean(prop.getProperty("enabledDurabilityAsPercentage", String.valueOf(enabledDurabilityAsPercentage)));
				music = prop.getProperty("music", music);
				toolDamage = prop.getProperty("toolDamage", toolDamage);
				modernStyle = Boolean.parseBoolean(prop.getProperty("modernStyle", String.valueOf(modernStyle)));
				giveDebugItem = Boolean.parseBoolean(prop.getProperty("giveDebugItem", String.valueOf(giveDebugItem)));
			} catch (IOException e) {
				System.out.println("Failed to load AdvanceTooltipConfig.prop");
				e.printStackTrace();
			}
		} else {
			createOptionFile(); // Create file with defaults
		}
	}

	public void createOptionFile() {
		try (FileOutputStream out = new FileOutputStream(configFile)) {
			prop.setProperty("compassPointing", compassPointing);
			prop.setProperty("compassSpinning", compassSpinning);
			prop.setProperty("clockTime", clockTime);
			prop.setProperty("foodHeal", foodHeal);
			prop.setProperty("durability", durability);
			prop.setProperty("durabilityAsNormal", durabilityAsNormal);
			prop.setProperty("durabilityAsPercentage", durabilityAsPercentage);
			prop.setProperty("enabledDurabilityAsPercentage", String.valueOf(enabledDurabilityAsPercentage));
			prop.setProperty("music", music);
			prop.setProperty("toolDamage", toolDamage);
			prop.setProperty("modernStyle", String.valueOf(modernStyle));
			prop.setProperty("giveDebugItem", String.valueOf(giveDebugItem));
			prop.store(out, "Advance Tooltip Plus Config");
		} catch (IOException e) {
			System.out.println("Failed to save AdvanceTooltipConfig.prop");
			e.printStackTrace();
		}
	}

	public String formatDurability(int currentDurability, int maxDurability) {
		if (enabledDurabilityAsPercentage) {
			double percentage = (double) currentDurability / maxDurability * 100;
			return String.format(durabilityAsPercentage, percentage);
		} else {
			return String.format(durabilityAsNormal, currentDurability, maxDurability);
		}
	}
}
