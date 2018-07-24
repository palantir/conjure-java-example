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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import com.google.common.io.Resources;
import com.palantir.conjure.examples.recipes.api.Recipe;
import com.palantir.conjure.examples.recipes.api.RecipeBookService;
import com.palantir.conjure.examples.recipes.api.RecipeName;
import feign.Client;
import feign.Feign;
import feign.FeignException;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.jaxrs.JAXRSContract;
import io.dropwizard.testing.junit.DropwizardAppRule;
import java.util.Set;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

public class RecipeBookApplicationTest {

    @ClassRule
    public static final DropwizardAppRule<RecipeBookConfiguration> RULE =
            new DropwizardAppRule<>(RecipeBookApplication.class, Resources.getResource("test.yml").getPath());

    private static RecipeBookService client;

    @BeforeClass
    public static void before() {
        client = Feign.builder()
                .contract(new JAXRSContract())
                .client(new Client.Default(null, null))
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(RecipeBookService.class,
                        String.format("http://localhost:%d/examples/api/", RULE.getLocalPort()));
    }

    @Test
    public void getRecipeUsingInvalidName() {
        assertThatThrownBy(() -> client.getRecipe(RecipeName.of("doesNotExist")))
                .isInstanceOf(FeignException.class)
                .hasMessageContaining("\"errorCode\":\"NOT_FOUND\",\"errorName\":\"Recipe:RecipeNotFound\"");
    }

    @Test
    public void getRecipe() {
        RecipeName recipeName = RecipeName.of("roasted broccoli with garlic");
        Recipe recipe = client.getRecipe(recipeName);
        Recipe expectedRecipe = RULE.getConfiguration().getRecipes().stream()
                .filter(r -> r.getName().equals(recipeName)).findFirst().get();
        assertEquals(expectedRecipe, recipe);

        Set<Recipe> recipes = client.getAllRecipes();
        assertEquals(RULE.getConfiguration().getRecipes(), recipes);
    }
}
