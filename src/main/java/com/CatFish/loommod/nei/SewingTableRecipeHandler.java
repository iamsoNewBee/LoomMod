package com.CatFish.loommod.nei;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.CatFish.loommod.blocks.sewing.GuiSewingTable;
import com.CatFish.loommod.recipe.SewingRecipe;
import com.CatFish.loommod.recipe.SewingRecipeManager;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class SewingTableRecipeHandler extends TemplateRecipeHandler {

    // 偏移量（基于物品格子大小18x18：向左1/4格=4.5≈5，向上2/3格=12）
    private static final int OFFSET_X = -5;
    private static final int OFFSET_Y = -12;

    // 原始坐标（必须与 ContainerSewingTable 完全一致）
    private static final int TOOL1_X = 8;
    private static final int TOOL1_Y = 15;
    private static final int TOOL2_X = 30;
    private static final int TOOL2_Y = 15;

    private static final int MATERIAL_X_START = 10;
    private static final int MATERIAL_Y_START = 35;
    private static final int MATERIAL_SPACING = 18;

    private static final int OUTPUT_X = 143;
    private static final int OUTPUT_Y = 33;

    // 偏移后的坐标
    private static final int NEW_TOOL1_X = TOOL1_X + OFFSET_X;
    private static final int NEW_TOOL1_Y = TOOL1_Y + OFFSET_Y;
    private static final int NEW_TOOL2_X = TOOL2_X + OFFSET_X;
    private static final int NEW_TOOL2_Y = TOOL2_Y + OFFSET_Y;

    private static final int NEW_MATERIAL_X_START = MATERIAL_X_START + OFFSET_X;
    private static final int NEW_MATERIAL_Y_START = MATERIAL_Y_START + OFFSET_Y;

    private static final int NEW_OUTPUT_X = OUTPUT_X + OFFSET_X;
    private static final int NEW_OUTPUT_Y = OUTPUT_Y + OFFSET_Y;

    @Override
    public String getRecipeName() {
        return "Sewing Table";
    }

    @Override
    public String getGuiTexture() {
        return new ResourceLocation("loommod", "textures/gui/sewing_station_new.png").toString();
    }

    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return GuiSewingTable.class;
    }

    @Override
    public String getOverlayIdentifier() {
        return "sewingTable";
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (SewingRecipe recipe : SewingRecipeManager.getInstance().getRecipes()) {
            if (ItemStack.areItemStacksEqual(recipe.getOutput(), result)) {
                arecipes.add(new CachedSewingRecipe(recipe));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (SewingRecipe recipe : SewingRecipeManager.getInstance().getRecipes()) {
            // 检查工具
            if (recipe.getToolOreDict() != null) {
                for (ItemStack ore : OreDictionary.getOres(recipe.getToolOreDict())) {
                    if (ItemStack.areItemStacksEqual(ore, ingredient)) {
                        arecipes.add(new CachedSewingRecipe(recipe));
                        break;
                    }
                }
            } else if (recipe.getTool() != null && ItemStack.areItemStacksEqual(recipe.getTool(), ingredient)) {
                arecipes.add(new CachedSewingRecipe(recipe));
                continue;
            }
            // 检查材料
            for (SewingRecipe.Material mat : recipe.getMaterials()) {
                if (mat.oreDict != null) {
                    for (ItemStack ore : OreDictionary.getOres(mat.oreDict)) {
                        if (ItemStack.areItemStacksEqual(ore, ingredient)) {
                            arecipes.add(new CachedSewingRecipe(recipe));
                            break;
                        }
                    }
                } else if (mat.ingredient != null && ItemStack.areItemStacksEqual(mat.ingredient, ingredient)) {
                    arecipes.add(new CachedSewingRecipe(recipe));
                    break;
                }
            }
        }
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals("sewingTable")) {
            for (SewingRecipe recipe : SewingRecipeManager.getInstance().getRecipes()) {
                arecipes.add(new CachedSewingRecipe(recipe));
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public int recipiesPerPage() {
        return 2;
    }

    @Override
    public void drawExtras(int recipe) {
        // 进度条已删除，不绘制任何额外内容
    }

    public class CachedSewingRecipe extends CachedRecipe {
        private final List<PositionedStack> ingredients = new ArrayList<>();
        private final PositionedStack result;

        public CachedSewingRecipe(SewingRecipe recipe) {
            // 工具槽1（使用偏移后的新坐标）
            if (recipe.getToolOreDict() != null) {
                List<ItemStack> tools = OreDictionary.getOres(recipe.getToolOreDict());
                if (!tools.isEmpty()) {
                    ingredients.add(new PositionedStack(tools, NEW_TOOL1_X, NEW_TOOL1_Y));
                }
            } else if (recipe.getTool() != null) {
                ingredients.add(new PositionedStack(recipe.getTool(), NEW_TOOL1_X, NEW_TOOL1_Y));
            }

            // 工具槽2（保留以备未来使用，坐标也已偏移，但当前逻辑不会添加）
            // 如需启用第二个工具槽，可在此添加类似代码

            // 材料槽（最多4个，2x2网格，使用偏移后的起始坐标）
            List<SewingRecipe.Material> mats = recipe.getMaterials();
            for (int i = 0; i < Math.min(4, mats.size()); i++) {
                SewingRecipe.Material mat = mats.get(i);
                int row = i / 2;
                int col = i % 2;
                int x = NEW_MATERIAL_X_START + col * MATERIAL_SPACING;
                int y = NEW_MATERIAL_Y_START + row * MATERIAL_SPACING;
                ItemStack display = null;
                List<ItemStack> variants = new ArrayList<>();
                if (mat.oreDict != null) {
                    variants = OreDictionary.getOres(mat.oreDict);
                    if (!variants.isEmpty()) display = variants.get(0).copy();
                } else if (mat.ingredient != null) {
                    display = mat.ingredient.copy();
                    variants.add(display);
                }
                if (display != null) {
                    display.stackSize = mat.count;
                    PositionedStack ps = new PositionedStack(display, x, y);
                    if (variants.size() > 1) {
                        ps = new PositionedStack(variants, x, y);
                        ps.setMaxSize(1);
                    }
                    ingredients.add(ps);
                }
            }

            // 输出槽（使用偏移后的新坐标）
            result = new PositionedStack(recipe.getOutput(), NEW_OUTPUT_X, NEW_OUTPUT_Y);
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return getCycledIngredients(cycleticks / 20, ingredients);
        }

        @Override
        public PositionedStack getResult() {
            return result;
        }

        @Override
        public PositionedStack getOtherStack() {
            return null;
        }
    }
}
