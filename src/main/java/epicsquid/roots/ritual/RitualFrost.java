package epicsquid.roots.ritual;

import epicsquid.roots.init.ModBlocks;
import epicsquid.roots.init.ModItems;
import epicsquid.roots.recipe.conditions.ConditionItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class  RitualFrost extends RitualBase {

  public RitualFrost(String name, int duration) {
    super(name, duration);
    addCondition(new ConditionItems(
        new ItemStack(Items.SNOWBALL),
        new ItemStack(ModItems.dewgonia),
        new ItemStack(Blocks.SNOW),
        new ItemStack(ModItems.bark_spruce),
        new ItemStack(ModItems.bark_spruce)
    ));
  }

  @Override
  public void doEffect(World world, BlockPos pos) {
    for (int i = -19; i < 20; i++) {
      for (int j = -19; j < 20; j++) {
        BlockPos topBlockPos = world.getTopSolidOrLiquidBlock(pos.add(i, 0, j));
        if (world.getBlockState(topBlockPos).getBlock() == Blocks.WATER) {
          world.setBlockState(topBlockPos, Blocks.ICE.getDefaultState());
        } else {
          if (!world.getBlockState(topBlockPos).getBlock().isLeaves(world.getBlockState(topBlockPos), world, topBlockPos)
              && world.getBlockState(topBlockPos.add(0, -1, 0)).getBlock() != ModBlocks.bonfire
              && world.getBlockState(topBlockPos.add(0, -1, 0)).getBlock() != ModBlocks.mortar) {
            world.setBlockState(topBlockPos, Blocks.SNOW_LAYER.getDefaultState());
          }
        }
      }
    }
  }

}
