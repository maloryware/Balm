package net.blay09.mods.balm.api.recipe;

import net.blay09.mods.balm.api.DeferredObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.function.Supplier;

public interface BalmRecipes {
    <T extends Recipe<?>> DeferredObject<RecipeType<T>> registerRecipeType(Supplier<RecipeType<T>> typeSupplier, Supplier<RecipeSerializer<T>> serializerSupplier, ResourceLocation identifier);

    <T extends RecipeDisplay.Type<?>> DeferredObject<T> registerRecipeDisplayType(Supplier<T> supplier, ResourceLocation identifier);

    <T extends SlotDisplay.Type<?>> DeferredObject<T> registerSlotDisplayType(Supplier<T> supplier, ResourceLocation identifier);

    DeferredObject<RecipeBookCategory> registerRecipeBookCategory(Supplier<RecipeBookCategory> supplier, ResourceLocation identifier);
}
