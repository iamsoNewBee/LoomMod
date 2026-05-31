package com.CatFish.loommod.tileentity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import com.CatFish.loommod.LoomMod;
import com.CatFish.loommod.recipe.LoomRecipe;

public class TileEntityLoom extends TileEntity {

    private int progress = 0; // 当前进度
    private String currentInputId; // 当前配方输入物品的注册名
    private int currentInputMeta; // 当前配方输入物品的 metadata

    public int getProgress() {
        return progress;
    }

    /**
     * 设置进度并强制同步客户端
     */
    public void setProgress(int newProgress) {
        this.progress = newProgress;
        markDirty();
        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            // 强制重新渲染
            worldObj.func_147479_m(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        progress = tag.getInteger("progress");
        currentInputId = tag.getString("inputId");
        if (currentInputId.isEmpty()) currentInputId = null;
        currentInputMeta = tag.getInteger("inputMeta");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("progress", progress);
        tag.setString("inputId", currentInputId == null ? "" : currentInputId);
        tag.setInteger("inputMeta", currentInputMeta);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void onActivated(EntityPlayer player) {
        World world = getWorldObj();
        if (world.isRemote) return;

        ItemStack held = player.getHeldItem();
        if (held == null) return;

        // 空闲状态
        if (currentInputId == null) {
            // 查找匹配的配方
            LoomRecipe recipe = LoomMod.recipeManager.getRecipeForInput(held);
            if (recipe == null) {
                player.addChatMessage(new ChatComponentText("§c这个物品不能用于织布机！"));
                return;
            }
            // 启动织布机
            currentInputId = Item.itemRegistry.getNameForObject(held.getItem());
            currentInputMeta = held.getItemDamage();
            setProgress(0);
            // 消耗物品/耐久
            if (!consumeFromPlayer(player, held, recipe)) {
                // 消耗失败，回退状态
                currentInputId = null;
                setProgress(0);
                return;
            }
            world.spawnParticle("largesmoke", xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, 0, 0, 0);
            markDirty();
            world.markBlockForUpdate(xCoord, yCoord, zCoord);
            player.swingItem();
        } else {
            // 工作中，获取当前配方
            LoomRecipe currentRecipe = LoomMod.recipeManager.getRecipeForIdentifiers(currentInputId, currentInputMeta);
            if (currentRecipe == null) {
                // 配方已不存在，重置
                reset();
                player.addChatMessage(new ChatComponentText("§c当前配方已失效，织布机已重置。"));
                return;
            }
            // 检查手持物品是否与当前配方输入匹配
            if (!currentRecipe.matches(held)) {
                player.addChatMessage(new ChatComponentText("§c请使用与启动时相同的物品继续！"));
                return;
            }
            // 消耗物品/耐久
            if (!consumeFromPlayer(player, held, currentRecipe)) {
                return; // 消耗失败
            }

            world.spawnParticle("cloud", xCoord + 0.5, yCoord + 1.5, zCoord + 0.5, 0.1, 0.1, 0.1);
            player.swingItem();

            // 推进进度（避免设置 >= maxProgress，防止图标数组越界）
            int newProgress = progress + currentRecipe.getProgressPerUse();
            if (newProgress >= currentRecipe.getMaxProgress()) {
                // 产出物品
                ItemStack output = currentRecipe.getOutput().copy();
                if (!world.isRemote) {
                    EntityItem entity = new EntityItem(world, xCoord + 0.5, yCoord + 1.5, zCoord + 0.5, output);
                    world.spawnEntityInWorld(entity);
                }
                // 重置织布机
                reset();
            } else {
                setProgress(newProgress);
            }
        }
    }

    /** 消耗物品/耐久，返回是否成功 */
    private boolean consumeFromPlayer(EntityPlayer player, ItemStack held, LoomRecipe recipe) {
        if (player.capabilities.isCreativeMode) return true;

        if (recipe.useDurability()) {
            // 消耗耐久
            if (held.getItemDamage() + recipe.getConsumePerUse() > held.getMaxDamage()) {
                player.addChatMessage(new ChatComponentText("§c工具耐久不足！"));
                return false;
            }
            held.damageItem(recipe.getConsumePerUse(), player);
            if (held.stackSize <= 0) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            }
        } else {
            // 消耗数量
            if (held.stackSize < recipe.getConsumePerUse()) {
                player.addChatMessage(new ChatComponentText("§c物品数量不足！"));
                return false;
            }
            held.stackSize -= recipe.getConsumePerUse();
            if (held.stackSize <= 0) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            }
        }
        return true;
    }

    private void reset() {
        progress = 0;
        currentInputId = null;
        currentInputMeta = 0;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
}
