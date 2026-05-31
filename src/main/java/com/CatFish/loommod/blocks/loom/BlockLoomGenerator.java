package com.CatFish.loommod.blocks.loom;

import com.CatFish.loommod.LoomMod;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockLoomGenerator extends Block {

    @SideOnly(Side.CLIENT)
    private IIcon[] icons = new IIcon[6]; // 0底,1顶,2北,3南,4西,5东

    public BlockLoomGenerator(Material material) {
        super(material);
        setHardness(2.0f);
        setStepSound(soundTypeStone);
        setBlockName("loomGenerator");
        setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        // 获取玩家朝向（与织布机相同的计算方式）
        int facing = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        // 织布机元数据映射：0=北,1=东,2=南,3=西（与 BlockLoom 的 getRotatedIcon 一致）
        // 玩家 facing: 0=南,1=西,2=北,3=东，因此直接使用 facing 即可得到正确映射
        int meta = facing; // 如果映射需要调整，可在此修改，但当前织布机代码使用恒等映射
        // 替换为织布机，并设置正确的元数据
        world.setBlock(x, y, z, LoomMod.loomBlock, meta, 2);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg) {
        String modId = LoomMod.MODID;
        icons[0] = reg.registerIcon(modId + ":loom_bottom");
        icons[1] = reg.registerIcon(modId + ":loom_top_loomA");
        icons[2] = reg.registerIcon(modId + ":loom_side");
        icons[3] = reg.registerIcon(modId + ":loom_front_loomA");
        icons[4] = reg.registerIcon(modId + ":loom_side");
        icons[5] = reg.registerIcon(modId + ":loom_side");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if (side == 0) return icons[0];
        if (side == 1) return icons[1];
        // 正面（南面）固定为 side=3，其他侧面用 side=2 的纹理
        return (side == 3) ? icons[3] : icons[2];
    }

    @Override
    public boolean renderAsNormalBlock() {
        return true;
    }

    @Override
    public int getRenderType() {
        return 0; // 1.7.10 普通方块渲染 ID 为 0
    }

    @Override
    public boolean isOpaqueCube() {
        return true;
    }

    // 生成器方块不需要 TileEntity，因此移除 hasTileEntity 和 createTileEntity

    // 自定义 ItemBlock，用于物品栏渲染
    public static class ItemBlockLoomGenerator extends ItemBlock {
        public ItemBlockLoomGenerator(Block block) {
            super(block);
            setHasSubtypes(false);
            setMaxDamage(0);
            setUnlocalizedName("loomGenerator");
        }

        @SideOnly(Side.CLIENT)
        @Override
        public IIcon getIconFromDamage(int meta) {
            // 物品栏显示北面（side=2），也可以改为南面（side=3）或其他
            return field_150939_a.getIcon(2, 0);
        }
    }
}
