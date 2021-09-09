/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.kogito.client.editor;

import com.ait.lienzo.client.core.types.JsLienzo;
import com.google.gwt.user.client.Command;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="window")
public class WindowJSType {

    @JsProperty
    private static Object linkedjsLienzo;

    @JsProperty
    private static JSCommand commandExecute;

    @JsProperty
    private static JSCommandWithArguments operationExecute;


    @JsOverlay
    public static final void linkLienzoJS(JsLienzo jsLienzo) {
        linkedjsLienzo = jsLienzo;
    }

    @JsOverlay
    public static final void linkStunnerCommand(Command command) {
        commandExecute = new JSCommand(command);
    }

    @JsOverlay
    public static final void linkStunnerOperation(JSCommandWithArguments command) {
        operationExecute = new JSCommandWithArgumentsImpl(command);
    }

}
