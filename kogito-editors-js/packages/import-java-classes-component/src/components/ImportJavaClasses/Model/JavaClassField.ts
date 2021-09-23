/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { DMNSimpleType, JAVA_TO_DMN_MAP } from "./DMNSimpleType";
import { JavaClass } from "./JavaClass";

export class JavaClassField {
  /* Field Name */
  public name: string;
  /* The Java Type of the field (eg. java.lang.String OR com.mypackace.Test) */
  public type: string;
  /* The DMN Type reference */
  public dmnTypeRef: DMNSimpleType | JavaClass;

  constructor(name: string, type: string) {
    this.name = name;
    this.type = type;
    this.dmnTypeRef = (JAVA_TO_DMN_MAP as any)[this.getJavaSimpleNameType()!] || DMNSimpleType.ANY;
  }

  /* It returns the Java Type without the package (eg. com.mypackage.Test -> Test) */
  getJavaSimpleNameType() {
    return this.type.split(".").pop();
  }

}
