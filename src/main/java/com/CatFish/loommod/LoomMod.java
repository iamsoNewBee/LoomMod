package com.CatFish.loommod;

import com.CatFish.loommod.blocks.sewing.ContainerSewingTable;
import com.CatFish.loommod.blocks.sewing.GuiSewingTable;
import com.CatFish.loommod.blocks.sewing.SewingTableBlock;
import com.CatFish.loommod.blocks.sewing.TileEntitySewingTable;
import com.CatFish.loommod.items.armor.WoolArmorItem;
import com.CatFish.loommod.items.needle.NeedleItem;
import com.CatFish.loommod.items.needle.Needles;
import com.CatFish.loommod.nei.SewingTableRecipeHandler;
import com.CatFish.loommod.recipe.LoomRecipe;
import com.CatFish.loommod.recipe.SewingRecipe;
import com.CatFish.loommod.recipe.SewingRecipeManager;
import com.CatFish.loommod.blocks.loom.BlockLoomGenerator;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.OreDictionary;

import com.CatFish.loommod.blocks.loom.BlockLoom;
import com.CatFish.loommod.items.*;
import com.CatFish.loommod.recipe.LoomRecipeManager;
import com.CatFish.loommod.tileentity.TileEntityLoom;

import codechicken.nei.api.API;
import com.CatFish.loommod.nei.SewingTableRecipeHandler;
import com.CatFish.loommod.nei.SewingTableOverlayHandler;

import java.util.Arrays;

@Mod(modid = LoomMod.MODID, version = LoomMod.VERSION, name = LoomMod.NAME)
public class LoomMod {

    public static final String MODID = "loommod";
    public static final String VERSION = "1.0";
    public static final String NAME = "Loom Mod";

    public static Block sewingTable;

    public static Item leatherSheet;
    public static Item leatherStrip;
    public static Item woolFabric;
    public static Item woolStrip;

    public static Item woodNeedle;
    public static Item stoneNeedle;
    public static Item ironNeedle;
    public static Item diamondNeedle;
    public static Item goldNeedle;
    public static Item boneNeedle;

    public static Block loomBlock;
    public static Block loomGenerator;

    // 物品
    public static Item flaxRope;
    public static Item woolLoop;
    public static Item hempLoop;
    public static Item grassLoop;
    public static Item grassRope;
    public static Item grassString;
    public static Item flintKnife;
    public static Item grassFabric;

    public static LoomMod instance;

    public static ItemArmor.ArmorMaterial WOOL_ARMOR;

    public static Item woolHelmet;
    public static Item woolChestplate;
    public static Item woolLeggings;
    public static Item woolBoots;

    public static LoomRecipeManager recipeManager = new LoomRecipeManager();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (Loader.isModLoaded("MineTweaker3")) {
            try {
                Class.forName("com.CatFish.loommod.minetweaker.MtProxy")
                    .getMethod("init")
                    .invoke(null);
                System.out.println("[LoomMod] MineTweaker integration loaded (preInit).");
            } catch (Exception e) {
                System.err.println("[LoomMod] Failed to load MineTweaker integration: " + e.getMessage());
            }
        }

        instance = this;

        WOOL_ARMOR = EnumHelper.addArmorMaterial("WOOL", 5, new int[]{1, 2, 2, 1}, 15);

        // 注册羊毛盔甲
        woolHelmet = new WoolArmorItem(WOOL_ARMOR, 0, 0, "wool")
            .setUnlocalizedName("woolHelmet")
            .setTextureName("loommod:wool_helmet");
        GameRegistry.registerItem(woolHelmet, "woolHelmet");

        woolChestplate = new WoolArmorItem(WOOL_ARMOR, 0, 1, "wool")
            .setUnlocalizedName("woolChestplate")
            .setTextureName("loommod:wool_chestplate");
        GameRegistry.registerItem(woolChestplate, "woolChestplate");

        woolLeggings = new WoolArmorItem(WOOL_ARMOR, 0, 2, "wool")
            .setUnlocalizedName("woolLeggings")
            .setTextureName("loommod:wool_leggings");
        GameRegistry.registerItem(woolLeggings, "woolLeggings");

        woolBoots = new WoolArmorItem(WOOL_ARMOR, 0, 3, "wool")
            .setUnlocalizedName("woolBoots")
            .setTextureName("loommod:wool_boots");
        GameRegistry.registerItem(woolBoots, "woolBoots");

        // 注册针
        woodNeedle = new NeedleItem(Needles.WOOD)
            .setUnlocalizedName("woodNeedle")
            .setTextureName("loommod:wood_needle")
            .setCreativeTab(CreativeTabs.tabTools);
        GameRegistry.registerItem(woodNeedle, "woodNeedle");

        stoneNeedle = new NeedleItem(Needles.STONE)
            .setUnlocalizedName("stoneNeedle")
            .setTextureName("loommod:stone_needle")
            .setCreativeTab(CreativeTabs.tabTools);
        GameRegistry.registerItem(stoneNeedle, "stoneNeedle");

        ironNeedle = new NeedleItem(Needles.IRON)
            .setUnlocalizedName("ironNeedle")
            .setTextureName("loommod:iron_needle")
            .setCreativeTab(CreativeTabs.tabTools);
        GameRegistry.registerItem(ironNeedle, "ironNeedle");

        diamondNeedle = new NeedleItem(Needles.DIAMOND)
            .setUnlocalizedName("diamondNeedle")
            .setTextureName("loommod:diamond_needle")
            .setCreativeTab(CreativeTabs.tabTools);
        GameRegistry.registerItem(diamondNeedle, "diamondNeedle");

        goldNeedle = new NeedleItem(Needles.GOLD)
            .setUnlocalizedName("goldNeedle")
            .setTextureName("loommod:gold_needle")
            .setCreativeTab(CreativeTabs.tabTools);
        GameRegistry.registerItem(goldNeedle, "goldNeedle");

        boneNeedle = new NeedleItem(Needles.BONE)
            .setUnlocalizedName("boneNeedle")
            .setTextureName("loommod:bone_needle")
            .setCreativeTab(CreativeTabs.tabTools);
        GameRegistry.registerItem(boneNeedle, "boneNeedle");

        // 注册缝纫材料
        leatherSheet = new Item()
            .setUnlocalizedName("leatherSheet")
            .setTextureName("loommod:leather_sheet")
            .setCreativeTab(CreativeTabs.tabMaterials);
        GameRegistry.registerItem(leatherSheet, "leatherSheet");

        leatherStrip = new Item()
            .setUnlocalizedName("leatherStrip")
            .setTextureName("loommod:leather_strip")
            .setCreativeTab(CreativeTabs.tabMaterials);
        GameRegistry.registerItem(leatherStrip, "leatherStrip");

        // 注册缝纫台方块
        sewingTable = new SewingTableBlock(Material.wood).setBlockName("sewingTable");
        GameRegistry.registerBlock(sewingTable, "sewingTable");

        // 织布机
        loomGenerator = new BlockLoomGenerator(Material.wood);
        GameRegistry.registerBlock(loomGenerator, BlockLoomGenerator.ItemBlockLoomGenerator.class, "loomGenerator");

        loomBlock = new BlockLoom(Material.wood).setBlockName("loom")
            .setBlockTextureName(MODID + ":loom");
        GameRegistry.registerBlock(loomBlock, "loom");

        // 注册其他物品
        flaxRope = new ItemBase().setUnlocalizedName("flaxRope")
            .setTextureName(MODID + ":flaxRope");
        woolLoop = new ItemCoil(2).setUnlocalizedName("woolLoop")
            .setTextureName(MODID + ":woolLoop");
        hempLoop = new ItemCoil(2).setUnlocalizedName("hempLoop")
            .setTextureName(MODID + ":hempLoop");
        grassLoop = new ItemCoil(2).setUnlocalizedName("grassLoop")
            .setTextureName(MODID + ":grassLoop");
        grassRope = new ItemBase().setUnlocalizedName("grassRope")
            .setTextureName(MODID + ":grassRope");
        grassString = new ItemBase().setUnlocalizedName("grassString")
            .setTextureName(MODID + ":grassString");
        flintKnife = new ItemFlintKnife().setUnlocalizedName("flintKnife")
            .setTextureName(MODID + ":flintKnife");
        grassFabric = new ItemBase().setUnlocalizedName("grassFabric")
            .setTextureName(MODID + ":grassFabric");

        woolFabric = new ItemBase()
            .setUnlocalizedName("woolFabric")
            .setTextureName(MODID + ":wool_fabric");
        GameRegistry.registerItem(woolFabric, "woolFabric");

        woolStrip = new ItemBase()
            .setUnlocalizedName("woolStrip")
            .setTextureName(MODID + ":wool_strip");
        GameRegistry.registerItem(woolStrip, "woolStrip");

        GameRegistry.registerItem(flaxRope, "flaxRope");
        GameRegistry.registerItem(woolLoop, "woolLoop");
        GameRegistry.registerItem(hempLoop, "hempLoop");
        GameRegistry.registerItem(grassLoop, "grassLoop");
        GameRegistry.registerItem(grassRope, "grassRope");
        GameRegistry.registerItem(grassString, "grassString");
        GameRegistry.registerItem(flintKnife, "flintKnife");
        GameRegistry.registerItem(grassFabric, "grassFabric");

        // 注册针矿辞
        OreDictionary.registerOre("sewingNeedle", woodNeedle);
        OreDictionary.registerOre("sewingNeedle", stoneNeedle);
        OreDictionary.registerOre("sewingNeedle", ironNeedle);
        OreDictionary.registerOre("sewingNeedle", diamondNeedle);
        OreDictionary.registerOre("sewingNeedle", goldNeedle);
        OreDictionary.registerOre("sewingNeedle", boneNeedle);

        // 注册剪刀矿辞
        OreDictionary.registerOre("sewingShears", Items.shears);

        // 注册矿物词典（线、绳等，供其他模组使用）
        OreDictionary.registerOre("string", Items.string);
        //OreDictionary.registerOre("string", grassString);
        OreDictionary.registerOre("string", grassRope);
        OreDictionary.registerOre("string", flaxRope);
        OreDictionary.registerOre("rope", grassRope);
        OreDictionary.registerOre("rope", flaxRope);
        OreDictionary.registerOre("yarn", woolLoop);
        OreDictionary.registerOre("yarn", hempLoop);

        // 注册 TileEntity
        GameRegistry.registerTileEntity(TileEntityLoom.class, "tileEntityLoom");
        if (event.getSide().isClient()) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLoom.class, new com.CatFish.loommod.renderer.TileEntityLoomRenderer((BlockLoom) loomBlock));
        }
        GameRegistry.registerTileEntity(TileEntitySewingTable.class, "sewingTable");
        if (event.getSide().isClient()) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySewingTable.class, new com.CatFish.loommod.renderer.TileEntitySewingTableRenderer());
        }
    }

    public static class GuiHandler implements IGuiHandler {
        @Override
        public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
            if (ID == 0) {
                TileEntity te = world.getTileEntity(x, y, z);
                if (te instanceof TileEntitySewingTable)
                    return new ContainerSewingTable(player.inventory, (TileEntitySewingTable) te);
            }
            return null;
        }

        @Override
        public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
            if (ID == 0) {
                TileEntity te = world.getTileEntity(x, y, z);
                if (te instanceof TileEntitySewingTable)
                    return new GuiSewingTable(player.inventory, (TileEntitySewingTable) te);
            }
            return null;
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        SewingRecipeManager sewingManager = SewingRecipeManager.getInstance();

        // ========== 缝纫台配方 ==========
        // 皮革片（使用剪刀矿辞）
        sewingManager.addRecipe(new SewingRecipe(
            Arrays.asList(new SewingRecipe.Material(new ItemStack(Items.leather), 1)),
            "sewingShears",
            null,
            new ItemStack(leatherSheet, 4)
        ));
        // 皮革条
        sewingManager.addRecipe(new SewingRecipe(
            Arrays.asList(new SewingRecipe.Material(new ItemStack(Items.leather), 1)),
            "sewingShears",
            null,
            new ItemStack(leatherStrip, 3)
        ));

        // 皮革护甲（使用针矿辞，线材使用矿辞 "string"）
        // 靴子
        sewingManager.addRecipe(new SewingRecipe(
            Arrays.asList(
                new SewingRecipe.Material(new ItemStack(leatherSheet), 2),
                new SewingRecipe.Material(new ItemStack(leatherStrip), 1),
                new SewingRecipe.Material("string", 1)   // 矿辞线
            ),
            "sewingNeedle",
            null,
            new ItemStack(Items.leather_boots)
        ));
        // 头盔
        sewingManager.addRecipe(new SewingRecipe(
            Arrays.asList(
                new SewingRecipe.Material(new ItemStack(leatherSheet), 2),
                new SewingRecipe.Material(new ItemStack(leatherStrip), 1),
                new SewingRecipe.Material("string", 1)
            ),
            "sewingNeedle",
            null,
            new ItemStack(Items.leather_helmet)
        ));
        // 护腿
        sewingManager.addRecipe(new SewingRecipe(
            Arrays.asList(
                new SewingRecipe.Material(new ItemStack(leatherSheet), 4),
                new SewingRecipe.Material(new ItemStack(leatherStrip), 2),
                new SewingRecipe.Material("string", 1)
            ),
            "sewingNeedle",
            null,
            new ItemStack(Items.leather_leggings)
        ));
        // 胸甲
        sewingManager.addRecipe(new SewingRecipe(
            Arrays.asList(
                new SewingRecipe.Material(new ItemStack(leatherSheet), 6),
                new SewingRecipe.Material(new ItemStack(leatherStrip), 2),
                new SewingRecipe.Material("string", 2)
            ),
            "sewingNeedle",
            null,
            new ItemStack(Items.leather_chestplate)
        ));

        System.out.println("[LoomMod] Recipes registered.");

        if (Loader.isModLoaded("NotEnoughItems")) {
            try {
                SewingTableRecipeHandler handler = new SewingTableRecipeHandler();
                codechicken.nei.api.API.registerRecipeHandler(handler);
                codechicken.nei.api.API.registerUsageHandler(handler);
                codechicken.nei.api.API.registerGuiOverlay(GuiSewingTable.class, "sewingTable");
                codechicken.nei.api.API.registerGuiOverlayHandler(GuiSewingTable.class, new SewingTableOverlayHandler(), "sewingTable");
                System.out.println("[LoomMod] NEI integration registered.");
            } catch (Throwable t) {
                System.err.println("[LoomMod] NEI integration failed: " + t);
            }
        }
    }
}
