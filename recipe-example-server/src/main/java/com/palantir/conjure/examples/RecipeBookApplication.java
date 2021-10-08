/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.palantir.conjure.examples;

import com.google.common.collect.ImmutableSet;
import com.palantir.conjure.examples.recipe.api.BakeStep;
import com.palantir.conjure.examples.recipe.api.Ingredient;
import com.palantir.conjure.examples.recipe.api.Recipe;
import com.palantir.conjure.examples.recipe.api.RecipeBookServiceEndpoints;
import com.palantir.conjure.examples.recipe.api.RecipeName;
import com.palantir.conjure.examples.recipe.api.RecipeStep;
import com.palantir.conjure.examples.recipe.api.Temperature;
import com.palantir.conjure.examples.recipe.api.TemperatureUnit;
import com.palantir.conjure.examples.resources.RecipeBookResource;
import com.palantir.conjure.java.undertow.runtime.ConjureHandler;
import io.undertow.Handlers;
import io.undertow.Undertow;
import java.util.Set;

public final class RecipeBookApplication {

    private RecipeBookApplication() {
        // noop
    }

    public static void main(String[] _args) {
        Undertow server = Undertow.builder()
                .addHttpListener(8000, "0.0.0.0")
                .setHandler(Handlers.path()
                        .addPrefixPath(
                                "api/",
                                ConjureHandler.builder()
                                        .services(RecipeBookServiceEndpoints.of(new RecipeBookResource(someRecipes())))
                                        .build()))
                .build();
        server.start();
    }

    private static Set<Recipe> someRecipes() {
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
}
