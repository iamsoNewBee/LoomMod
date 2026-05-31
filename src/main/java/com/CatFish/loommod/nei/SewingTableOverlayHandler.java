package com.CatFish.loommod.nei;

import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.recipe.IRecipeHandler;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import codechicken.nei.PositionedStack;
import java.util.List;

public class SewingTableOverlayHandler implements IOverlayHandler {
    @Override
    public void overlayRecipe(GuiContainer gui, IRecipeHandler recipe, int recipeIndex, boolean shift) {
        List<PositionedStack> ingredients = recipe.getIngredientStacks(recipeIndex);
        if (ingredients == null) return;

        Container container = gui.inventorySlots;
        // 槽位顺序：0工具槽1，1工具槽2，2-5材料槽，6-14图案槽，15输出槽
        int[] slotIndices = new int[]{0, 1, 2, 3, 4, 5};
        int slotPointer = 0;

        for (PositionedStack pstack : ingredients) {
            if (pstack == null) continue;
            ItemStack stack = pstack.items[0].copy();
            if (stack == null) continue;

            for (int i = slotPointer; i < slotIndices.length; i++) {
                int slotIdx = slotIndices[i];
                ItemStack existing = container.getSlot(slotIdx).getStack();
                if (existing == null) {
                    container.getSlot(slotIdx).putStack(stack);
                    slotPointer = i + 1;
                    break;
                } else if (existing.isItemEqual(stack) && existing.stackSize + stack.stackSize <= existing.getMaxStackSize()) {
                    existing.stackSize += stack.stackSize;
                    container.getSlot(slotIdx).onSlotChanged();
                    slotPointer = i;
                    break;
                }
            }
        }
        container.detectAndSendChanges();
    }
}
