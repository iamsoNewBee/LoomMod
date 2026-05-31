package com.CatFish.loommod.renderer;

import com.CatFish.loommod.blocks.loom.BlockLoom;
import com.CatFish.loommod.tileentity.TileEntityLoom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class TileEntityLoomRenderer extends TileEntitySpecialRenderer {
    private final BlockLoom blockLoom;

    public TileEntityLoomRenderer(BlockLoom block) {
        this.blockLoom = block;
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (!(te instanceof TileEntityLoom)) return;
        TileEntityLoom loom = (TileEntityLoom) te;
        World world = te.getWorldObj();
        int meta = world.getBlockMetadata(te.xCoord, te.yCoord, te.zCoord);
        int progress = loom.getProgress();

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);

        int light = world.getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord, 0);
        tessellator.setBrightness(light);

        for (int side = 0; side < 6; side++) {
            IIcon icon = blockLoom.getIcon(world, te.xCoord, te.yCoord, te.zCoord, side);
            if (icon == null) continue;

            switch (side) {
                case 0: tessellator.setNormal(0, -1, 0); break;
                case 1: tessellator.setNormal(0, 1, 0); break;
                case 2: tessellator.setNormal(0, 0, -1); break;
                case 3: tessellator.setNormal(0, 0, 1); break;
                case 4: tessellator.setNormal(-1, 0, 0); break;
                case 5: tessellator.setNormal(1, 0, 0); break;
            }

            double minU = icon.getMinU();
            double maxU = icon.getMaxU();
            double minV = icon.getMinV();
            double maxV = icon.getMaxV();
            drawFace(tessellator, 0, 0, 0, side, meta, minU, maxU, minV, maxV);
        }

        tessellator.draw();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void drawFace(Tessellator tessellator, double x, double y, double z, int side, int meta,
                          double minU, double maxU, double minV, double maxV) {
        double[][] vertices;
        double[][] uv;

        // 默认UV（逆时针顺序：左下、右下、右上、左上）
        double[][] defaultUV = {{minU, minV}, {maxU, minV}, {maxU, maxV}, {minU, maxV}};
        uv = defaultUV;

        // 顶面根据方块朝向旋转UV（顺时针旋转）
        if (side == 1) {
            switch (meta) {
                case 0: // 北
                    uv = defaultUV;
                    break;
                case 1: // 东（顺时针90度 → 旋转180度 → 水平翻转）
                    uv = new double[][]{{maxU, minV}, {maxU, maxV}, {minU, maxV}, {minU, minV}};
                    // 步骤1：旋转180度
                    uv = new double[][] {uv[2], uv[3], uv[0], uv[1]};
                    // 步骤2：水平翻转（修复纹理方向）
                    for (int i = 0; i < uv.length; i++) {
                        uv[i][0] = maxU - (uv[i][0] - minU);
                    }
                    break;
                case 2: // 南（顺时针180度）
                    uv = new double[][]{{maxU, maxV}, {minU, maxV}, {minU, minV}, {maxU, minV}};
                    break;
                case 3: // 西（顺时针270度 → 旋转180度 → 水平翻转）
                    uv = new double[][]{{minU, maxV}, {minU, minV}, {maxU, minV}, {maxU, maxV}};
                    // 步骤1：旋转180度
                    uv = new double[][] {uv[2], uv[3], uv[0], uv[1]};
                    // 步骤2：水平翻转（修复纹理方向）
                    for (int i = 0; i < uv.length; i++) {
                        uv[i][0] = maxU - (uv[i][0] - minU);
                    }
                    break;
                default:
                    uv = defaultUV;
            }
        }

        // 判断当前面是否为正面（根据meta）
        int frontSide = getFrontSide(meta);
        if (side == frontSide) {
            // 旋转180度：交换UV顺序 (3,2,1,0) 并相应调整坐标
            uv = new double[][]{
                uv[2], // 原右上 -> 左下
                uv[3], // 原左上 -> 右下
                uv[0], // 原左下 -> 右上
                uv[1]  // 原右下 -> 左上
            };

            // ========== 原有：水平翻转东/南方向的正面材质 ==========
            if (meta == 1 || meta == 2) {
                // 水平翻转：交换U值（minU和maxU互换）
                for (int i = 0; i < uv.length; i++) {
                    double tempU = uv[i][0];
                    uv[i][0] = maxU - (tempU - minU); // 水平翻转公式：newU = maxU - (oldU - minU)
                }
            }
        }

        // 定义顶点坐标（逆时针顺序）
        switch (side) {
            case 0: // 底面
                vertices = new double[][]{
                    {x, y, z}, {x+1, y, z}, {x+1, y, z+1}, {x, y, z+1}
                };
                // 底面垂直翻转UV
                uv = new double[][]{{minU, maxV}, {maxU, maxV}, {maxU, minV}, {minU, minV}};
                break;
            case 1: // 顶面
                vertices = new double[][]{
                    {x, y+1, z}, {x+1, y+1, z}, {x+1, y+1, z+1}, {x, y+1, z+1}
                };
                // 顶面UV已通过meta旋转，无需额外翻转
                break;
            case 2: // 北面
                vertices = new double[][]{
                    {x, y, z}, {x+1, y, z}, {x+1, y+1, z}, {x, y+1, z}
                };
                break;
            case 3: // 南面
                vertices = new double[][]{
                    {x, y, z+1}, {x+1, y, z+1}, {x+1, y+1, z+1}, {x, y+1, z+1}
                };
                break;
            case 4: // 西面
                vertices = new double[][]{
                    {x, y, z}, {x, y, z+1}, {x, y+1, z+1}, {x, y+1, z}
                };
                break;
            case 5: // 东面
                vertices = new double[][]{
                    {x+1, y, z}, {x+1, y, z+1}, {x+1, y+1, z+1}, {x+1, y+1, z}
                };
                break;
            default:
                return;
        }

        // 添加顶点
        for (int i = 0; i < 4; i++) {
            tessellator.addVertexWithUV(vertices[i][0], vertices[i][1], vertices[i][2], uv[i][0], uv[i][1]);
        }
    }

    // 根据meta获取正面方向
    private int getFrontSide(int meta) {
        switch (meta) {
            case 0: return 2; // 北
            case 1: return 5; // 东
            case 2: return 3; // 南
            case 3: return 4; // 西
            default: return 3;
        }
    }
}
