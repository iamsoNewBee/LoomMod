package com.CatFish.loommod.minetweaker;

import com.CatFish.loommod.LoomMod;
import com.CatFish.loommod.recipe.LoomRecipe;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;

@ZenClass("mods.loom.LoomRecipes")
public class MtLoomRecipes {

    @ZenMethod
    public static void addRecipe(IIngredient input, IItemStack output, int progressPerUse, int consumePerUse,
        int maxProgress, boolean useDurability) {
        MineTweakerAPI
            .apply(new AddRecipeAction(input, output, progressPerUse, consumePerUse, maxProgress, useDurability));
    }

    @ZenMethod
    public static void removeAll() {
        MineTweakerAPI.apply(new RemoveAllAction());
    }

    // 动作类：添加配方
    private static class AddRecipeAction implements minetweaker.IUndoableAction {

        private final IIngredient input;
        private final IItemStack output;
        private final int progressPerUse;
        private final int consumePerUse;
        private final int maxProgress;
        private final boolean useDurability;

        public AddRecipeAction(IIngredient input, IItemStack output, int progressPerUse, int consumePerUse,
            int maxProgress, boolean useDurability) {
            this.input = input;
            this.output = output;
            this.progressPerUse = progressPerUse;
            this.consumePerUse = consumePerUse;
            this.maxProgress = maxProgress;
            this.useDurability = useDurability;
        }

        @Override
        public void apply() {
            List<IItemStack> iItems = input.getItems();
            if (iItems == null || iItems.isEmpty()) {
                MineTweakerAPI.logError("Loom recipe input has no matching items: " + input);
                return;
            }
            List<ItemStack> matchingInputs = new ArrayList<>();
            for (IItemStack iItem : iItems) {
                matchingInputs.add(MineTweakerMC.getItemStack(iItem));
            }
            ItemStack outputStack = MineTweakerMC.getItemStack(output);
            LoomRecipe recipe = new LoomRecipe(
                matchingInputs,
                outputStack,
                progressPerUse,
                consumePerUse,
                maxProgress,
                useDurability);
            LoomMod.recipeManager.addRecipe(recipe);
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            // 由于无法精确定位单个配方，undo 直接移除所有配方（可自定义更精确的 undo）
            LoomMod.recipeManager.removeAll();
        }

        @Override
        public String describe() {
            return "Adding Loom recipe for " + input;
        }

        @Override
        public String describeUndo() {
            return "Removing Loom recipe for " + input;
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

    // 动作类：移除所有配方
    private static class RemoveAllAction implements minetweaker.IUndoableAction {

        @Override
        public void apply() {
            LoomMod.recipeManager.removeAll();
        }

        @Override
        public boolean canUndo() {
            return false; // 移除所有不可撤销
        }

        @Override
        public void undo() {}

        @Override
        public String describe() {
            return "Removing all Loom recipes";
        }

        @Override
        public String describeUndo() {
            return "";
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}
