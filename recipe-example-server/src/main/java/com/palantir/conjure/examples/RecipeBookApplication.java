/*
 * (c) Copyright 2021 Palantir Technologies Inc. All rights reserved.
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

import com.palantir.conjure.examples.recipe.Configuration;
import com.palantir.conjure.examples.recipe.api.RecipeBookServiceEndpoints;
import com.palantir.conjure.examples.resources.RecipeBookResource;
import com.palantir.conjure.java.api.config.ssl.SslConfiguration;
import com.palantir.conjure.java.config.ssl.SslSocketFactories;
import com.palantir.conjure.java.undertow.runtime.ConjureHandler;
import io.undertow.Handlers;
import io.undertow.Undertow;
import java.io.IOException;
import java.nio.file.Paths;
import javax.net.ssl.SSLContext;

public final class RecipeBookApplication {
    private static final String KEY_STORE_PATH = "src/test/resources/certs/keystore.jks"; // password changeit
    private static final String TRUSTSTORE_PATH = "src/test/resources/certs/truststore.jks"; // password changeit

    private RecipeBookApplication() {
        // noop
    }

    public static void main(String[] _args) throws IOException {
        Configuration config = ConfigurationLoader.load();
        SslConfiguration sslConfig =
                SslConfiguration.of(Paths.get(TRUSTSTORE_PATH), Paths.get(KEY_STORE_PATH), "changeit");
        SSLContext sslContext = SslSocketFactories.createSslContext(sslConfig);

        Undertow.Builder builder = Undertow.builder().addHttpsListener(config.getPort(), config.getHost(), sslContext);
        setupRecipeApplicationServer(builder).start();
    }

    public static Undertow setupRecipeApplicationServer(Undertow.Builder builder) {
        return builder.setHandler(Handlers.path()
                        .addPrefixPath(
                                "api/",
                                ConjureHandler.builder()
                                        .services(RecipeBookServiceEndpoints.of(
                                                new RecipeBookResource(ExampleRecipes.getCommonRecipes())))
                                        .build()))
                .build();
    }
}
