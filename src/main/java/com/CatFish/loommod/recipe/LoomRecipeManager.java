package com.CatFish.loommod.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class LoomRecipeManager {

    private final List<LoomRecipe> recipes = new ArrayList<>();

    public void addRecipe(LoomRecipe recipe) {
        recipes.add(recipe);
    }

    public void removeRecipe(LoomRecipe recipe) {
        recipes.remove(recipe);
    }

    public void removeAll() {
        recipes.clear();
    }

    public LoomRecipe getRecipeForInput(ItemStack stack) {
        for (LoomRecipe recipe : recipes) {
            if (recipe.matches(stack)) return recipe;
        }
        return null;
    }

    public LoomRecipe getRecipeForIdentifiers(String id, int meta) {
        Item item = (Item) Item.itemRegistry.getObject(id);
        if (item == null) return null;
        ItemStack dummy = new ItemStack(item, 1, meta);
        return getRecipeForInput(dummy);
    }
}
