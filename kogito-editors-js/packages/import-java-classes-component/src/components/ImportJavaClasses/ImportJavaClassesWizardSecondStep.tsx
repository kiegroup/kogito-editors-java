/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { Spinner } from "@patternfly/react-core";
import { useEffect, useState } from "react";
import { ImportJavaClassesWizardFieldListTable } from "./ImportJavaClassesWizardFieldListTable";
import { JavaClassField } from "./Model/JavaClassField";
import { JavaClass } from "./Model/JavaClass";
import { DMNSimpleType, JAVA_TO_DMN_MAP } from "./Model/DMNSimpleType";
import { getJavaClassSimpleName } from "./Model/JavaClassUtils";
import { inlinePositioning } from "@patternfly/react-core/dist/js/helpers/Popper/DeprecatedTippyTypes";

export interface ImportJavaClassesWizardSecondStepProps {
  /** List of the selected classes by user */
  selectedJavaClasses: string[];
  /** Function to be called to updated selected a Java Classes for user */
  onSelectedJavaClassesUpdated: (fullClassName: string, add: boolean) => void;
}

export const ImportJavaClassesWizardSecondStep: React.FunctionComponent<ImportJavaClassesWizardSecondStepProps> = ({
  selectedJavaClasses,
  onSelectedJavaClassesUpdated,
}: ImportJavaClassesWizardSecondStepProps) => {
  const [retrievedJavaClassFields, setRetrievedJavaClassFields] = useState<JavaClass[]>([]);
  useEffect(
    () => selectedJavaClasses.filter(item => !retrievedJavaClassFields.map(jc => jc.name).includes(item)).forEach((className: string) => loadJavaClassFields(className)),
    // eslint-disable-next-line
    [selectedJavaClasses]
  );
  /* This function temporary mocks a call to the LSP service method getClassFields */
  const loadJavaClassFields = (className: string) => {
    window.envelopeMock.lspGetClassFieldsServiceMocked(className).then(
      value => {
        setRetrievedJavaClassFields((prevState: JavaClass[]) => {
          updateJavaClassFieldReference(prevState, selectedJavaClasses);
          const javaFields = Array.from(value, ([name, type]) => generateJavaClassField(name, type, selectedJavaClasses));
          const javaClass = new JavaClass(className, javaFields);
          return [...prevState, javaClass].sort((a, b) => (a.name < b.name ? -1 : 1));
        });
      }
    );
  };
  const generateJavaClassField = (name: string, type: string, selectedJavaClasses: string[]) => {
    let dmnTypeRef: string = (JAVA_TO_DMN_MAP as any)[getJavaClassSimpleName(type)] || DMNSimpleType.ANY;
    if (dmnTypeRef === DMNSimpleType.ANY) {
      if (selectedJavaClasses.includes(type)) {
        dmnTypeRef = getJavaClassSimpleName(type);
      }
    }
    return new JavaClassField(name, type, dmnTypeRef);
  }
  const updateJavaClassFieldReference = (classes: JavaClass[], selectedJavaClasses: string[]) => {
    classes.forEach(clazz => {
      clazz.fields.forEach(field => {
        if (field.dmnTypeRef === DMNSimpleType.ANY && selectedJavaClasses.includes(field.type)) {
          field.dmnTypeRef = getJavaClassSimpleName(field.type);
        }
      })
    })
  }

  return (
    <>
      {retrievedJavaClassFields.length != selectedJavaClasses.length ? (
        <Spinner isSVG diameter="150px" />
      ) : (
        <ImportJavaClassesWizardFieldListTable
          selectedJavaClassFields={retrievedJavaClassFields}
          readOnly={false}
          onFetchButtonClick={(fullClassName: string) => onSelectedJavaClassesUpdated(fullClassName, true)}
        />
      )}
    </>
  );
};
