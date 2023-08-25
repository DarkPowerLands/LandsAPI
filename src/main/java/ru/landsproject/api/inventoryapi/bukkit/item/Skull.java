package ru.landsproject.api.inventoryapi.bukkit.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.JSONException;
import org.json.JSONObject;
import ru.landsproject.api.util.Reflection;

import java.util.Base64;
import java.util.UUID;

public enum Skull {
    ARROW_LEFT("MHF_ArrowLeft"),
    ARROW_RIGHT("MHF_ArrowRight"),
    ARROW_UP("MHF_ArrowUp"),
    ARROW_DOWN("MHF_ArrowDown"),
    QUESTION("MHF_Question"),
    EXCLAMATION("MHF_Exclamation"),
    CAMERA("FHG_Cam"),
    ZOMBIE_PIGMAN("MHF_PigZombie"),
    PIG("MHF_Pig"),
    SHEEP("MHF_Sheep"),
    BLAZE("MHF_Blaze"),
    CHICKEN("MHF_Chicken"),
    COW("MHF_Cow"),
    SLIME("MHF_Slime"),
    SPIDER("MHF_Spider"),
    SQUID("MHF_Squid"),
    VILLAGER("MHF_Villager"),
    OCELOT("MHF_Ocelot"),
    HEROBRINE("MHF_Herobrine"),
    LAVA_SLIME("MHF_LavaSlime"),
    MOOSHROOM("MHF_MushroomCow"),
    GOLEM("MHF_Golem"),
    GHAST("MHF_Ghast"),
    ENDERMAN("MHF_Enderman"),
    CAVE_SPIDER("MHF_CaveSpider"),
    CACTUS("MHF_Cactus"),
    CAKE("MHF_Cake"),
    CHEST("MHF_Chest"),
    MELON("MHF_Melon"),
    LOG("MHF_OakLog"),
    PUMPKIN("MHF_Pumpkin"),
    TNT("MHF_TNT"),
    DYNAMITE("MHF_TNT2");

    private String id;

    Skull(String id) {
        this.id = id;
    }

    public static ItemStack getCustomSkullByValue(String value) throws JSONException {
        JSONObject jsonObject = new JSONObject(new String(Base64.getDecoder().decode(value)));
        return getCustomSkullByUrl(jsonObject.getJSONObject("textures").getJSONObject("SKIN").getString("url"));
    }

    public static ItemStack getCustomSkullByUrl(String url) {
        UUID uuid = UUID.fromString((new StringBuilder(url.split("/")[(url.split("/")).length - 1].substring(32)))
                .insert(20, '-')
                .insert(16, '-')
                .insert(12, '-')
                .insert(8, '-')
                .toString());
        GameProfile profile = new GameProfile(uuid, null);
        PropertyMap propertyMap = profile.getProperties();
        if (propertyMap == null)
            throw new IllegalStateException("Profile doesn't contain a property map");
        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", new Object[] { url }).getBytes());
        propertyMap.put("textures", new Property("textures", new String(encodedData)));
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1, (short)3);
        ItemMeta headMeta = head.getItemMeta();
        Class<?> headMetaClass = headMeta.getClass();
        Reflection.<GameProfile>getField(headMetaClass, "profile", GameProfile.class).set(headMeta, profile);
        head.setItemMeta(headMeta);
        return head;
    }

    public static ItemStack getCustomSkullByPlayerName(String playerName) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD, 1, (short)3);
        SkullMeta meta = (SkullMeta)itemStack.getItemMeta();
        meta.setOwner(playerName);
        itemStack.setItemMeta((ItemMeta)meta);
        return itemStack;
    }

    public String getId() {
        return this.id;
    }

    public ItemStack getSkull() {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD, 1, (short)3);
        SkullMeta meta = (SkullMeta)itemStack.getItemMeta();
        meta.setOwner(this.id);
        itemStack.setItemMeta((ItemMeta)meta);
        return itemStack;
    }
}
