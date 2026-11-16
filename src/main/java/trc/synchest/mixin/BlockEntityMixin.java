package trc.synchest.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

import trc.synchest.TRCAddon;
import trc.synchest.SyncedChests;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {
	@Inject(method = "preRemoveSideEffects", at = @At("HEAD"), cancellable = true)
	private void synchest$onPreRemove(BlockPos pos, BlockState state, CallbackInfo ci) {
		if (!TRCAddon.SYNC_ENABLED.value) return;

		BlockEntity self = (BlockEntity) (Object) this;
		if (!(self instanceof ChestBlockEntity chest) || self.getType() != BlockEntityTypes.CHEST) return;
		Level level = self.getLevel();
		if (level == null || level.isClientSide()) return;
		if (!(state.getBlock() instanceof ChestBlock)) return;

		ChestType type = state.getValue(ChestBlock.TYPE);
		if (type == ChestType.SINGLE) {
			if (SyncedChests.isSyncedChest(self, state)) ci.cancel();
			return;
		}

		BlockPos otherPos = ChestBlock.getConnectedBlockPos(pos, state);
		BlockEntity other = level.getBlockEntity(otherPos);
		ChestBlockEntity otherChest =
				(other instanceof ChestBlockEntity && other.getType() == BlockEntityTypes.CHEST)
						? (ChestBlockEntity) other : null;

		boolean involvesNamedChest = chest.getCustomName() != null || (otherChest != null && otherChest.getCustomName() != null);
		if (!involvesNamedChest) return;

		if (otherChest != null) {
			Containers.dropContents(level, pos, (Container) otherChest);
			otherChest.setChanged();
		}
	}
}