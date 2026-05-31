package com.CatFish.loommod.items.needle;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public enum Needles implements INeedleTier
{
    WOOD("wood", 1, 10, 2.0f, 0, 15, "plankWood"),
    STONE("stone", 2, 15, 4.0f, 1, 5, "cobblestone"),
    IRON("iron", 3, 150, 6.0f, 2, 4, "ingotIron"),
    DIAMOND("diamond", 4, 250, 8.0f, 3, 10, "gemDiamond"),
    GOLD("gold", 1, 25, 12.0f, 0, 22, "ingotGold"),
    BONE("bone", 2, 50, 4.0f, 1, 12, "bone");

    private final String type;
    private final int harvestLevel;
    private final int maxUses;
    private final float efficiency;
    private final float attackDamage;
    private final int enchantability;
    private final String repairOreDict;

    Needles(String type, int harvestLevel, int maxUses, float efficiency, float attackDamage, int enchantability, String repairOreDict)
    {
        this.type = type;
        this.harvestLevel = harvestLevel;
        this.maxUses = maxUses;
        this.efficiency = efficiency;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairOreDict = repairOreDict;
    }

    @Override
    public int getMaxUses() { return maxUses; }

    @Override
    public float getEfficiency() { return efficiency; }

    @Override
    public float getAttackDamage() { return attackDamage; }

    @Override
    public int getHarvestLevel() { return harvestLevel; }

    @Override
    public int getEnchantability() { return enchantability; }

    @Override
    public boolean getRepairMaterial(ItemStack toRepair, ItemStack repair)
    {
        if (repair == null) return false;
        for (ItemStack ore : OreDictionary.getOres(repairOreDict))
        {
            if (OreDictionary.itemMatches(ore, repair, false)) return true;
        }
        return false;
    }

    public String getType() { return type; }
}
