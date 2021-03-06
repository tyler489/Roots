package epicsquid.roots.spell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import epicsquid.mysticallib.util.ListUtil;
import epicsquid.roots.api.Herb;
import epicsquid.roots.util.PowderInventoryUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public abstract class SpellBase {
  private float red1, green1, blue1;
  private float red2, green2, blue2;
  private String name;
  protected int cooldown = 20;

  private TextFormatting textColor;
  protected EnumCastType castType = EnumCastType.INSTANTANEOUS;
  private Map<Herb, Double> costs = new HashMap<>();
  private List<ItemStack> ingredients = new ArrayList<>();

  public enum EnumCastType {
    INSTANTANEOUS, CONTINUOUS
  }

  public SpellBase(String name, TextFormatting textColor, float r1, float g1, float b1, float r2, float g2, float b2) {
    this.name = name;
    this.red1 = r1;
    this.green1 = g1;
    this.blue1 = b1;
    this.red2 = r2;
    this.green2 = g2;
    this.blue2 = b2;
    this.textColor = textColor;
  }

  public SpellBase addIngredients(ItemStack... stack) {
    Collections.addAll(ingredients, stack);
    return this;
  }

  public boolean costsMet(EntityPlayer player) {
    boolean matches = true;
    for(Map.Entry<Herb, Double> entry : this.costs.entrySet()){
      Herb herb = entry.getKey();
      double d = entry.getValue();
      matches = matches && PowderInventoryUtil.getPowderTotal(player, herb) >= d;
    }
    return matches && costs.size() > 0 || player.capabilities.isCreativeMode;
  }

  public void enactCosts(EntityPlayer player) {
    for(Map.Entry<Herb, Double> entry : this.costs.entrySet()){
      Herb herb = entry.getKey();
      double d = entry.getValue();
      PowderInventoryUtil.removePowder(player, herb, d);
    }
  }

  public void enactTickCosts(EntityPlayer player) {
    for(Map.Entry<Herb, Double> entry : this.costs.entrySet()){
      Herb herb = entry.getKey();
      double d = entry.getValue();
      PowderInventoryUtil.removePowder(player, herb, d / 20.0);
    }
  }

  public void addToolTip(List<String> tooltip) {
    tooltip.add("" + textColor + TextFormatting.BOLD + I18n.format("roots.spell." + name + ".name") + TextFormatting.RESET);
    for(Map.Entry<Herb, Double> entry : this.costs.entrySet()){
      Herb herb = entry.getKey();
      double d = entry.getValue();
      tooltip.add(I18n.format(herb.getItem().getUnlocalizedName() + ".name") + I18n.format("roots.tooltip.pouch_divider") + d);
    }
  }

  public SpellBase addCost(Herb herb, double amount) {
    if (herb == null) {
      System.out.println("Spell - " + this.getClass().getName() + " - added a null herb ingredient. This is a bug.");
      return this;
    }
    costs.put(herb, amount);
    return this;
  }

  public boolean matchesIngredients(List<ItemStack> ingredients) {
    return ListUtil.stackListsMatch(ingredients, this.ingredients);
  }

  public abstract void cast(EntityPlayer caster);

  public float getRed1() {
    return red1;
  }

  public float getGreen1() {
    return green1;
  }

  public float getBlue1() {
    return blue1;
  }

  public float getRed2() {
    return red2;
  }

  public float getGreen2() {
    return green2;
  }

  public float getBlue2() {
    return blue2;
  }

  public String getName() {
    return name;
  }

  public int getCooldown() {
    return cooldown;
  }

  public TextFormatting getTextColor() {
    return textColor;
  }

  public EnumCastType getCastType() {
    return castType;
  }

  public Map<Herb, Double> getCosts() {
    return costs;
  }

  public List<ItemStack> getIngredients() {
    return ingredients;
  }
}