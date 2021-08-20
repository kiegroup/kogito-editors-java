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
import { TableComposable, Tbody, Td, Tr } from "@patternfly/react-table";

export interface ImportJavaClassesWizardSecondStepProps {
  /** List of the selected classes by user */
  selectedJavaClasses: string[];
}

export const ImportJavaClassesWizardSecondStep: React.FunctionComponent<ImportJavaClassesWizardSecondStepProps> = ({
  selectedJavaClasses,
}) => {
  const emptyMap = new Map<string, Map<string, string>>();
  const [retrievedJavaClassFields, setRetrievedJavaClassFields] = useState<Map<string, Map<string, string>>>(emptyMap);
  /* This function temporary mocks a call to the LSP service method getClasses */
  const loadJavaClassFields = (className: string) => {
    const retrieved = window.envelopeMock.lspGetClassFieldsServiceMocked(className);
    if (retrieved) {
      setRetrievedJavaClassFields((prevState) => {
        const javaClassFieldsMap = new Map<string, Map<string, string>>(prevState);
        javaClassFieldsMap.set(className, retrieved);
        return javaClassFieldsMap;
      });
    }
  };
  useEffect(
    () => selectedJavaClasses.forEach((className: string) => loadJavaClassFields(className)),
    [selectedJavaClasses]
  );

  return (
    <>
      {retrievedJavaClassFields.size != selectedJavaClasses.length ? (
        <Spinner isSVG diameter="80px" />
      ) : (
        <TableComposable aria-label="field-table" variant="compact">
          <Tbody>
            {selectedJavaClasses.map((className, rowIndex) => (
              <Tr key={rowIndex}>
                <Td key={`${rowIndex}_${className}`}>{className.split(".").pop() + " (" + className + ")"}</Td>
              </Tr>
            ))}
          </Tbody>
        </TableComposable>
      )}
    </>
  );
};
