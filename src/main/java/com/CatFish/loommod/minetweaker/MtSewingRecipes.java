package com.CatFish.loommod.minetweaker;

import com.CatFish.loommod.recipe.SewingRecipe;
import com.CatFish.loommod.recipe.SewingRecipeManager;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;

@ZenClass("mods.loom.SewingRecipes")
public class MtSewingRecipes {

    @ZenMethod
    public static void addRecipe(IIngredient[] materials, IIngredient tool, IIngredient pattern, IItemStack output) {
        MineTweakerAPI.apply(new AddRecipeAction(materials, tool, pattern, output));
    }

    @ZenMethod
    public static void removeAll() {
        MineTweakerAPI.apply(new RemoveAllAction());
    }

    // 添加配方动作
    private static class AddRecipeAction implements minetweaker.IUndoableAction {
        private final IIngredient[] materials;
        private final IIngredient tool;
        private final IIngredient pattern;
        private final IItemStack output;

        public AddRecipeAction(IIngredient[] materials, IIngredient tool, IIngredient pattern, IItemStack output) {
            this.materials = materials;
            this.tool = tool;
            this.pattern = pattern;
            this.output = output;
        }

        @Override
        public void apply() {
            // 转换工具
            ItemStack toolStack = tool != null ? convertIngredientToItemStack(tool) : null;
            // 转换图案
            ItemStack patternStack = pattern != null ? convertIngredientToItemStack(pattern) : null;
            // 转换材料列表
            List<SewingRecipe.Material> materialList = new ArrayList<>();
            for (IIngredient mat : materials) {
                if (mat != null) {
                    int amount = mat.getAmount();                     // 获取脚本中指定的数量
                    List<IItemStack> items = mat.getItems();          // 获取所有匹配的物品
                    if (items == null || items.isEmpty()) {
                        MineTweakerAPI.logError("Material has no matching items: " + mat);
                        continue;
                    }
                    ItemStack template = MineTweakerMC.getItemStack(items.get(0));
                    template.stackSize = 1;                           // 模板数量设为1，数量由amount单独保存
                    materialList.add(new SewingRecipe.Material(template, amount));
                }
            }
            ItemStack outputStack = MineTweakerMC.getItemStack(output);
            SewingRecipe recipe = new SewingRecipe(materialList, toolStack, patternStack, outputStack);
            SewingRecipeManager.getInstance().addRecipe(recipe);
        }

        // 辅助方法：将IIngredient转换为ItemStack（用于工具/图案，一般数量为1即可）
        private ItemStack convertIngredientToItemStack(IIngredient ingredient) {
            if (ingredient == null) return null;
            List<IItemStack> items = ingredient.getItems();
            if (items == null || items.isEmpty()) return null;
            return MineTweakerMC.getItemStack(items.get(0));
        }

        @Override
        public boolean canUndo() { return true; }

        @Override
        public void undo() {
            // 简单移除所有配方（无法精确 undo，可忽略或实现更精确移除）
            SewingRecipeManager.getInstance().clear();
        }

        @Override
        public String describe() {
            return "Adding Sewing recipe for " + output;
        }

        @Override
        public String describeUndo() {
            return "Removing Sewing recipe for " + output;
        }

        @Override
        public Object getOverrideKey() { return null; }
    }

    // 移除所有配方
    private static class RemoveAllAction implements minetweaker.IUndoableAction {
        @Override
        public void apply() {
            SewingRecipeManager.getInstance().clear();
        }

        @Override
        public boolean canUndo() { return false; }

        @Override
        public void undo() {}

        @Override
        public String describe() {
            return "Removing all Sewing recipes";
        }

        @Override
        public String describeUndo() { return ""; }

        @Override
        public Object getOverrideKey() { return null; }
    }
}
