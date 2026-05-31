package com.CatFish.loommod.blocks.sewing;

import com.CatFish.loommod.recipe.SewingRecipe;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiSewingTable extends GuiContainer
{
    private static final ResourceLocation BG_TEXTURE = new ResourceLocation("loommod:textures/gui/sewing_station_new.png");
    private TileEntitySewingTable tileEntity;

    public GuiSewingTable(InventoryPlayer playerInv, TileEntitySewingTable te)
    {
        super(new ContainerSewingTable(playerInv, te));
        this.tileEntity = te;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRendererObj.drawString("Sewing Table", 8, 6, 4210752);
        this.fontRendererObj.drawString("Inventory", 8, this.ySize - 94, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BG_TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }

    private Slot getSlotAtPosition(int mouseX, int mouseY) {
        int guiLeft = (this.width - this.xSize) / 2;
        int guiTop = (this.height - this.ySize) / 2;
        for (int i = 0; i < this.inventorySlots.inventorySlots.size(); i++) {
            Slot slot = (Slot) this.inventorySlots.inventorySlots.get(i);
            int slotX = guiLeft + slot.xDisplayPosition;
            int slotY = guiTop + slot.yDisplayPosition;
            if (mouseX >= slotX && mouseX < slotX + 16 && mouseY >= slotY && mouseY < slotY + 18) {
                return slot;
            }
        }
        return null;
    }

    @Override
    protected void mouseClicked(int x, int y, int button)
    {
        Slot slot = getSlotAtPosition(x, y);
        if (slot != null && slot instanceof ContainerSewingTable.SlotPattern) {
            // 只要图案槽中有物品（由服务端填充），就允许点击
            if (slot.getStack() != null) {
                int slotIndex = slot.slotNumber;
                int recipeIndex = slotIndex - 6; // 图案槽起始索引6
                ContainerSewingTable container = (ContainerSewingTable) this.inventorySlots;
                this.mc.playerController.sendEnchantPacket(container.windowId, recipeIndex);
                return;
            }
        }
        super.mouseClicked(x, y, button);
    }
}
