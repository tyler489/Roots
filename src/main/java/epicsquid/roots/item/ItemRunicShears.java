package epicsquid.roots.item;

import java.util.Random;

import javax.annotation.Nonnull;

import epicsquid.mysticallib.item.ItemBase;
import epicsquid.mysticallib.particle.particles.ParticleGlitter;
import epicsquid.mysticallib.proxy.ClientProxy;
import epicsquid.mysticallib.util.Util;
import epicsquid.mysticalworld.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemRunicShears extends ItemBase {

  private Random random;

  public ItemRunicShears(@Nonnull String name) {
    super(name);
    setMaxDamage(80);
    setMaxStackSize(1);
    setHasSubtypes(false);
    random = new Random();
  }

  @Override
  @Nonnull
  public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    Block block = world.getBlockState(pos).getBlock();
    if (block == Blocks.MOSSY_COBBLESTONE) {

      if (!world.isRemote) {
        world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY() + 1, pos.getZ(), new ItemStack(ModItems.terra_moss)));
        world.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());

        player.getHeldItem(hand).damageItem(1, player);
      } else {
        for (int i = 0; i < 50; i++) {
          ClientProxy.particleRenderer.spawnParticle(world, Util.getLowercaseClassName(ParticleGlitter.class), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, random.nextDouble() * 0.1 * (random.nextDouble() > 0.5 ? -1 : 1), random.nextDouble() * 0.1 * (random.nextDouble() > 0.5 ? -1 : 1),
              random.nextDouble() * 0.1 * (random.nextDouble() > 0.5 ? -1 : 1), 120, 0.855 + random.nextDouble() * 0.05, 0.710, 0.943 - random.nextDouble() * 0.05, 1, random.nextDouble() + 0.5, random.nextDouble() * 2);
        }
      }
    }
    return EnumActionResult.SUCCESS;
  }
}
