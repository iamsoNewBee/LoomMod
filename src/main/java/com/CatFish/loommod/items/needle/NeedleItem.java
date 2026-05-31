package com.CatFish.loommod.items.needle;

import com.google.common.collect.ImmutableSet;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import java.util.Set;

public class NeedleItem extends Item
{
    private final INeedleTier tier;

    public NeedleItem(INeedleTier tier)
    {
        super();
        this.tier = tier;
        this.setMaxDamage(tier.getMaxUses());
        this.setMaxStackSize(1);
        this.setNoRepair();
    }

    @Override
    public boolean isItemTool(ItemStack stack)
    {
        return true;
    }

    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass)
    {
        if ("sewing_needle".equals(toolClass))
        {
            return tier.getHarvestLevel();
        }
        return super.getHarvestLevel(stack, toolClass);
    }

    @Override
    public boolean canHarvestBlock(net.minecraft.block.Block block, ItemStack stack)
    {
        return false;
    }

    @Override
    public float getDigSpeed(ItemStack stack, net.minecraft.block.Block block, int meta)
    {
        return 1.0f;
    }

    @Override
    public int getItemEnchantability()
    {
        return tier.getEnchantability();
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
    {
        return tier.getRepairMaterial(toRepair, repair);
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack)
    {
        return ImmutableSet.of("sewing_needle");
    }

    @Override
    public boolean isFull3D()
    {
        return true;
    }
}
