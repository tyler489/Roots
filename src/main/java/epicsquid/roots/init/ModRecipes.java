package epicsquid.roots.init;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import epicsquid.mysticallib.block.BlockBase;
import epicsquid.mysticallib.event.RegisterModRecipesEvent;
import epicsquid.roots.Roots;
import epicsquid.roots.api.Herb;
import epicsquid.roots.recipe.MortarRecipe;
import epicsquid.roots.recipe.PowderPouchFillRecipe;
import epicsquid.roots.recipe.PyreCraftingRecipe;
import epicsquid.roots.recipe.RunicCarvingRecipe;
import epicsquid.roots.recipe.RunicShearRecipe;
import epicsquid.roots.recipe.recipes.RunicShearRecipes;
import epicsquid.roots.spell.SpellBase;
import epicsquid.roots.spell.SpellRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

public class ModRecipes {

  private static ArrayList<MortarRecipe> mortarRecipes = new ArrayList<>();
  private static Map<String, PyreCraftingRecipe> pyreCraftingRecipes = new HashMap<>();
  private static List<RunicShearRecipe> runicShearRecipes = new ArrayList<>();
  private static List<RunicCarvingRecipe> runicCarvingRecipes = new ArrayList<>();

  private static ResourceLocation getRL(@Nonnull String s) {
    return new ResourceLocation(Roots.MODID + ":" + s);
  }

  private static void registerShapeless(@Nonnull IForgeRegistry<IRecipe> registry, @Nonnull String name, @Nonnull ItemStack result, Object... ingredients) {
    registry.register(new ShapelessOreRecipe(getRL(name), result, ingredients).setRegistryName(getRL(name)));
  }

  private static void registerShaped(@Nonnull IForgeRegistry<IRecipe> registry, @Nonnull String name, @Nonnull ItemStack result, Object... ingredients) {
    registry.register(new ShapedOreRecipe(getRL(name), result, ingredients).setRegistryName(getRL(name)));
  }

  public static void addRunicCarvingRecipe(RunicCarvingRecipe recipe) {
    for (RunicCarvingRecipe runicCarvingRecipe : runicCarvingRecipes) {
      if (runicCarvingRecipe.matches(recipe)) {
        System.out.println("Recipe is already registered with carving block - " + recipe.getCarvingBlock() + ", rune block - " + recipe.getRuneBlock() + ", herb - " + recipe.getHerb().getItem());
        return;
      }
    }
    runicCarvingRecipes.add(recipe);
  }

  public static RunicCarvingRecipe getRunicCarvingRecipe(IBlockState carvingBlock, Herb herb) {
    if (carvingBlock != null && herb != null) {
      for (RunicCarvingRecipe recipe : runicCarvingRecipes) {
        if (recipe.getHerb().equals(herb) && recipe.getCarvingBlock().equals(carvingBlock)) {
          return recipe;
        }
      }
    }
    return null;
  }

  public static RunicCarvingRecipe getRunicCarvingRecipe(String name) {
    if (name != null) {
      for (RunicCarvingRecipe recipe : runicCarvingRecipes) {
        if (recipe.getName().equals(name)) {
          return recipe;
        }
      }
    }
    return null;
  }

  public static void addRunicShearRecipe(RunicShearRecipe recipe) {
    for (RunicShearRecipe runicShearRecipe : runicShearRecipes) {
      if (recipe.isBlockRecipe() && recipe.getBlock() == runicShearRecipe.getBlock()) {
        System.out.println("Recipe is already registered with block - " + recipe.getBlock().getUnlocalizedName());
        return;
      } else if (recipe.isEntityRecipe() && recipe.getEntity() == runicShearRecipe.getEntity()) {
        System.out.println("Recipe is already registered with entity - " + recipe.getEntity().getName());
        return;
      }
    }
    runicShearRecipes.add(recipe);
  }

  public static RunicShearRecipe getRunicShearRecipe(Block block) {
    for (RunicShearRecipe recipe : runicShearRecipes) {
      if (recipe.isBlockRecipe() && recipe.getBlock() == block) {
        return recipe;
      }
    }
    return null;
  }

  public static RunicShearRecipe getRunicShearRecipe(EntityLiving entity) {
    for (RunicShearRecipe recipe : runicShearRecipes) {
      if (recipe.isEntityRecipe() && recipe.getEntity() == entity) {
        return recipe;
      }
    }
    return null;
  }

  public static RunicShearRecipe getRunicShearRecipe(String name) {
    for (RunicShearRecipe recipe : runicShearRecipes) {
      if (recipe.getName().equals(name)) {
        return recipe;
      }
    }
    return null;
  }

  public static MortarRecipe getMortarRecipe(List<ItemStack> items) {
    for (MortarRecipe mortarRecipe : mortarRecipes) {
      if (mortarRecipe.matches(items)) {
        return mortarRecipe;
      }
    }
    return null;
  }

  private static void addMortarRecipe(MortarRecipe recipe) {
    for (MortarRecipe mortarRecipe : mortarRecipes) {
      if (mortarRecipe.matches(recipe.getIngredients())) {
        System.out.println("Recipe is already registered with output - " + recipe.getResult().getItem().getUnlocalizedName());
        return;
      }
    }

    mortarRecipes.add(recipe);
  }

  public static SpellBase getSpellRecipe(List<ItemStack> items) {
    for (SpellBase spell : SpellRegistry.spellRegistry.values()) {
      if (spell.matchesIngredients(items)) {
        return spell;
      }
    }
    return null;
  }

  public static PyreCraftingRecipe getCraftingRecipe(List<ItemStack> items) {
    List<ItemStack> stacksToRemove = new ArrayList<>();
    for(ItemStack s : items){
      if(s == ItemStack.EMPTY){
        stacksToRemove.add(s);
      }
    }
    for(ItemStack s : stacksToRemove){
      items.remove(s);
    }
    stacksToRemove.clear();
    for (Map.Entry<String, PyreCraftingRecipe> pyreCraftingRecipe : pyreCraftingRecipes.entrySet()) {
      if (pyreCraftingRecipe.getValue().matches(items)) {
        return pyreCraftingRecipe.getValue();
      }
    }
    return null;
  }

  public static PyreCraftingRecipe getCraftingRecipe(String recipeName) {
    return pyreCraftingRecipes.get(recipeName);
  }

  private static void addCraftingRecipe(String recipeName, PyreCraftingRecipe pyreCraftingRecipe) {
    for (Map.Entry<String, PyreCraftingRecipe> pyreCraftingRecipeEntry : pyreCraftingRecipes.entrySet()) {
      if (pyreCraftingRecipeEntry.getValue().matches(pyreCraftingRecipe.getIngredients())) {
        System.out.println("A Crafting Recipe is already registered");
        return;
      }
    }

    pyreCraftingRecipes.put(recipeName, pyreCraftingRecipe);
  }

  /**
   * Register all recipes
   */
  public static void initRecipes(@Nonnull RegisterModRecipesEvent event) {
    initDrops();
    addMortarRecipe(new MortarRecipe(new ItemStack(Items.DYE, 1, 12), new ItemStack[] { new ItemStack(epicsquid.mysticalworld.init.ModItems.carapace) }, 1, 1, 1, 1, 1, 1));

    initCraftingRecipes();
    RunicShearRecipes.initRecipes();

    event.getRegistry().register(new PowderPouchFillRecipe().setRegistryName(getRL("powder_pouch_fill")));

  }

  private static void initCraftingRecipes() {

    addCraftingRecipe("unending_bowl",
        new PyreCraftingRecipe(new ItemStack(ItemBlock.getItemFromBlock(ModBlocks.unending_bowl))).addIngredients(
            new ItemStack(Items.WATER_BUCKET),
            new ItemStack(ItemBlock.getItemFromBlock(ModBlocks.mortar)),
            new ItemStack(ModItems.moonglow_leaf),
            new ItemStack(ModItems.terra_moss),
            new ItemStack(ModItems.spirit_herb)));

    addCraftingRecipe("runic_soil",
        new PyreCraftingRecipe(new ItemStack(ModBlocks.runic_soil)).addIngredients(
          new ItemStack(Blocks.DIRT),
          new ItemStack(ModItems.terra_moss),
          new ItemStack(ModItems.wildroot),
          new ItemStack(Blocks.GRAVEL),
          new ItemStack(Items.DYE, 1, 15)));

    addCraftingRecipe("living_pickaxe",
        new PyreCraftingRecipe(new ItemStack(ModItems.living_pickaxe)).addIngredients(
          new ItemStack(Items.GOLD_INGOT),
          new ItemStack(Items.WOODEN_PICKAXE),
          new ItemStack(ModItems.wildroot),
          new ItemStack(ModItems.bark_oak),
          new ItemStack(ModItems.bark_oak)));

    addCraftingRecipe("living_axe",
        new PyreCraftingRecipe(new ItemStack(ModItems.living_axe)).addIngredients(
          new ItemStack(Items.GOLD_INGOT),
          new ItemStack(Items.WOODEN_AXE),
          new ItemStack(ModItems.wildroot),
          new ItemStack(ModItems.bark_oak),
          new ItemStack(ModItems.bark_oak)));

    addCraftingRecipe("living_shovel",
        new PyreCraftingRecipe(new ItemStack(ModItems.living_shovel)).addIngredients(
            new ItemStack(Items.GOLD_INGOT),
            new ItemStack(Items.WOODEN_SHOVEL),
            new ItemStack(ModItems.wildroot),
            new ItemStack(ModItems.bark_oak),
            new ItemStack(ModItems.bark_oak)));

    addCraftingRecipe("living_hoe",
        new PyreCraftingRecipe(new ItemStack(ModItems.living_hoe)).addIngredients(
          new ItemStack(Items.GOLD_INGOT),
          new ItemStack(Items.WOODEN_HOE),
          new ItemStack(ModItems.wildroot),
          new ItemStack(ModItems.bark_oak),
          new ItemStack(ModItems.bark_oak)));

    addCraftingRecipe("living_sword",
        new PyreCraftingRecipe(new ItemStack(ModItems.living_sword)).addIngredients(
            new ItemStack(Items.GOLD_INGOT),
            new ItemStack(Items.WOODEN_SWORD),
            new ItemStack(ModItems.wildroot),
            new ItemStack(ModItems.bark_oak),
            new ItemStack(ModItems.bark_oak)));

    addCraftingRecipe("infernal_bulb",
        new PyreCraftingRecipe(new ItemStack(ModItems.infernal_bulb, 3)).addIngredients(
            new ItemStack(Items.NETHER_WART),
            new ItemStack(ItemBlock.getItemFromBlock(Blocks.MAGMA)),
            new ItemStack(Items.BLAZE_ROD),
            new ItemStack(ModItems.wildroot),
            new ItemStack(Items.GUNPOWDER)));

    addCraftingRecipe("dewgonia",
        new PyreCraftingRecipe(new ItemStack(ModItems.dewgonia, 3)).addIngredients(
            new ItemStack(Item.getItemFromBlock(Blocks.TALLGRASS), 1, 1),
            new ItemStack(Items.SUGAR),
            new ItemStack(Items.DYE, 1, 4),
            new ItemStack(ModItems.terra_moss),
            new ItemStack(Item.getItemFromBlock(Blocks.WATERLILY))));

    addCraftingRecipe("cloud_berry",
        new PyreCraftingRecipe(new ItemStack(ModItems.cloud_berry, 3)).addIngredients(
            new ItemStack(ItemBlock.getItemFromBlock(Blocks.LEAVES)),
            new ItemStack(Item.getItemFromBlock(Blocks.TALLGRASS), 1, 1),
            new ItemStack(ItemBlock.getItemFromBlock(Blocks.WOOL)),
            new ItemStack(ModItems.terra_moss),
            new ItemStack(ModItems.terra_moss)));

    addCraftingRecipe("stalicripe",
        new PyreCraftingRecipe(new ItemStack(ModItems.stalicripe, 3)).addIngredients(
            new ItemStack(Items.FLINT),
            new ItemStack(Blocks.STONE),
            new ItemStack(Items.IRON_INGOT),
            new ItemStack(ModItems.wildroot),
            new ItemStack(Items.QUARTZ)));

    addCraftingRecipe("moonglow_leaf",
        new PyreCraftingRecipe(new ItemStack(ModItems.moonglow_leaf, 3)).addIngredients(
            new ItemStack(Blocks.LEAVES),
            new ItemStack(Blocks.GLASS),
            new ItemStack(Items.QUARTZ),
            new ItemStack(ModItems.bark_birch),
            new ItemStack(ModItems.bark_birch)));

    addCraftingRecipe("pereskia",
        new PyreCraftingRecipe(new ItemStack(ModItems.pereskia, 3)).addIngredients(
            new ItemStack(ModItems.wildroot),
            new ItemStack(Items.SPECKLED_MELON),
            new ItemStack(Items.REDSTONE),
            new ItemStack(Items.GLOWSTONE_DUST),
            new ItemStack(Items.REEDS)));

    addCraftingRecipe("baffle_cap",
        new PyreCraftingRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.baffle_cap_mushroom), 3)).addIngredients(
            new ItemStack(ModItems.terra_moss),
            new ItemStack(Items.POISONOUS_POTATO),
            new ItemStack(Items.WHEAT_SEEDS),
            new ItemStack(Blocks.RED_MUSHROOM),
            new ItemStack(Blocks.BROWN_MUSHROOM)));

    addCraftingRecipe("runic_shears",
        new PyreCraftingRecipe(new ItemStack(epicsquid.roots.init.ModItems.runic_shears)).addIngredients(
          new ItemStack(Items.SHEARS),
          new ItemStack(ModItems.pereskia),
          new ItemStack(ModItems.pereskia),
          new ItemStack(ModBlocks.runestone),
          new ItemStack(ModBlocks.runestone)));

    addCraftingRecipe("rune_stone",
        new PyreCraftingRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.runestone), 8)).addIngredients(
            new ItemStack(Items.DYE, 1, 4),
            new ItemStack(Blocks.STONE),
            new ItemStack(Blocks.STONE),
            new ItemStack(Blocks.STONE),
            new ItemStack(Blocks.STONE)));

    addCraftingRecipe("wildwood_helmet", new PyreCraftingRecipe(new ItemStack(ModItems.wildwood_helmet)).addIngredients(
            new ItemStack(Items.IRON_HELMET), new ItemStack(ModItems.bark_oak),
            new ItemStack(ModItems.bark_oak),
            new ItemStack(Blocks.PLANKS, 1, 0),
            new ItemStack(Items.DIAMOND)));

    addCraftingRecipe("wildwood_chestplate", new PyreCraftingRecipe(new ItemStack(ModItems.wildwood_chestplate)).addIngredients(
            new ItemStack(Items.IRON_CHESTPLATE),
            new ItemStack(ModItems.bark_oak),
            new ItemStack(ModItems.bark_oak),
            new ItemStack(Blocks.PLANKS, 1, 0),
            new ItemStack(Items.DIAMOND)));
    addCraftingRecipe("wildwood_leggings", new PyreCraftingRecipe(new ItemStack(ModItems.wildwood_leggings)).addIngredients(
            new ItemStack(Items.IRON_LEGGINGS),
            new ItemStack(ModItems.bark_oak),
            new ItemStack(ModItems.bark_oak),
            new ItemStack(Blocks.PLANKS, 1, 0),
            new ItemStack(Items.DIAMOND)));
    addCraftingRecipe("wildwood_boots", new PyreCraftingRecipe(new ItemStack(ModItems.wildwood_boots)).addIngredients(
            new ItemStack(Items.IRON_BOOTS),
            new ItemStack(ModItems.bark_oak),
            new ItemStack(ModItems.bark_oak),
            new ItemStack(Blocks.PLANKS, 1, 0),
            new ItemStack(Items.DIAMOND)));
  }

  public static void afterHerbRegisterInit(){
    //Sylvan Armor Recipe Registration
    for (Herb herb : HerbRegistry.REGISTRY)
    {
      NBTTagCompound nbtData = new NBTTagCompound();
      nbtData.setString("herb", herb.getName());

      ItemStack helmet = new ItemStack(ModItems.sylvan_helmet);
      ItemStack chestplate = new ItemStack(ModItems.sylvan_chestplate);
      ItemStack leggings = new ItemStack(ModItems.sylvan_leggings);
      ItemStack boots = new ItemStack(ModItems.sylvan_boots);

      helmet.setTagCompound(nbtData);
      chestplate.setTagCompound(nbtData);
      leggings.setTagCompound(nbtData);
      boots.setTagCompound(nbtData);

      System.out.println(herb.getItem().getUnlocalizedName());

      addCraftingRecipe("sylvan_helmet_" + herb.getName(), new PyreCraftingRecipe(helmet).addIngredients(
              new ItemStack(Items.LEATHER_HELMET),
              new ItemStack(Blocks.VINE),
              new ItemStack(ModItems.bark_birch),
              new ItemStack(Items.DIAMOND),
              new ItemStack(herb.getItem())));
      addCraftingRecipe("sylvan_chestplate_" + herb.getName(), new PyreCraftingRecipe(chestplate).addIngredients(
              new ItemStack(Items.LEATHER_CHESTPLATE),
              new ItemStack(Blocks.VINE),
              new ItemStack(ModItems.bark_birch),
              new ItemStack(Items.DIAMOND),
              new ItemStack(herb.getItem())));
      addCraftingRecipe("sylvan_leggings_" + herb.getName(), new PyreCraftingRecipe(leggings).addIngredients(
              new ItemStack(Items.LEATHER_LEGGINGS),
              new ItemStack(Blocks.VINE),
              new ItemStack(ModItems.bark_birch),
              new ItemStack(Items.DIAMOND),
              new ItemStack(herb.getItem())));
      addCraftingRecipe("sylvan_boots_" + herb.getName(), new PyreCraftingRecipe(boots).addIngredients(
              new ItemStack(Items.LEATHER_BOOTS),
              new ItemStack(Blocks.VINE),
              new ItemStack(ModItems.bark_birch),
              new ItemStack(Items.DIAMOND),
              new ItemStack(herb.getItem())));
    }
  }

  private static void initDrops() {
    List<ItemStack> baffleCapDrops = new ArrayList<>();
    baffleCapDrops.add(new ItemStack(Item.getItemFromBlock(ModBlocks.baffle_cap_mushroom)));
    ((BlockBase) ModBlocks.baffle_cap_huge_top).setDrops(baffleCapDrops);
  }

}
