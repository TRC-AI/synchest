package trc.synchest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;

import trc.synchest.mixin.BaseContainerBlockEntityAccessor;

public final class NameTagNaming {
    private NameTagNaming() {}

    public static void register() {
        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (!TRCAddon.SYNC_ENABLED.value) return InteractionResult.PASS;

            ItemStack stack = player.getItemInHand(hand);
            if (!stack.is(Items.NAME_TAG)) return InteractionResult.PASS;

            Component tagName = stack.get(DataComponents.CUSTOM_NAME);
            if (tagName == null) return InteractionResult.PASS;
            if (!player.isShiftKeyDown()) return InteractionResult.PASS;

            BlockPos pos = hit.getBlockPos();
            BlockState state = level.getBlockState(pos);
            if (!state.is(Blocks.CHEST)) return InteractionResult.PASS;
            if (state.getValue(ChestBlock.TYPE) != ChestType.SINGLE) return InteractionResult.PASS;
            if (level.isClientSide()) return InteractionResult.PASS;

            BlockEntity be = level.getBlockEntity(pos);
            if (!(be instanceof ChestBlockEntity chest) || be.getType() != BlockEntityTypes.CHEST) return InteractionResult.PASS;
            if (!chest.isEmpty()) return InteractionResult.PASS;

            if (chest.getCustomName() != null && chest.getCustomName().equals(tagName)) return InteractionResult.PASS;

            ((BaseContainerBlockEntityAccessor) chest).synchest$setName(tagName.copy());
            chest.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);

            if (!player.getAbilities().instabuild) stack.shrink(1);
            level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.7F, 1.2F);
            return InteractionResult.SUCCESS;
        });
    }
}