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

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;

@JsType
public class JSCommandWithArgumentsImpl implements JSCommandWithArguments {

    private JSCommandWithArguments command;

    public JSCommandWithArgumentsImpl(JSCommandWithArguments command) {
        this.command = command;
    }

    @Override
    @JsIgnore
    public void execute(String UUID) {
        command.execute(UUID);
    }

    public void perform(String UUID) {
        command.execute(UUID);
    }
}
