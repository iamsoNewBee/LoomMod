package com.CatFish.loommod.blocks.sewing;

import com.CatFish.loommod.recipe.SewingRecipe;
import com.CatFish.loommod.recipe.SewingRecipeManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class ContainerSewingTable extends Container
{
    private String lastSelectedRecipeId = "";
    private SewingRecipe currentRecipe = null;
    private TileEntitySewingTable tileEntity;
    private List<SewingRecipe> availableRecipes = new ArrayList<>();
    private int selectedRecipeIndex = -1;

    public ContainerSewingTable(InventoryPlayer playerInv, TileEntitySewingTable te)
    {
        this.tileEntity = te;

        // 工具槽 (2个)
        this.addSlotToContainer(new Slot(te, 0, 8, 15));
        this.addSlotToContainer(new Slot(te, 1, 30, 15));

        // 材料槽 (4个) 2x2网格，向右偏移2像素 (1/8格)
        this.addSlotToContainer(new Slot(te, 2, 10, 35));   // 原8→10
        this.addSlotToContainer(new Slot(te, 3, 28, 35));   // 原26→28
        this.addSlotToContainer(new Slot(te, 4, 10, 53));   // 原8→10
        this.addSlotToContainer(new Slot(te, 5, 28, 53));   // 原26→28

        // 图案槽 (9个) 3x3网格，起始坐标 (x=57, y=14)，每个间隔18
        int patternStartX = 57;
        int patternStartY = 14;
        int patternIndex = 6;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int x = patternStartX + col * 18;
                int y = patternStartY + row * 18;
                this.addSlotToContainer(new SlotPattern(te, patternIndex++, x, y));
            }
        }

        // 输出槽
        this.addSlotToContainer(new SlotOutput(te, 15, 143, 33));

        // 玩家物品栏
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
        for (int i = 0; i < 9; ++i)
            this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 142));

        onCraftMatrixChanged(te);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return tileEntity.isUseableByPlayer(player);
    }

    @Override
    public void onCraftMatrixChanged(IInventory inv) {
        ItemStack tool1 = inv.getStackInSlot(0);
        ItemStack tool2 = inv.getStackInSlot(1);
        ItemStack pattern = null;
        ItemStack[] materials = new ItemStack[4];
        for (int i = 0; i < 4; i++)
            materials[i] = inv.getStackInSlot(i + 2);

        List<SewingRecipe> newAvailable = new ArrayList<>();
        for (SewingRecipe recipe : SewingRecipeManager.getInstance().getRecipes()) {
            boolean toolMatches = false;
            ItemStack requiredItem = recipe.getTool();
            String requiredOre = recipe.getToolOreDict();

            if (requiredItem != null || requiredOre != null) {
                if (tool1 != null) {
                    if (requiredOre != null) {
                        int[] ids = OreDictionary.getOreIDs(tool1);
                        for (int id : ids) {
                            if (OreDictionary.getOreName(id).equals(requiredOre)) {
                                toolMatches = true;
                                break;
                            }
                        }
                    } else if (SewingRecipe.areStacksEqual(requiredItem, tool1)) {
                        toolMatches = true;
                    }
                }
                if (!toolMatches && tool2 != null) {
                    if (requiredOre != null) {
                        int[] ids = OreDictionary.getOreIDs(tool2);
                        for (int id : ids) {
                            if (OreDictionary.getOreName(id).equals(requiredOre)) {
                                toolMatches = true;
                                break;
                            }
                        }
                    } else if (SewingRecipe.areStacksEqual(requiredItem, tool2)) {
                        toolMatches = true;
                    }
                }
            } else {
                toolMatches = (tool1 == null && tool2 == null);
            }

            if (!toolMatches) continue;
            if (recipe.getPattern() != null) continue;
            if (recipe.matches(tool1, pattern, materials) || recipe.matches(tool2, pattern, materials)) {
                newAvailable.add(recipe);
            }
        }

        int newSelected = -1;
        if (!lastSelectedRecipeId.isEmpty()) {
            for (int i = 0; i < newAvailable.size(); i++) {
                if (newAvailable.get(i).getRecipeId().equals(lastSelectedRecipeId)) {
                    newSelected = i;
                    break;
                }
            }
        }
        if (newSelected == -1 && !newAvailable.isEmpty()) {
            newSelected = 0;
            lastSelectedRecipeId = newAvailable.get(newSelected).getRecipeId();
        }

        availableRecipes = newAvailable;
        selectedRecipeIndex = newSelected;
        if (selectedRecipeIndex >= 0) {
            currentRecipe = availableRecipes.get(selectedRecipeIndex);
            lastSelectedRecipeId = currentRecipe.getRecipeId();
        } else {
            currentRecipe = null;
            lastSelectedRecipeId = "";
        }
        updatePatternSlots();
        updateOutputSlot();
    }

    private void updatePatternSlots() {
        for (int i = 6; i <= 14; i++) {
            tileEntity.setInventorySlotContents(i, null);
        }
        int limit = Math.min(9, availableRecipes.size());
        for (int i = 0; i < limit; i++) {
            ItemStack icon = availableRecipes.get(i).getOutput().copy();
            icon.stackSize = 1;
            tileEntity.setInventorySlotContents(6 + i, icon);
        }
    }

    private void updateOutputSlot() {
        if (selectedRecipeIndex >= 0 && selectedRecipeIndex < availableRecipes.size()) {
            currentRecipe = availableRecipes.get(selectedRecipeIndex);
            tileEntity.setInventorySlotContents(15, currentRecipe.getOutput());
        } else {
            currentRecipe = null;
            tileEntity.setInventorySlotContents(15, null);
        }
        tileEntity.markDirty();
    }

    public List<SewingRecipe> getAvailableRecipes() { return availableRecipes; }
    public int getSelectedRecipeIndex() { return selectedRecipeIndex; }

    public void setSelectedRecipeIndex(int index) {
        if (index >= 0 && index < availableRecipes.size()) {
            selectedRecipeIndex = index;
            currentRecipe = availableRecipes.get(selectedRecipeIndex);
            lastSelectedRecipeId = currentRecipe.getRecipeId();
            updateOutputSlot();
            // 强制立即同步输出槽到客户端
            for (Object crafter : this.crafters) {
                if (crafter instanceof net.minecraft.entity.player.EntityPlayerMP) {
                    ((net.minecraft.entity.player.EntityPlayerMP) crafter).sendSlotContents(this, 15, tileEntity.getStackInSlot(15));
                }
            }
        }
    }

    @Override
    public boolean enchantItem(EntityPlayer player, int id) {
        System.out.println("enchantItem called: id=" + id + ", availableRecipes.size=" + availableRecipes.size());
        if (id >= 0 && id < availableRecipes.size()) {
            setSelectedRecipeIndex(id);
            return true;
        }
        return false;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(slotIndex);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (slotIndex == 15) {
                if (!this.mergeItemStack(itemstack1, 16, 52, true)) return null;
                slot.onSlotChange(itemstack1, itemstack);
            } else if (slotIndex >= 0 && slotIndex < 6) {
                if (!this.mergeItemStack(itemstack1, 16, 52, false)) return null;
            } else {
                if (!this.mergeItemStack(itemstack1, 0, 1, false) &&
                    !this.mergeItemStack(itemstack1, 1, 2, false) &&
                    !this.mergeItemStack(itemstack1, 2, 6, false)) return null;
            }
            if (itemstack1.stackSize == 0) slot.putStack(null);
            else slot.onSlotChanged();
            if (itemstack1.stackSize == itemstack.stackSize) return null;
            slot.onPickupFromSlot(player, itemstack1);
        }
        return itemstack;
    }

    @Override
    public void detectAndSendChanges() {
        this.onCraftMatrixChanged(tileEntity);
        super.detectAndSendChanges();
        // 强制立即同步所有图案槽和输出槽
        for (int i = 6; i <= 15; i++) {
            ItemStack stack = tileEntity.getStackInSlot(i);
            for (Object crafter : this.crafters) {
                if (crafter instanceof net.minecraft.entity.player.EntityPlayerMP) {
                    ((net.minecraft.entity.player.EntityPlayerMP) crafter).sendSlotContents(this, i, stack);
                }
            }
        }
    }

    // 只读图案槽
    public static class SlotPattern extends Slot {
        public SlotPattern(IInventory inv, int index, int x, int y) {
            super(inv, index, x, y);
        }
        @Override public boolean isItemValid(ItemStack stack) { return false; }
        @Override public boolean canTakeStack(EntityPlayer player) { return false; }
    }

    // 输出槽
    public static class SlotOutput extends Slot {
        public SlotOutput(IInventory inv, int index, int x, int y) {
            super(inv, index, x, y);
        }
        @Override public boolean isItemValid(ItemStack stack) { return false; }

        @Override
        public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
            TileEntitySewingTable te = (TileEntitySewingTable) inventory;
            ContainerSewingTable container = (ContainerSewingTable) player.openContainer;
            SewingRecipe recipe = container.currentRecipe;
            if (recipe != null) {
                boolean consumed = false;
                ItemStack tool = te.getStackInSlot(0);
                String requiredOre = recipe.getToolOreDict();
                ItemStack requiredItem = recipe.getTool();

                if (tool != null) {
                    if (requiredOre != null) {
                        int[] ids = OreDictionary.getOreIDs(tool);
                        for (int id : ids) {
                            if (OreDictionary.getOreName(id).equals(requiredOre)) {
                                tool.damageItem(1, player);
                                if (tool.stackSize == 0) te.setInventorySlotContents(0, null);
                                consumed = true;
                                break;
                            }
                        }
                    } else if (requiredItem != null && SewingRecipe.areStacksEqual(requiredItem, tool)) {
                        tool.damageItem(1, player);
                        if (tool.stackSize == 0) te.setInventorySlotContents(0, null);
                        consumed = true;
                    }
                }
                if (!consumed) {
                    tool = te.getStackInSlot(1);
                    if (tool != null) {
                        if (requiredOre != null) {
                            int[] ids = OreDictionary.getOreIDs(tool);
                            for (int id : ids) {
                                if (OreDictionary.getOreName(id).equals(requiredOre)) {
                                    tool.damageItem(1, player);
                                    if (tool.stackSize == 0) te.setInventorySlotContents(1, null);
                                    break;
                                }
                            }
                        } else if (requiredItem != null && SewingRecipe.areStacksEqual(requiredItem, tool)) {
                            tool.damageItem(1, player);
                            if (tool.stackSize == 0) te.setInventorySlotContents(1, null);
                        }
                    }
                }

                for (SewingRecipe.Material mat : recipe.getMaterials()) {
                    int remaining = mat.count;
                    for (int i = 2; i <= 5; i++) {
                        ItemStack slotStack = te.getStackInSlot(i);
                        if (slotStack != null && SewingRecipe.areStacksEqual(mat.ingredient, slotStack)) {
                            int toTake = Math.min(remaining, slotStack.stackSize);
                            te.decrStackSize(i, toTake);
                            remaining -= toTake;
                            if (remaining <= 0) break;
                        }
                    }
                }
                te.setInventorySlotContents(15, null);
                container.onCraftMatrixChanged(te);
            }
            super.onPickupFromSlot(player, stack);
            if (container.currentRecipe != null) {
                container.lastSelectedRecipeId = container.currentRecipe.getRecipeId();
            }
            container.onCraftMatrixChanged(te);
        }
    }
}
