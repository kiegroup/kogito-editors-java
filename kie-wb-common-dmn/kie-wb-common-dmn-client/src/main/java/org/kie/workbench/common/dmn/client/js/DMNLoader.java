/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.js;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ExpressionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.PMMLParam;

@JsType(isNative = true)
public class DMNLoader {

    @JsMethod(namespace = "__KIE__DMN_LOADER__")
    public static native void renderHelloWorld(final String selector);

    @JsMethod(namespace = "__KIE__DMN_LOADER__")
    public static native void renderBoxedExpressionEditor(final String selector, final ExpressionProps expressionProps, final Boolean clearSupported, final PMMLParam[] pmmlParams);
}
