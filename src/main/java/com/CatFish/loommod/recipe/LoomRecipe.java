package com.CatFish.loommod.recipe;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class LoomRecipe {

    private final List<ItemStack> matchingInputs; // 所有可能的输入物品（由 CrT 展开）
    private final ItemStack output;
    private final int progressPerUse;
    private final int consumePerUse;
    private final int maxProgress;
    private final boolean useDurability;

    public LoomRecipe(List<ItemStack> matchingInputs, ItemStack output, int progressPerUse, int consumePerUse,
        int maxProgress, boolean useDurability) {
        this.matchingInputs = matchingInputs;
        this.output = output;
        this.progressPerUse = progressPerUse;
        this.consumePerUse = consumePerUse;
        this.maxProgress = maxProgress;
        this.useDurability = useDurability;
    }

    public boolean matches(ItemStack stack) {
        if (stack == null) return false;
        for (ItemStack input : matchingInputs) {
            if (itemsMatch(input, stack)) return true;
        }
        return false;
    }

    private boolean itemsMatch(ItemStack required, ItemStack offered) {
        if (required.getItem() != offered.getItem()) return false;
        // 元数据匹配：required 的元数据为 WILDCARD 则忽略元数据
        if (required.getItemDamage() != OreDictionary.WILDCARD_VALUE
            && required.getItemDamage() != offered.getItemDamage()) return false;
        // NBT 匹配：如果 required 有 NBT，则比较
        if (required.hasTagCompound()) {
            if (!offered.hasTagCompound()) return false;
            if (!required.getTagCompound()
                .equals(offered.getTagCompound())) return false;
        }
        return true;
    }

    // Getter 方法
    public ItemStack getOutput() {
        return output.copy();
    }

    public int getProgressPerUse() {
        return progressPerUse;
    }

    public int getConsumePerUse() {
        return consumePerUse;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public boolean useDurability() {
        return useDurability;
    }
}
