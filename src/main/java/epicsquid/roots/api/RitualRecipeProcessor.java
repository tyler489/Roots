package epicsquid.roots.api;

import java.util.ArrayList;
import java.util.List;

import epicsquid.roots.ritual.RitualBase;
import epicsquid.roots.ritual.RitualRegistry;
import net.minecraft.item.ItemStack;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.ItemStackUtil;

public class RitualRecipeProcessor implements IComponentProcessor {

  private List<ItemStack> ingredients = new ArrayList<>();

  @Override
  public void setup(IVariableProvider<String> iVariableProvider) {
    String ritualName = iVariableProvider.get("ritual");
    RitualBase ritualBase = RitualRegistry.ritualRegistry.get(ritualName);
    ingredients = ritualBase.getRecipe();
  }

  @Override
  public String process(String s) {
    if(s.startsWith("item")) {
      int index = Integer.parseInt(s.substring(4)) - 1;
      ItemStack ingredient = ingredients.get(index);

      return ItemStackUtil.serializeStack(ingredient);
    }
    return null;
  }

}
