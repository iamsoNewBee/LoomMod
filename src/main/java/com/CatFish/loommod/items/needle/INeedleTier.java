package com.CatFish.loommod.items.needle;

import net.minecraft.item.ItemStack;

public interface INeedleTier
{
    int getMaxUses();
    float getEfficiency();
    float getAttackDamage();
    int getHarvestLevel();
    int getEnchantability();
    boolean getRepairMaterial(ItemStack toRepair, ItemStack repair);
}
