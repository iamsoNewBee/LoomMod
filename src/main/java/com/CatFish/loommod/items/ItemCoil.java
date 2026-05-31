package com.CatFish.loommod.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemCoil extends Item {

    public ItemCoil(int maxDamage) {
        setMaxDamage(maxDamage);
        setMaxStackSize(1);
        setNoRepair();
        setCreativeTab(CreativeTabs.tabMisc);
    }
}
