package ru.landsproject.api.inventoryapi.bukkit.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftMetaBanner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.json.JSONException;
import ru.landsproject.api.util.Version;
import ru.landsproject.api.util.interfaces.Colorful;

public class ItemBuilder {
    private ConfigurationSection configurationSection;

    private ItemStack itemStack;

    private Colorful colorful;

    public ItemBuilder(Configuration configuration, String configurationSection) {
        this.configurationSection = configuration.getConfigurationSection("items." + configurationSection);
        this.itemStack = new ItemStack(Material.BEDROCK, 1);

        setColorful(new Colorful() {
            @Override
            public String getColor(String text) {
                return text.replace("&", "§");
            }
        });
    }

    public void setColorful(Colorful colorful) {
        this.colorful = colorful;
    }

    public Colorful getColorful() {
        return colorful;
    }

    public void buildMaterial() {
        Material material = Material.valueOf(this.configurationSection.getString("material"));
        if (material != Material.PLAYER_HEAD) {
            this.itemStack.setType(material);
            return;
        }
        String owner = this.configurationSection.getString("owner", null);
        if (owner != null)
            this.itemStack = Skull.getCustomSkullByPlayerName(owner);
        String textureUrl = this.configurationSection.getString("texture_url", null);
        if (textureUrl != null)
            this.itemStack = Skull.getCustomSkullByUrl(textureUrl);
        String value = this.configurationSection.getString("value", null);
        if (value != null) {
            try {
                this.itemStack = Skull.getCustomSkullByValue(value);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void buildAmount() {
        this.itemStack.setAmount(this.configurationSection.getInt("amount", 1));
    }

    public void buildDurabilityData() {
        Object object = this.configurationSection.get("durability_data", null);
        if (object == null)
            return;
        this.itemStack.setDurability((short)this.configurationSection.getInt("durability_data"));
    }

    public void buildDisplayName() {
        String displayName = this.configurationSection.getString("display_name", null);
        if (displayName != null) {
            ItemMeta itemMeta = this.itemStack.getItemMeta();
            itemMeta.setDisplayName(getColorful().getColor(displayName));
            this.itemStack.setItemMeta(itemMeta);
        }
    }

    public void buildDescription() {
        List<String> stringList = this.configurationSection.getStringList("description");
        if (stringList.isEmpty())
            return;
        List<String> lore = new ArrayList<>();
        for (String string : stringList)
            lore.add(getColorful().getColor(string));
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setLore(lore);
        this.itemStack.setItemMeta(itemMeta);
    }

    public void buildUnbreakable() {
        Object object = this.configurationSection.get("unbreakable", null);
        boolean unbreakable = (object != null) ? this.configurationSection.getBoolean("unbreakable") : true;
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setUnbreakable(unbreakable);
        this.itemStack.setItemMeta(itemMeta);
    }

    public void buildEnchanted() {
        boolean enchanted = this.configurationSection.getBoolean("enchanted", false);
        if (enchanted) {
            ItemMeta itemMeta = this.itemStack.getItemMeta();
            itemMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            this.itemStack.setItemMeta(itemMeta);
        }
    }

    public void buildRGB() {
        Object object = this.configurationSection.get("rgb", null);
        if (object == null)
            return;
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        ConfigurationSection rgbConfigurationSection = this.configurationSection.getConfigurationSection("rgb");
        int r = rgbConfigurationSection.getInt("r");
        int g = rgbConfigurationSection.getInt("g");
        int b = rgbConfigurationSection.getInt("b");
        try {
            ((LeatherArmorMeta)itemMeta).setColor(Color.fromRGB(r, g, b));
        } catch (ClassCastException classCastException) {}
        this.itemStack.setItemMeta(itemMeta);
    }

    public void buildHideAttributes() {
        boolean hideAttributes = this.configurationSection.getBoolean("hide_attributes", true);
        if (hideAttributes) {
            ItemMeta itemMeta = this.itemStack.getItemMeta();
            for (ItemFlag itemFlag : ItemFlag.values()) {
                itemMeta.addItemFlags(new ItemFlag[] { itemFlag });
            }
            this.itemStack.setItemMeta(itemMeta);
        }
    }

    public void buildBannerPatternList() {
        try {
            ConfigurationSection shieldPatternListConfigurationSection = this.configurationSection.getConfigurationSection("banner_pattern_list");
            if (shieldPatternListConfigurationSection == null)
                return;
            ItemMeta itemMeta = this.itemStack.getItemMeta();
            Set<String> stringSet = shieldPatternListConfigurationSection.getKeys(false);
            for (String string : stringSet) {
                ConfigurationSection shieldPatternConfigurationSection = shieldPatternListConfigurationSection.getConfigurationSection(string);
                shieldPatternConfigurationSection.get("color");
                shieldPatternConfigurationSection.get("pattern");
                CraftMetaBanner craftMetaBanner = (CraftMetaBanner)itemMeta;
                DyeColor dyeColor = DyeColor.valueOf(shieldPatternConfigurationSection.getString("color"));
                PatternType patternType = PatternType.valueOf(shieldPatternConfigurationSection.getString("pattern"));
                craftMetaBanner.addPattern(new Pattern(dyeColor, patternType));
            }
            this.itemStack.setItemMeta(itemMeta);
        } catch (Exception exception) {}
    }

    public void buildShieldPatternList() {
        Version version = Version.getServerVersion(Bukkit.getServer());
        if (!version.isNewerOrSameThan(Version.v1_9_R1))
            return;
        try {
            ConfigurationSection shieldPatternListConfigurationSection = this.configurationSection.getConfigurationSection("shield_pattern_list");
            if (shieldPatternListConfigurationSection == null)
                return;
            ItemMeta itemMeta = this.itemStack.getItemMeta();
            Set<String> stringSet = shieldPatternListConfigurationSection.getKeys(false);
            for (String string : stringSet) {
                ConfigurationSection shieldPatternConfigurationSection = shieldPatternListConfigurationSection.getConfigurationSection(string);
                shieldPatternConfigurationSection.get("color");
                shieldPatternConfigurationSection.get("pattern");
                BlockStateMeta blockStateMeta = (BlockStateMeta)itemMeta;
                BlockState blockState = blockStateMeta.getBlockState();
                Banner banner = (Banner)blockState;
                DyeColor dyeColor = DyeColor.valueOf(shieldPatternConfigurationSection.getString("color"));
                PatternType patternType = PatternType.valueOf(shieldPatternConfigurationSection.getString("pattern"));
                banner.addPattern(new Pattern(dyeColor, patternType));
                blockStateMeta.setBlockState(banner);
            }
            this.itemStack.setItemMeta(itemMeta);
        } catch (Exception exception) {}
    }
    public void buildSpawnEgg() {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta instanceof SpawnEggMeta) {
            SpawnEggMeta spawnEggMeta = (SpawnEggMeta) meta;
            if (configurationSection.isSet("eggentitytype")) {
                spawnEggMeta.setSpawnedType(EntityType.valueOf("eggentitytype"));
            }
        }
    }
    public void buildFlags() {
        ItemMeta meta = itemStack.getItemMeta();
        if (configurationSection.isSet("flags")) {
            for (String str : configurationSection.getStringList("flags")) {
                meta.addItemFlags(ItemFlag.valueOf(str));
            }
        }
    }
    public void buildEnchants() {
        ItemMeta meta = itemStack.getItemMeta();
        if (configurationSection.isSet("enchantments")) {
            for (String str : configurationSection.getStringList("enchantments")) {
                Enchantment enchantment = Enchantment.getByName(str.split(";")[0]);
                int level = Integer.parseInt(str.split(";")[1]);
                meta.addEnchant(enchantment, level, true);
            }
        }
    }

    public ItemStack build() {
        buildMaterial();
        buildAmount();
        buildDurabilityData();
        buildDisplayName();
        buildDescription();
        buildUnbreakable();
        buildEnchanted();
        buildRGB();
        buildHideAttributes();
        buildBannerPatternList();
        buildShieldPatternList();
        buildSpawnEgg();
        buildFlags();
        buildEnchants();
        return this.itemStack;
    }
}
