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
import com.palantir.conjure.java.api.testing.Assertions;
import com.palantir.conjure.java.client.config.ClientConfiguration;
import com.palantir.conjure.java.client.config.ClientConfigurations;
import com.palantir.conjure.java.client.jaxrs.JaxRsClient;
import com.palantir.conjure.java.okhttp.NoOpHostEventsSink;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class RecipeBookApplicationTest {

    private static RecipeBookService client;

    @BeforeAll
    public static void before()
            throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException,
                    KeyManagementException {

        File crtFile = new File("src/test/resources/certs/ca-cert");
        Certificate certificate =
                CertificateFactory.getInstance("X.509").generateCertificate(new FileInputStream(crtFile));
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setCertificateEntry("server", certificate);

        TrustManagerFactory trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);

        TrustManager[] trustManager = trustManagerFactory.getTrustManagers();
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManager, null);

        ClientConfiguration clientConfig = ClientConfigurations.of(
                ImmutableList.of("https://localhost:8345/api/"), sslContext.getSocketFactory(), (X509TrustManager)
                        trustManager[0]);

        client = JaxRsClient.create(
                RecipeBookService.class,
                UserAgent.of(UserAgent.Agent.of("test", "0.0.0")),
                NoOpHostEventsSink.INSTANCE,
                clientConfig);
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
}
