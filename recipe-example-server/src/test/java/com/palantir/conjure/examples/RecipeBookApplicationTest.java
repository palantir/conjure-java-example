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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.palantir.conjure.examples.recipe.Configuration;
import com.palantir.conjure.examples.recipe.api.BakeStep;
import com.palantir.conjure.examples.recipe.api.Ingredient;
import com.palantir.conjure.examples.recipe.api.Recipe;
import com.palantir.conjure.examples.recipe.api.RecipeBookService;
import com.palantir.conjure.examples.recipe.api.RecipeErrors;
import com.palantir.conjure.examples.recipe.api.RecipeName;
import com.palantir.conjure.examples.recipe.api.RecipeStep;
import com.palantir.conjure.examples.recipe.api.Temperature;
import com.palantir.conjure.examples.recipe.api.TemperatureUnit;
import com.palantir.conjure.java.api.config.service.UserAgent;
import com.palantir.conjure.java.api.config.ssl.SslConfiguration;
import com.palantir.conjure.java.api.testing.Assertions;
import com.palantir.conjure.java.client.config.ClientConfiguration;
import com.palantir.conjure.java.client.config.ClientConfigurations;
import com.palantir.conjure.java.client.jaxrs.JaxRsClient;
import com.palantir.conjure.java.config.ssl.SslSocketFactories;
import com.palantir.conjure.java.okhttp.NoOpHostEventsSink;
import io.undertow.Undertow;
import java.nio.file.Paths;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class RecipeBookApplicationTest {
    private static final Configuration config = Configuration.of(8345, "localhost");

    private static final SslConfiguration TRUST_STORE_CONFIGURATION = new SslConfiguration.Builder()
            .trustStorePath(Paths.get("src/test/resources/certs/truststore.jks"))
            .build();
    private static final SSLSocketFactory SSL_SOCKET_FACTORY =
            SslSocketFactories.createSslSocketFactory(TRUST_STORE_CONFIGURATION);
    private static final X509TrustManager TRUST_MANAGER =
            SslSocketFactories.createX509TrustManager(TRUST_STORE_CONFIGURATION);

    private static Undertow server;
    private static RecipeBookService client;

    @BeforeAll
    public static void before() {
        server = RecipeBookApplication.setupRecipeApplicationServer(
                Undertow.builder().addHttpListener(config.getPort(), config.getHost()));
        server.start();

        client = JaxRsClient.create(
                RecipeBookService.class,
                UserAgent.of(UserAgent.Agent.of("test", "0.0.0")),
                NoOpHostEventsSink.INSTANCE,
                clientConfiguration());
    }

    @AfterAll
    public static void after() {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    public void getRecipeUsingInvalidName() {
        Assertions.assertThatRemoteExceptionThrownBy(() -> client.getRecipe(RecipeName.of("doesNotExist")))
                .isGeneratedFromErrorType(RecipeErrors.RECIPE_NOT_FOUND);
    }

    @Test
    public void getRecipeWithBake() {
        RecipeName recipeName = RecipeName.of("baked potatoes");
        Recipe recipe = client.getRecipe(recipeName);
        Recipe expectedRecipe = Recipe.of(
                recipeName,
                ImmutableList.of(
                        RecipeStep.mix(ImmutableSet.of(
                                Ingredient.of("rub oil all over the potatoes"),
                                Ingredient.of("Rub salt all over the potatoes"))),
                        RecipeStep.bake(BakeStep.builder()
                                .temperature(Temperature.builder()
                                        .degree(220)
                                        .unit(TemperatureUnit.CELSIUS)
                                        .build())
                                .durationInSeconds(2700)
                                .build())));
        assertThat(recipe).isEqualTo(expectedRecipe);
    }

    private static ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .from(ClientConfigurations.of(
                        ImmutableList.of("http://" + config.getHost() + ":" + config.getPort() + "/api"),
                        SSL_SOCKET_FACTORY,
                        TRUST_MANAGER))
                // Disable retries to avoid spinning unnecessarily on negative tests
                .maxNumRetries(0)
                .userAgent(clientUserAgent())
                .build();
    }

    private static UserAgent clientUserAgent() {
        return UserAgent.of(UserAgent.Agent.of("test", "develop"));
    }
}
