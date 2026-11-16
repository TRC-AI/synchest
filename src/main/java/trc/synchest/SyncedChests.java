package trc.synchest;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SyncedChests {
    public static final int CHEST_SIZE = 27;

    private SyncedChests() {}

    public static boolean isSyncedChest(BlockEntity be) { return isSyncedChest(be, be.getBlockState()); }

    public static boolean isSyncedChest(BlockEntity be, @NotNull BlockState state) {
        if (!TRCAddon.SYNC_ENABLED.value) return false;
        if (!(be instanceof ChestBlockEntity chest)) return false;
        if (be.getType() != BlockEntityTypes.CHEST) return false;
        if (!(state.getBlock() instanceof ChestBlock)) return false;
        if (state.getValue(ChestBlock.TYPE) != ChestType.SINGLE) return false;
        return chest.getCustomName() != null;
    }

    public static @NotNull String groupKey(Component name, Level level) {
        try {
            RegistryOps<@NotNull Tag> ops = level.registryAccess().createSerializationContext(NbtOps.INSTANCE);
            Tag tag = ComponentSerialization.CODEC.encodeStart(ops, name).getOrThrow();
            return tag.toString();
        } catch (Exception e) {
            return "raw:" + name.getString();
        }
    }

    public static @Nullable NonNullList<@NotNull ItemStack> group(Component name, @NotNull Level level) {
        if (level.isClientSide()) return null;
        MinecraftServer server = level.getServer();
        if (server == null) return null;
        return GlobalChestStorage.get(server).group(groupKey(name, level));
    }

    public static void markDirty(@NotNull BlockEntity be) {
        Level level = be.getLevel();
        if (level == null || level.isClientSide()) return;
        MinecraftServer server = level.getServer();
        if (server != null) GlobalChestStorage.get(server).setDirty();
    }
}