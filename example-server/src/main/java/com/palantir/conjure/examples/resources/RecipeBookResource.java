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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.palantir.comnjure.examples.api.RecipeBookService;
import com.palantir.conjure.examples.Recipe;
import com.palantir.conjure.examples.RecipeErrors;
import com.palantir.conjure.examples.RecipeName;
import java.util.List;
import java.util.Optional;

public final class RecipeBookResource implements RecipeBookService {

    private final List<Recipe> recipes;

    public RecipeBookResource(List<Recipe> recipes) {
        this.recipes = ImmutableList.copyOf(recipes);
    }

    @Override
    public Recipe getRecipe(RecipeName name) {
        Preconditions.checkNotNull(name, "Recipe name must be provided.");
        Optional<Recipe> maybeRecipe = this.recipes.stream().filter(r -> r.getName().equals(name)).findAny();
        if (!maybeRecipe.isPresent()) {
            throw RecipeErrors.recipeNotFound(name);
        }

        return maybeRecipe.get();
    }

    @Override
    public List<Recipe> getRecipes() {
        return recipes;
    }
}
