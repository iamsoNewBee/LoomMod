package com.CatFish.loommod.recipe;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SewingRecipeManager
{
    private static final SewingRecipeManager INSTANCE = new SewingRecipeManager();
    private List<SewingRecipe> recipes = new ArrayList<>();

    public static SewingRecipeManager getInstance() { return INSTANCE; }

    public void addRecipe(SewingRecipe recipe)
    {
        recipes.add(recipe);
    }

    public SewingRecipe findMatchingRecipe(ItemStack tool, ItemStack pattern, ItemStack[] materials)
    {
        for (SewingRecipe recipe : recipes)
        {
            if (recipe.matches(tool, pattern, materials))
                return recipe;
        }
        return null;
    }

    public List<SewingRecipe> findAllMatchingRecipes(ItemStack tool, ItemStack pattern, ItemStack[] materials)
    {
        List<SewingRecipe> matched = new ArrayList<>();
        for (SewingRecipe recipe : recipes)
        {
            if (recipe.matches(tool, pattern, materials))
                matched.add(recipe);
        }
        return matched;
    }
    public void clear() {
        recipes.clear();
    }

    public List<SewingRecipe> getRecipes() {
        return Collections.unmodifiableList(recipes);
    }
}
