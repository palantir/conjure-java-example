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

package com.palantir.conjure.examples.resources;

import com.palantir.conjure.examples.recipe.api.Recipe;
import com.palantir.conjure.examples.recipe.api.RecipeBookService;
import com.palantir.conjure.examples.recipe.api.RecipeErrors;
import com.palantir.conjure.examples.recipe.api.RecipeName;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class RecipeBookResource implements RecipeBookService {

    private final Map<RecipeName, Recipe> recipes;

    public RecipeBookResource(Set<Recipe> recipes) {
        this.recipes = recipes.stream()
                .collect(Collectors.toConcurrentMap(Recipe::getName, Function.identity()));
    }

    @Override
    public Recipe getRecipe(RecipeName name) {
        com.palantir.logsafe.Preconditions.checkNotNull(name, "Recipe name must be provided.");
        checkIfRecipeExists(name);

        return recipes.get(name);
    }

    @Override
    public void createRecipe(Recipe createRecipeRequest) {
        recipes.put(createRecipeRequest.getName(), createRecipeRequest);
    }

    @Override
    public Set<Recipe> getAllRecipes() {
        return new HashSet<>(recipes.values());
    }

    @Override
    public void deleteRecipe(RecipeName name) {
        checkIfRecipeExists(name);
        recipes.remove(name);
    }

    private void checkIfRecipeExists(RecipeName name) {
        if (!this.recipes.containsKey(name)) {
            throw RecipeErrors.recipeNotFound(name);
        }
    }
}
