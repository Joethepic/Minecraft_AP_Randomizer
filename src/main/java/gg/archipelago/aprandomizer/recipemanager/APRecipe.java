package gg.archipelago.aprandomizer.recipemanager;

import net.minecraft.world.item.crafting.Recipe;

import java.util.Set;

public interface APRecipe {

    Set<Recipe<?>> getGrantedRecipes();

}
