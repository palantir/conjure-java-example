/*
 * (c) Copyright 2024 Palantir Technologies Inc. All rights reserved.
 */

package com.palantir.conjure.examples;

import com.google.common.collect.ImmutableSet;
import com.palantir.conjure.examples.recipe.api.BakeStep;
import com.palantir.conjure.examples.recipe.api.Ingredient;
import com.palantir.conjure.examples.recipe.api.Recipe;
import com.palantir.conjure.examples.recipe.api.RecipeName;
import com.palantir.conjure.examples.recipe.api.RecipeStep;
import com.palantir.conjure.examples.recipe.api.Temperature;
import com.palantir.conjure.examples.recipe.api.TemperatureUnit;
import java.util.Set;

public final class ExampleRecipes {
    public static Set<Recipe> getCommonRecipes() {
        return ImmutableSet.of(
                Recipe.builder()
                        .name(RecipeName.of("roasted broccoli with garlic"))
                        .steps(ImmutableSet.of(
                                RecipeStep.chop(Ingredient.valueOf("3 cloves of garlic")),
                                RecipeStep.mix(ImmutableSet.of(
                                        Ingredient.of("2 tbsp extra olive oil"), Ingredient.of("chopped garlic"))),
                                RecipeStep.bake(BakeStep.of(Temperature.of(230, TemperatureUnit.CELSIUS), 1200))))
                        .build(),
                Recipe.builder()
                        .name(RecipeName.of("baked potatoes"))
                        .steps(ImmutableSet.of(
                                RecipeStep.mix(ImmutableSet.of(
                                        Ingredient.of("rub oil all over the potatoes"),
                                        Ingredient.of("Rub salt all over the potatoes"))),
                                RecipeStep.bake(BakeStep.of(Temperature.of(220, TemperatureUnit.CELSIUS), 2700))))
                        .build());
    }

    private ExampleRecipes() {}
}
