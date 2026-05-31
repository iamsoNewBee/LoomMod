package com.CatFish.loommod.items;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;

import com.CatFish.loommod.LoomMod;

public class ItemFlintKnife extends ItemTool {

    public ItemFlintKnife() {
        super(1.0f, ToolMaterial.WOOD, null);
        setMaxDamage(131);
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.tabTools);
    }

    @Override
    public float getDigSpeed(ItemStack stack, Block block, int meta) {
        return 1.0f; // 始终返回基础速度，不调用父类方法
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z,
        EntityLivingBase player) {
        if (world.isRemote) return true;

        // 破坏草
        if (block == Blocks.tallgrass) {
            if (!world.isRemote) {
                world.spawnEntityInWorld(
                    new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, new ItemStack(LoomMod.grassString, 1, 0)));
            }
            stack.damageItem(1, player);
            return true;
        }
        // 破坏枯死的灌木或树叶
        else if (block == Blocks.deadbush || block.isLeaves(world, x, y, z)) {
            if (!world.isRemote) {
                world.spawnEntityInWorld(
                    new EntityItem(
                        world,
                        x + 0.5,
                        y + 0.5,
                        z + 0.5,
                        new ItemStack(net.minecraft.init.Items.stick, 1, 0)));
                world.spawnEntityInWorld(
                    new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, new ItemStack(LoomMod.grassString, 2, 0)));
            }
            stack.damageItem(1, player);
            return true;
        }
        return false;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        Block block = world.getBlock(x, y, z);
        // 潜行右键草方块
        if (player.isSneaking() && block == Blocks.grass) {
            world.setBlock(x, y, z, Blocks.dirt);
            world.spawnEntityInWorld(
                new EntityItem(world, x + 0.5, y + 1.5, z + 0.5, new ItemStack(LoomMod.grassString, 1, 0)));
            stack.damageItem(1, player);
            return true;
        }
        return false;
    }

    @Override
    public boolean canHarvestBlock(Block block, ItemStack stack) {
        // 不允许用刀挖掘任何方块，避免误触
        return false;
    }
}
