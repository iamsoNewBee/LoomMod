package com.CatFish.loommod.items.armor;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WoolArmorItem extends ItemArmor {
    private final String baseTextureName;

    public WoolArmorItem(ArmorMaterial material, int renderIndex, int armorType, String baseTextureName) {
        super(material, renderIndex, armorType);
        this.baseTextureName = baseTextureName;
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.tabCombat);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        // 护腿使用 layer_2，其他使用 layer_1
        boolean isLeggings = armorType == 2;
        String layer = isLeggings ? "layer_2" : "layer_1";

        // 染色层使用 overlay 纹理
        if (type != null && type.equals("overlay")) {
            return "loommod:textures/models/armor/" + baseTextureName + "_" + layer + "_overlay.png";
        }
        return "loommod:textures/models/armor/" + baseTextureName + "_" + layer + ".png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        // 物品栏图标（无需染色层，仅基础纹理）
        this.itemIcon = reg.registerIcon("loommod:" + baseTextureName + "_" + getArmorTypeName());
    }

    private String getArmorTypeName() {
        switch (armorType) {
            case 0: return "helmet";
            case 1: return "chestplate";
            case 2: return "leggings";
            case 3: return "boots";
            default: return "helmet";
        }
    }

    // ---------- 染色支持 ----------
    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        // renderPass 0 为基础层，返回白色；renderPass 1 为叠加层，返回存储的颜色
        if (renderPass == 1) {
            return getColor(stack);
        }
        return 0xFFFFFF;
    }

    // 获取颜色（默认白色）
    public int getColor(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound nbttagcompound = stack.getTagCompound();
            if (nbttagcompound.hasKey("display", 10)) {
                NBTTagCompound display = nbttagcompound.getCompoundTag("display");
                if (display.hasKey("color", 3)) {
                    return display.getInteger("color");
                }
            }
        }
        return 0xFFFFFF;
    }

    // 设置颜色
    public void setColor(ItemStack stack, int color) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound nbttagcompound = stack.getTagCompound();
        if (!nbttagcompound.hasKey("display", 10))
            nbttagcompound.setTag("display", new NBTTagCompound());
        nbttagcompound.getCompoundTag("display").setInteger("color", color);
    }

    // 移除颜色
    public void removeColor(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound nbttagcompound = stack.getTagCompound();
            if (nbttagcompound.hasKey("display")) {
                nbttagcompound.getCompoundTag("display").removeTag("color");
            }
        }
    }
}
