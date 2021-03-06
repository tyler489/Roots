package epicsquid.roots.item;

import java.util.List;

import epicsquid.mysticallib.item.ItemBase;
import epicsquid.mysticallib.util.Util;
import epicsquid.roots.EventManager;
import epicsquid.roots.Roots;
import epicsquid.roots.event.SpellEvent;
import epicsquid.roots.spell.SpellBase;
import epicsquid.roots.spell.SpellRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemStaff extends ItemBase {
  public ItemStaff(String name) {
    super(name);
    setMaxStackSize(1);
  }

  @Override
  public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
    if (oldStack.hasTagCompound() && newStack.hasTagCompound()) {
      return slotChanged || oldStack.getTagCompound().getInteger("selected") != newStack.getTagCompound().getInteger("selected");
    }
    return slotChanged;
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
    ;
    ItemStack stack = player.getHeldItem(hand);
    if (player.isSneaking()) {
      if (!stack.hasTagCompound()) {
        addTagCompound(stack);
      }
      stack.getTagCompound().setInteger("selected", stack.getTagCompound().getInteger("selected") + 1);
      if (stack.getTagCompound().getInteger("selected") > 3) {
        stack.getTagCompound().setInteger("selected", 0);
      }
      return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
    } else {
      if (stack.hasTagCompound()) {
        if (!stack.getTagCompound().hasKey("cooldown")) {
          SpellBase spell = SpellRegistry.spellRegistry.get(stack.getTagCompound().getString("spell" + stack.getTagCompound().getInteger("selected")));
          if (spell != null) {
            SpellEvent event = new SpellEvent(player, spell);
            MinecraftForge.EVENT_BUS.post(event);
            spell = event.getSpell();
            if (spell.getCastType() == SpellBase.EnumCastType.INSTANTANEOUS) {
              if (spell.costsMet(player)) {
                spell.cast(player);
                spell.enactCosts(player);
                stack.getTagCompound().setInteger("cooldown", event.getCooldown());
                stack.getTagCompound().setInteger("lastCooldown", event.getCooldown());
                return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
              }
            } else if (spell.getCastType() == SpellBase.EnumCastType.CONTINUOUS) {
              player.setActiveHand(hand);
              return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
          }
        }
      }
    }
    return new ActionResult<>(EnumActionResult.FAIL, player.getHeldItem(hand));
  }

  @Override
  public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
    if (stack.hasTagCompound() && player instanceof EntityPlayer) {
      if (!stack.getTagCompound().hasKey("cooldown")) {
        SpellBase spell = SpellRegistry.spellRegistry.get(stack.getTagCompound().getString("spell" + stack.getTagCompound().getInteger("selected")));
        if (spell != null) {
          if (spell.getCastType() == SpellBase.EnumCastType.CONTINUOUS) {
            if (spell.costsMet((EntityPlayer) player)) {
              spell.cast((EntityPlayer) player);
              spell.enactTickCosts((EntityPlayer) player);
            }
          }
        }
      }
    }
  }

  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft) {
    if (stack.hasTagCompound()) {
      SpellBase spell = SpellRegistry.spellRegistry.get(stack.getTagCompound().getString("spell" + stack.getTagCompound().getInteger("selected")));
      if (spell != null) {
        SpellEvent event = new SpellEvent((EntityPlayer) entity, spell);
        MinecraftForge.EVENT_BUS.post(event);
        if (spell.getCastType() == SpellBase.EnumCastType.CONTINUOUS) {
          stack.getTagCompound().setInteger("cooldown", event.getCooldown());
          stack.getTagCompound().setInteger("lastCooldown", event.getCooldown());
        }
      }
    }
  }

  @Override
  public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
    if (stack.hasTagCompound()) {
      if (stack.getTagCompound().hasKey("cooldown")) {
        stack.getTagCompound().setInteger("cooldown", stack.getTagCompound().getInteger("cooldown") - 1);
        if (stack.getTagCompound().getInteger("cooldown") <= 0) {
          stack.getTagCompound().removeTag("cooldown");
          stack.getTagCompound().removeTag("lastCooldown");
        }
      }
    }
  }

  public static void createData(ItemStack stack, String spellName) {
    if (!stack.hasTagCompound()) {
      addTagCompound(stack);
    }
    stack.getTagCompound().setString("spell" + stack.getTagCompound().getInteger("selected"), spellName);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
    if (!stack.hasTagCompound()) {
      addTagCompound(stack);
    } else {
      tooltip.add(I18n.format("roots.tooltip.staff.selected") + (stack.getTagCompound().getInteger("selected") + 1));
      SpellBase spell = SpellRegistry.spellRegistry.get(stack.getTagCompound().getString("spell" + stack.getTagCompound().getInteger("selected")));
      if (spell != null) {
        tooltip.add("");
        spell.addToolTip(tooltip);
      }
    }
  }

  private static void addTagCompound(ItemStack stack) {
    stack.setTagCompound(new NBTTagCompound());
    stack.getTagCompound().setInteger("selected", 0);
    stack.getTagCompound().setString("spell0", "null");
    stack.getTagCompound().setString("spell1", "null");
    stack.getTagCompound().setString("spell2", "null");
    stack.getTagCompound().setString("spell3", "null");
  }

  @Override
  public boolean showDurabilityBar(ItemStack stack) {
    if (stack.hasTagCompound()) {
      SpellBase spell = SpellRegistry.spellRegistry.get(stack.getTagCompound().getString("spell" + stack.getTagCompound().getInteger("selected")));
      if (stack.getTagCompound().hasKey("cooldown") && spell != null) {
        return true;
      }
    }
    return false;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public int getRGBDurabilityForDisplay(ItemStack stack) {
    if (stack.hasTagCompound()) {
      SpellBase spell = SpellRegistry.spellRegistry.get(stack.getTagCompound().getString("spell" + stack.getTagCompound().getInteger("selected")));
      if (spell != null) {
        double factor = 0.5f * (Math.sin(6.0f * Math.toRadians(EventManager.ticks + Minecraft.getMinecraft().getRenderPartialTicks())) + 1.0f);
        return Util
            .intColor((int) (255 * (spell.getRed1() * factor + spell.getRed2() * (1.0 - factor))), (int) (255 * (spell.getGreen1() * factor + spell.getGreen2() * (1.0 - factor))),
                (int) (255 * (spell.getBlue1() * factor + spell.getBlue2() * (1.0 - factor))));
      }
    }
    return Util.intColor(255, 255, 255);
  }

  @Override
  public double getDurabilityForDisplay(ItemStack stack) {
    if (stack.hasTagCompound()) {
      if (stack.getTagCompound().hasKey("cooldown")) {
        return (double) stack.getTagCompound().getInteger("cooldown") / (double) stack.getTagCompound().getInteger("lastCooldown");
      }
    }
    return 0;
  }

  @Override
  public int getMaxItemUseDuration(ItemStack stack) {
    return 72000;
  }

  @Override
  public EnumAction getItemUseAction(ItemStack stack) {
    if (stack.hasTagCompound()) {
      SpellBase spell = SpellRegistry.spellRegistry.get(stack.getTagCompound().getString("spell" + stack.getTagCompound().getInteger("selected")));
      if (spell != null) {
        if (spell.getCastType() == SpellBase.EnumCastType.CONTINUOUS) {
          return EnumAction.BOW;
        } else {
          return EnumAction.NONE;
        }
      }
    }
    return EnumAction.NONE;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void initModel() {
    ModelBakery
        .registerItemVariants(this, new ModelResourceLocation(getRegistryName().toString()), new ModelResourceLocation(getRegistryName().toString() + "_1"));
    ModelLoader.setCustomMeshDefinition(this, stack -> {
      ResourceLocation baseName = getRegistryName();
      if (stack.getDisplayName().compareToIgnoreCase("Shiny Rod") == 0) {
        baseName = new ResourceLocation(Roots.MODID + ":shiny_rod");
      }
      if (stack.getDisplayName().compareToIgnoreCase("Cutie Moon Rod") == 0) {
        baseName = new ResourceLocation(Roots.MODID + ":moon_rod");
      }
      if (stack.hasTagCompound()) {
        String s = stack.getTagCompound().getString("spell" + stack.getTagCompound().getInteger("selected"));
        if (SpellRegistry.spellRegistry.containsKey(s)) {
          return new ModelResourceLocation(baseName.toString() + "_1");
        } else {
          return new ModelResourceLocation(baseName.toString());
        }
      }
      return new ModelResourceLocation(baseName.toString());
    });
  }

  public static class StaffColorHandler implements IItemColor {

    @Override
    public int colorMultiplier(ItemStack stack, int tintIndex) {
      if (stack.hasTagCompound() && stack.getItem() instanceof ItemStaff) {
        if (stack.getDisplayName().compareToIgnoreCase("Shiny Rod") == 0 || stack.getDisplayName().compareToIgnoreCase("Cutie Moon Rod") == 0) {
          SpellBase spell = SpellRegistry.spellRegistry.get(stack.getTagCompound().getString("spell" + stack.getTagCompound().getInteger("selected")));
          if (spell != null) {
            if (tintIndex == 0) {
              int r = (int) (255 * spell.getRed1());
              int g = (int) (255 * spell.getGreen1());
              int b = (int) (255 * spell.getBlue1());
              return (r << 16) + (g << 8) + b;
            }
            if (tintIndex == 1) {
              int r = (int) (255 * spell.getRed2());
              int g = (int) (255 * spell.getGreen2());
              int b = (int) (255 * spell.getBlue2());
              return (r << 16) + (g << 8) + b;
            }
          }
          return Util.intColor(255, 255, 255);
        } else {
          SpellBase spell = SpellRegistry.spellRegistry.get(stack.getTagCompound().getString("spell" + stack.getTagCompound().getInteger("selected")));
          if (tintIndex == 1) {
            int r = (int) (255 * spell.getRed1());
            int g = (int) (255 * spell.getGreen1());
            int b = (int) (255 * spell.getBlue1());
            return (r << 16) + (g << 8) + b;
          }
          if (tintIndex == 2) {
            int r = (int) (255 * spell.getRed2());
            int g = (int) (255 * spell.getGreen2());
            int b = (int) (255 * spell.getBlue2());
            return (r << 16) + (g << 8) + b;
          }
          return Util.intColor(255, 255, 255);
        }
      }
      return Util.intColor(255, 255, 255);
    }
  }
}