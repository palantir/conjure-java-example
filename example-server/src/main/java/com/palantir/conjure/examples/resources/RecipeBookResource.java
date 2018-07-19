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

import com.palantir.comnjure.examples.api.RecipeBookService;
import com.palantir.conjure.examples.Recipe;
import com.palantir.conjure.examples.RecipeName;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class RecipeBookResource implements RecipeBookService {

    private final Map<RecipeName, Recipe> recipeMap;

    public RecipeBookResource(List<Recipe> recipes) {
        this.recipeMap = recipes.stream()
                .collect(Collectors.toMap(Recipe::getName, Function.identity()));
    }

    @Override
    public Optional<Recipe> getRecipe(RecipeName name) {
        return Optional.ofNullable(this.recipeMap.get(name));
    }
}
