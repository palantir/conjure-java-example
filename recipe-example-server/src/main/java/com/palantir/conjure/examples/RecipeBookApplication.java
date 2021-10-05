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
import com.palantir.conjure.examples.recipe.api.RecipeBookServiceEndpoints;
import com.palantir.conjure.examples.resources.RecipeBookResource;
import com.palantir.conjure.java.undertow.runtime.ConjureHandler;
import io.undertow.Undertow;

public final class RecipeBookApplication {

    private RecipeBookApplication() {
        // noop
    }

    public static void main(String[] _args) {
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "0.0.0.0")
                .setHandler(ConjureHandler.builder()
                        .services(RecipeBookServiceEndpoints.of(new RecipeBookResource(ImmutableSet.of())))
                        .build())
                .build();
        server.start();
    }
}
