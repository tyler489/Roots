package epicsquid.roots.rune;

import epicsquid.roots.tileentity.TileEntityWildrootRune;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

public abstract class Rune {

    private Item incense;

    public Rune(){

    }

    public abstract void saveToEntity(NBTTagCompound tag);

    public abstract void readFromEntity(NBTTagCompound tag);

    public abstract void activate(TileEntityWildrootRune entity, EntityPlayer player);

    public boolean isCharged(TileEntityWildrootRune entity){
        if(incense != null){
            if(entity.getIncenseBurner() == null){
                return false;
            }
            if(entity.getIncenseBurner().isLit() && entity.getIncenseBurner().inventory.getStackInSlot(0).getItem() == incense){
                return true;
            }
        }

        return false;
    }

    public Item getIncense() {
        return incense;
    }

    public void setIncense(Item incense) {
        this.incense = incense;
    }
}