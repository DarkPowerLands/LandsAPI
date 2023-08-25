package ru.landsproject.api.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Serialization {

    public static String serialize(Object object) {
        return new Gson().toJson(object);
    }
    public static Object deserialize(String json) {
        return new Gson().fromJson(json, new TypeToken<>() {}.getType());
    }
    public static ItemStack deserializeItemStack(byte[] serializedObject) {
        try (ByteArrayInputStream io = new ByteArrayInputStream(serializedObject);
             BukkitObjectInputStream os = new BukkitObjectInputStream(io)) {
            return (ItemStack) os.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] serializeItemStack(ItemStack item) {
        try (ByteArrayOutputStream io = new ByteArrayOutputStream();
             BukkitObjectOutputStream os = new BukkitObjectOutputStream(io)) {
            os.writeObject(item);
            os.flush();
            return io.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
