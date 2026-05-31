package com.CatFish.loommod.minetweaker;

import minetweaker.MineTweakerAPI;

public class MtProxy {

    public static void init() {
        // 向 MineTweaker 注册扩展类（自动暴露所有 public static 方法）
        MineTweakerAPI.registerClass(MtLoomRecipes.class);
        MineTweakerAPI.registerClass(MtLoomRecipes.class);
        MineTweakerAPI.registerClass(MtSewingRecipes.class);
    }
}
