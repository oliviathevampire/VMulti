package io.github.cottonmc.vmulti.mixin;

import io.github.cottonmc.vmulti.api.VMultiAPI;
import io.github.cottonmc.vmulti.api.ComponentCollector;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconBlockEntity.class)
public abstract class MixinBeaconBlockEntity extends BlockEntity implements ComponentCollector {
	private Object2IntMap<Block> beaconBlocks = new Object2IntArrayMap<>();

	public MixinBeaconBlockEntity(BlockEntityType<?> type) {
		super(type);
	}

	@Inject(method = "updateLevel", at = @At("HEAD"))
	public void clearBlockMap(int x, int y, int z, CallbackInfo ci) {
		beaconBlocks.clear();
	}

	@ModifyVariable(method = "updateLevel", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;"))
	public Block getNewBlock(Block original) {
		if (VMultiAPI.BEACON_BASES.contains(original)) {
			int currentCount = beaconBlocks.getOrDefault(original, 0);
			beaconBlocks.put(original, currentCount + 1);
			return Blocks.IRON_BLOCK;
		}
		return Blocks.AIR;
	}

	@Override
	public Object2IntMap<Block> getActivatingBlocks() {
		return beaconBlocks;
	}
}
