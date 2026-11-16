package trc.synchest.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;

import trc.synchest.SyncedChests;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.jetbrains.annotations.NotNull;

@Mixin(BaseContainerBlockEntity.class)
public abstract class BaseContainerBlockEntityMixin {
	@Shadow
	protected abstract NonNullList<@NotNull ItemStack> getItems();

	@Redirect(
			method = "applyImplicitComponents",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/entity/BaseContainerBlockEntity;getItems()Lnet/minecraft/core/NonNullList;"))
	private NonNullList<@NotNull ItemStack> synchest$dontOverwriteGroupOnPlace(BaseContainerBlockEntity self) {
		if (SyncedChests.isSyncedChest(self)) return NonNullList.withSize(SyncedChests.CHEST_SIZE, ItemStack.EMPTY);
		return this.getItems();
	}

	@Inject(method = { "setItem", "clearContent" }, at = @At("TAIL"))
	private void synchest$markDirtyVoid(CallbackInfo ci) {
		BaseContainerBlockEntity self = (BaseContainerBlockEntity) (Object) this;
		if (SyncedChests.isSyncedChest(self)) SyncedChests.markDirty(self);
	}

	@Inject(method = { "removeItem", "removeItemNoUpdate" }, at = @At("TAIL"))
	private void synchest$markDirtyReturning(CallbackInfoReturnable<ItemStack> cir) {
		BaseContainerBlockEntity self = (BaseContainerBlockEntity) (Object) this;
		if (SyncedChests.isSyncedChest(self)) SyncedChests.markDirty(self);
	}
}