package epicsquid.roots.recipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import epicsquid.roots.api.Herb;
import epicsquid.roots.init.HerbRegistry;
import epicsquid.roots.init.ModItems;
import epicsquid.roots.item.ItemPouch;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class PowderPouchFillRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

  @Override
  public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {
    int pestleCount = 0;
    int herbCount = 0;
    int pouchCount = 0;
    String plantName = "";
    String pouchPlant = "";
    for (int i = 0; i < inv.getSizeInventory(); i++) {
      if (inv.getStackInSlot(i) != ItemStack.EMPTY) {
        Herb herb = getHerb(inv.getStackInSlot(i));
        if (herb != null) {
          herbCount++;
          plantName = herb.getName();
        } else if (inv.getStackInSlot(i).getItem() instanceof ItemPouch) {
          pouchCount++;
          if (inv.getStackInSlot(i).hasTagCompound()) {
            if (inv.getStackInSlot(i).getTagCompound().hasKey("plant")) {
              pouchPlant = inv.getStackInSlot(i).getTagCompound().getString("plant");
            }
          }
        } else if (inv.getStackInSlot(i).getItem() == ModItems.pestle) {
          pestleCount++;
        } else {
          return false;
        }
      }
    }
    return pestleCount == 1 && herbCount == 1 && pouchCount == 1 && (pouchPlant.compareTo(plantName) == 0 || pouchPlant.compareTo("") == 0);
  }

  @Override
  @Nonnull
  public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
    String plantName = "";
    int plantStack = 0;
    for (int i = 0; i < inv.getSizeInventory(); i++) {
      if (inv.getStackInSlot(i) != ItemStack.EMPTY) {
        Herb herb = getHerb(inv.getStackInSlot(i));
        if (herb != null) {
          plantName = herb.getName();
          plantStack = inv.getStackInSlot(i).getCount();
        }
      }
    }
    for (int i = 0; i < inv.getSizeInventory(); i++) {
      if (inv.getStackInSlot(i) != ItemStack.EMPTY) {
        if (inv.getStackInSlot(i).getItem() instanceof ItemPouch) {
          ItemStack result = inv.getStackInSlot(i).copy();
          if (!result.hasTagCompound()) {
            ItemPouch.createData(result, plantName, plantStack);
          } else {
            ItemPouch.setQuantity(result, plantName, ItemPouch.getQuantity(result, plantName) + plantStack);
          }
          return result;
        }
      }
    }
    return ItemStack.EMPTY;
  }

  @Override
  @Nonnull
  public ItemStack getRecipeOutput() {
    return new ItemStack(ModItems.pouch, 1);
  }

  @Override
  @Nonnull
  public NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inv) {
    NonNullList<ItemStack> remaining = NonNullList.create();
    for (int i = 0; i < inv.getSizeInventory(); i++) {
      if (inv.getStackInSlot(i) != ItemStack.EMPTY) {
        if (inv.getStackInSlot(i).getItem() == ModItems.pestle) {
          remaining.add(inv.getStackInSlot(i).copy());
        } else {
          remaining.add(ItemStack.EMPTY);
        }
      }
    }
    inv.clear();
    return remaining;
  }

  @Nullable
  private Herb getHerb(@Nonnull ItemStack stack) {
    for (Herb herb : HerbRegistry.REGISTRY.getValuesCollection()) {
      if (stack.getItem() == herb.getItem()) {
        return herb;
      }
    }
    return null;
  }

  @Override
  public boolean canFit(int width, int height) {
    return width * height > 2;
  }
}
