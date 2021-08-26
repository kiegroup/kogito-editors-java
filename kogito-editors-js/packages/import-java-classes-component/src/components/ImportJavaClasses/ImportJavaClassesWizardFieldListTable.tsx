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
import { TableComposable, Tbody, Td, Tr } from "@patternfly/react-table";
import { JavaClass } from "./Model/JavaClass";

export interface ImportJavaClassesWizardFieldListTableProps {
  /** List of the selected classes by user */
  selectedJavaClassFields: JavaClass[];
}

export const ImportJavaClassesWizardFieldListTable: React.FunctionComponent<ImportJavaClassesWizardFieldListTableProps> =
  ({ selectedJavaClassFields }) => {
    const getJavaClassSimpleName = (className: string) => {
      return className.split(".").pop();
    }
    const formatJavaClassName = (className: string) => {
      return " (" + className + ")";
    }
    return (
      <TableComposable aria-label="field-table" variant="compact">
        <Tbody>
          {selectedJavaClassFields.map(value => (
            <Tr key={value.name}>
              <Td key={`${value.name}_${value.name}`}>
                <strong>{getJavaClassSimpleName(value.name)}</strong>
                <span>{formatJavaClassName(value.name)}</span>
              </Td>
            </Tr>
          ))}
        </Tbody>
      </TableComposable>
    );
  };
