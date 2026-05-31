package com.CatFish.loommod.recipe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import java.util.List;

public class SewingRecipe
{
    private final List<Material> materials;
    private final ItemStack tool;          // 具体物品工具，可为 null
    private final String toolOreDict;      // 矿辞工具，优先于 tool
    private final ItemStack pattern;
    private final ItemStack output;

    // 构造方法：使用矿辞工具
    public SewingRecipe(List<Material> materials, String toolOreDict, ItemStack pattern, ItemStack output) {
        this.materials = materials;
        this.tool = null;
        this.toolOreDict = toolOreDict;
        this.pattern = pattern;
        this.output = output;
    }

    // 构造方法：使用具体物品工具（兼容旧代码）
    public SewingRecipe(List<Material> materials, ItemStack tool, ItemStack pattern, ItemStack output) {
        this.materials = materials;
        this.tool = tool;
        this.toolOreDict = null;
        this.pattern = pattern;
        this.output = output;
    }

    public boolean matches(ItemStack toolStack, ItemStack patternStack, ItemStack[] materialStacks) {
        // 检查工具（支持矿辞）
        if (tool != null || toolOreDict != null) {
            if (toolStack == null) return false;
            if (toolOreDict != null) {
                int[] ids = OreDictionary.getOreIDs(toolStack);
                boolean found = false;
                for (int id : ids) {
                    if (OreDictionary.getOreName(id).equals(toolOreDict)) {
                        found = true;
                        break;
                    }
                }
                if (!found) return false;
            } else {
                if (!areStacksEqual(tool, toolStack)) return false;
            }
        } else {
            if (toolStack != null) return false;
        }

        // 检查图案
        if (pattern != null && !areStacksEqual(pattern, patternStack)) return false;
        if (pattern == null && patternStack != null) return false;

        // 复制材料槽
        ItemStack[] stacksCopy = new ItemStack[materialStacks.length];
        for (int i = 0; i < materialStacks.length; i++) {
            if (materialStacks[i] != null) {
                stacksCopy[i] = materialStacks[i].copy();
            }
        }

        // 匹配材料（支持矿辞）
        for (Material mat : materials) {
            int remaining = mat.count;
            boolean isOreDictMat = mat.oreDict != null;
            for (int i = 0; i < stacksCopy.length; i++) {
                if (stacksCopy[i] == null) continue;
                boolean matches = false;
                if (isOreDictMat) {
                    int[] ids = OreDictionary.getOreIDs(stacksCopy[i]);
                    for (int id : ids) {
                        if (OreDictionary.getOreName(id).equals(mat.oreDict)) {
                            matches = true;
                            break;
                        }
                    }
                } else {
                    matches = areStacksEqual(mat.ingredient, stacksCopy[i]);
                }
                if (matches) {
                    int available = stacksCopy[i].stackSize;
                    int take = Math.min(remaining, available);
                    remaining -= take;
                    if (take >= available) {
                        stacksCopy[i] = null;
                    } else {
                        stacksCopy[i].stackSize -= take;
                    }
                    if (remaining <= 0) break;
                }
            }
            if (remaining > 0) return false;
        }
        return true;
    }

    public static boolean areStacksEqual(ItemStack a, ItemStack b) {
        if (a == null || b == null) return false;
        if (a.getItem() != b.getItem()) return false;
        if (a.getItemDamage() != 32767 && a.getItemDamage() != b.getItemDamage()) return false;
        if (a.hasTagCompound() != b.hasTagCompound()) return false;
        if (a.hasTagCompound() && !a.getTagCompound().equals(b.getTagCompound())) return false;
        return true;
    }

    public ItemStack getOutput() { return output.copy(); }
    public List<Material> getMaterials() { return materials; }
    public ItemStack getTool() { return tool; }
    public String getToolOreDict() { return toolOreDict; }
    public ItemStack getPattern() { return pattern; }

    public static class Material {
        public final ItemStack ingredient;   // 具体物品，当 oreDict 为 null 时使用
        public final String oreDict;         // 矿辞，优先于 ingredient
        public final int count;

        // 使用具体物品
        public Material(ItemStack ingredient, int count) {
            this.ingredient = ingredient;
            this.oreDict = null;
            this.count = count;
        }

        // 使用矿辞
        public Material(String oreDict, int count) {
            this.ingredient = null;
            this.oreDict = oreDict;
            this.count = count;
        }
    }

    public String getRecipeId() {
        return output.getItem().getUnlocalizedName() + ":" + output.stackSize;
    }
}
