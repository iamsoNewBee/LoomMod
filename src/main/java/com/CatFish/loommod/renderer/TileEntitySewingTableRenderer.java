package com.CatFish.loommod.renderer;

import com.CatFish.loommod.blocks.sewing.TileEntitySewingTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

public class TileEntitySewingTableRenderer extends TileEntitySpecialRenderer
{
    private final RenderItem renderItem;
    private final EntityItem dummyEntityItem;

    public TileEntitySewingTableRenderer()
    {
        renderItem = (RenderItem) RenderManager.instance.getEntityClassRenderObject(EntityItem.class);
        dummyEntityItem = new EntityItem(null);
        dummyEntityItem.hoverStart = 0.0F;
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks)
    {
        if (!(te instanceof TileEntitySewingTable)) return;
        TileEntitySewingTable table = (TileEntitySewingTable) te;

        ItemStack tool1 = table.getStackInSlot(0);
        ItemStack tool2 = table.getStackInSlot(1);

        if (tool1 == null && tool2 == null) return;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        RenderHelper.enableStandardItemLighting();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

        // 移动到方块上方中心
        GL11.glTranslated(x + 0.5, y + 1.0, z );
        GL11.glScalef(1F, 1F, 1F);

        // 渲染第一个工具（左侧），平放
        if (tool1 != null)
        {
            GL11.glPushMatrix();
            GL11.glTranslated(-0.3, 0, 0);
            renderItemFlat(tool1);
            GL11.glPopMatrix();
        }

        // 渲染第二个工具（右侧），平放
        if (tool2 != null)
        {
            GL11.glPushMatrix();
            GL11.glTranslated(0.3, 0, 0);
            renderItemFlat(tool2);
            GL11.glPopMatrix();
        }

        RenderHelper.disableStandardItemLighting();
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private void renderItemFlat(ItemStack stack)
    {
        if (stack == null) return;
        dummyEntityItem.setEntityItemStack(stack);
        GL11.glPushMatrix();
        // 绕 X 轴旋转 90 度，使物品平躺（面朝上）
        GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
        // 适当提高物品位置，使其浮在桌面上方
        GL11.glTranslatef(0.0F, 0.15F, 0.0F);
        // 强制亮度再次确保亮丽
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        renderItem.doRender(dummyEntityItem, 0, 0, 0, 0, 0);
        GL11.glPopMatrix();
    }
}
