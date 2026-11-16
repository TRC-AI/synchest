package trc.synchest.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

import trc.synchest.SyncedChests;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.jetbrains.annotations.NotNull;

@Mixin(ChestBlockEntity.class)
public abstract class ChestBlockEntityMixin {
	@Unique
	private Component synchest$cachedName;
	@Unique
	private NonNullList<@NotNull ItemStack> synchest$cachedGroup;

	@Inject(method = "getItems", at = @At("HEAD"), cancellable = true)
	private void synchest$redirectToGroup(CallbackInfoReturnable<NonNullList<@NotNull ItemStack>> cir) {
		ChestBlockEntity self = (ChestBlockEntity) (Object) this;
		if (!SyncedChests.isSyncedChest(self)) return;

		Level level = self.getLevel();
		if (level == null) return;

		Component name = self.getCustomName();
		if (name == null) return;

		if (this.synchest$cachedGroup != null && name.equals(this.synchest$cachedName)) {
			cir.setReturnValue(this.synchest$cachedGroup);
			return;
		}

		NonNullList<@NotNull ItemStack> group = SyncedChests.group(name, level);
		if (group == null) return;

		this.synchest$cachedName = name;
		this.synchest$cachedGroup = group;
		cir.setReturnValue(group);
	}
}