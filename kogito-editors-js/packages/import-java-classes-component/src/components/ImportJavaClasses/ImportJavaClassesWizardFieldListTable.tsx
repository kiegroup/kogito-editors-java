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
    const [expanded, setExpanded] = React.useState(
      Object.fromEntries(selectedJavaClassFields.map((value, index) => [index, Boolean(value.fields && value.fields.length > 0)]))
    );
    const handleExpansionToggle = (event: React.MouseEvent, pairIndex: number) => {
      setExpanded({
        ...expanded,
        [pairIndex]: !expanded[pairIndex]
      });
    };
    const getJavaClassSimpleName = (className: string) => {
      return className.split(".").pop();
    }
    const formatJavaClassName = (className: string) => {
      return " (" + className + ")";
    }
    let rowIndex = -1;
    return (
      <TableComposable aria-label="field-table" variant="compact">
        {selectedJavaClassFields.map((pair, pairIndex) => {
          rowIndex += 1;
          const parentRow = (
            <Tr key={rowIndex}>
              <Td
                key={`${rowIndex}_0`}
                expand={
                  pair.fields && pair.fields.length > 0
                    ? {
                      rowIndex: pairIndex,
                      isExpanded: expanded[pairIndex],
                      onToggle: handleExpansionToggle
                    }
                    : undefined}
              />
              <Td key={`${rowIndex}_${pair.name}`}>
                <strong>{getJavaClassSimpleName(pair.name)}</strong>
                <span>{formatJavaClassName(pair.name)}</span>
              </Td>
            </Tr>
          );
          return (
            <Tbody key={pairIndex} isExpanded={expanded[pairIndex] === true}>
              {parentRow}
            </Tbody>
          );
        })}
      </TableComposable>
    );
  };
