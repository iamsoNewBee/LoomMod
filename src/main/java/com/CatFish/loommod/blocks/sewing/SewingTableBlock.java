package com.CatFish.loommod.blocks.sewing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import com.CatFish.loommod.LoomMod;


public class SewingTableBlock extends BlockContainer
{
    @SideOnly(Side.CLIENT)
    protected IIcon[] icons = new IIcon[5]; // 0=bottom,1=top,2=side,3=front,4=back

    public SewingTableBlock(Material material)
    {
        super(material);
        this.setHardness(2.5F);
        this.setStepSound(soundTypeWood);
        this.setBlockName("sewingTable");
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntitySewingTable();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (world.isRemote) return true;
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntitySewingTable)
        {
            player.openGui(LoomMod.instance, 0, world, x, y, z); // 需要注册 GUI
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack)
    {
        int facing = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        int[] mapping = {2, 3, 0, 1};
        int meta = mapping[facing];
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg)
    {
        icons[0] = reg.registerIcon("loommod:sewing_table_bottom");
        icons[1] = reg.registerIcon("loommod:sewing_table_top");
        icons[2] = reg.registerIcon("loommod:sewing_table_side");
        icons[3] = reg.registerIcon("loommod:sewing_table_side");
        icons[4] = reg.registerIcon("loommod:sewing_table_side");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        int frontSide;
        switch (meta & 3)
        {
            case 0: frontSide = 2; break;
            case 1: frontSide = 5; break;
            case 2: frontSide = 3; break;
            case 3: frontSide = 4; break;
            default: frontSide = 3;
        }
        int backSide;
        switch (frontSide)
        {
            case 2: backSide = 3; break;
            case 3: backSide = 2; break;
            case 4: backSide = 5; break;
            case 5: backSide = 4; break;
            default: backSide = 3;
        }
        if (side == 0) return icons[0];
        if (side == 1) return icons[1];
        if (side == frontSide) return icons[3];
        if (side == backSide) return icons[4];
        return icons[2];
    }

    @Override
    public boolean renderAsNormalBlock() { return true; }
    @Override
    public int getRenderType() { return 0; }
    @Override
    public boolean isOpaqueCube() { return true; }
}
