package trc.synchest;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import com.mojang.serialization.Codec;

import org.jetbrains.annotations.NotNull;

public class GlobalChestStorage extends SavedData {
	private static final Codec<GlobalChestStorage> CODEC = Codec
			.unboundedMap(Codec.STRING, ItemContainerContents.CODEC)
			.xmap(GlobalChestStorage::fromPacked, GlobalChestStorage::toPacked);

	public static final SavedDataType<@NotNull GlobalChestStorage> TYPE = new SavedDataType<>(
			Identifier.fromNamespaceAndPath(TRCAddon.MOD_ID, "global_chests"),
			GlobalChestStorage::new,
			CODEC,
			DataFixTypes.LEVEL);

	private final Map<String, NonNullList<@NotNull ItemStack>> groups = new HashMap<>();

	public GlobalChestStorage() {
	}

	private static @NotNull GlobalChestStorage fromPacked(@NotNull Map<String, ItemContainerContents> packed) {
		GlobalChestStorage storage = new GlobalChestStorage();
		packed.forEach((key, contents) -> {
			NonNullList<@NotNull ItemStack> list = NonNullList.withSize(SyncedChests.CHEST_SIZE, ItemStack.EMPTY);
			contents.copyInto(list);
			storage.groups.put(key, list);
		});
		return storage;
	}

	private static @NotNull Map<String, ItemContainerContents> toPacked(@NotNull GlobalChestStorage storage) {
		Map<String, ItemContainerContents> packed = new HashMap<>();
		storage.groups.forEach((key, list) -> packed.put(key, ItemContainerContents.fromItems(list)));
		return packed;
	}

	public NonNullList<@NotNull ItemStack> group(String key) {
		return this.groups.computeIfAbsent(key, k -> NonNullList.withSize(SyncedChests.CHEST_SIZE, ItemStack.EMPTY));
	}

	public static GlobalChestStorage get(@NotNull MinecraftServer server) {
		return server.getDataStorage().computeIfAbsent(TYPE);
	}
}