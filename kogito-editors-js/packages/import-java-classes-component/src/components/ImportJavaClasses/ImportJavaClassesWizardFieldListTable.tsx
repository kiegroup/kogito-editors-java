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

export interface ImportJavaClassesWizardFieldListTableProps {
  /** List of the selected classes by user */
  selectedJavaClassFields: Map<string, Map<string, string>>;
}

export const ImportJavaClassesWizardFieldListTable: React.FunctionComponent<ImportJavaClassesWizardFieldListTableProps> =
  ({ selectedJavaClassFields }) => {
    return (
      <TableComposable aria-label="field-table" variant="compact">
        <Tbody>
          {Array.from(selectedJavaClassFields).map(([key, value]) => (
            <Tr key={key}>
              <Td key={`${key}_${key}`}>
                <strong>{key.split(".").pop()}</strong>
                <span>{" (" + key + ")"}</span>
              </Td>
            </Tr>
          ))}
        </Tbody>
      </TableComposable>
    );
  };
