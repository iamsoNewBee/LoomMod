package com.CatFish.loommod.blocks.sewing;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileEntitySewingTable extends TileEntity implements IInventory
{
    // 槽位分配: 0-1工具, 2-5材料(4个), 6-14图案(9个), 15输出
    private ItemStack[] inventory = new ItemStack[16];

    @Override
    public int getSizeInventory() { return inventory.length; }

    @Override
    public ItemStack getStackInSlot(int slot) { return inventory[slot]; }

    @Override
    public ItemStack decrStackSize(int slot, int amount)
    {
        if (inventory[slot] != null)
        {
            ItemStack stack;
            if (inventory[slot].stackSize <= amount)
            {
                stack = inventory[slot];
                inventory[slot] = null;
                this.markDirty();
                if (worldObj != null && !worldObj.isRemote) {
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                }
                return stack;
            }
            else
            {
                stack = inventory[slot].splitStack(amount);
                if (inventory[slot].stackSize == 0) inventory[slot] = null;
                this.markDirty();
                if (worldObj != null && !worldObj.isRemote) {
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                }
                return stack;
            }
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        if (inventory[slot] != null)
        {
            ItemStack stack = inventory[slot];
            inventory[slot] = null;
            return stack;
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        inventory[slot] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
            stack.stackSize = this.getInventoryStackLimit();
        this.markDirty();
        if (worldObj != null && !worldObj.isRemote) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public String getInventoryName() { return "container.sewingTable"; }

    @Override
    public boolean hasCustomInventoryName() { return false; }

    @Override
    public int getInventoryStackLimit() { return 64; }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) <= 64;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {
        if (!worldObj.isRemote) {
            // 掉落工具槽(0-1)和材料槽(2-5)，图案槽(6-14)和输出槽(15)不掉落
            for (int i = 0; i < inventory.length; i++) {
                if (i >= 6 && i <= 15) continue; // 跳过图案槽和输出槽
                ItemStack stack = inventory[i];
                if (stack != null) {
                    net.minecraft.entity.item.EntityItem entity = new net.minecraft.entity.item.EntityItem(
                        worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, stack);
                    worldObj.spawnEntityInWorld(entity);
                    inventory[i] = null;
                }
            }
            // 清空图案槽和输出槽
            for (int i = 6; i <= 15; i++) inventory[i] = null;
            markDirty();
        }
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return slot <= 5; // 允许工具槽(0,1)和材料槽(2-5)放入物品
    }

    // NBT 读写
    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        NBTTagList nbttaglist = tag.getTagList("Items", 10);
        inventory = new ItemStack[getSizeInventory()];
        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound slotTag = nbttaglist.getCompoundTagAt(i);
            int slot = slotTag.getByte("Slot") & 255;
            if (slot >= 0 && slot < inventory.length)
                inventory[slot] = ItemStack.loadItemStackFromNBT(slotTag);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < inventory.length; ++i)
        {
            if (inventory[i] != null)
            {
                NBTTagCompound slotTag = new NBTTagCompound();
                slotTag.setByte("Slot", (byte) i);
                inventory[i].writeToNBT(slotTag);
                nbttaglist.appendTag(slotTag);
            }
        }
        tag.setTag("Items", nbttaglist);
    }
}
