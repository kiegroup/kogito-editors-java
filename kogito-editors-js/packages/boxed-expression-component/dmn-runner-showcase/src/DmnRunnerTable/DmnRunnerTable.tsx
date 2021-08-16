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

import * as React from "react";
import { useCallback, useContext, useEffect, useMemo } from "react";
import "../../../dist";
import { ColumnInstance, DataRecord } from "react-table";
import { ExpressionProps, GroupOperations, LogicType, TableHeaderVisibility, TableOperation } from "../../../dist/api";
import { BoxedExpressionGlobalContext } from "../../../dist/context";
import { getColumnsAtLastLevel, Table } from "../../../dist/components";
import "./DmnRunnerTable.css";
import { DmnRunnerClause, DmnRunnerRule } from "./DmnRunnerTableTypes";
import { useDmnAutoTableI18n } from "../unitables";

enum DecisionTableColumnType {
  InputClause = "input",
  OutputClause = "output",
  Annotation = "annotation",
}

const DASH_SYMBOL = "-";
const EMPTY_SYMBOL = "";

export interface DmnRunnerTableProps extends ExpressionProps {
  /** Input columns definition */
  input?: DmnRunnerClause[];
  /** Output columns definition */
  output?: DmnRunnerClause[];
  /** Rules represent rows values */
  rules?: DmnRunnerRule[];
  /** Callback to be called when row number is updated */
  onRowNumberUpdated: (rowNumber: number) => void;
}

export function DmnRunnerTable(props: DmnRunnerTableProps) {
  const { i18n } = useDmnAutoTableI18n();

  const getColumnPrefix = useCallback((groupType?: string) => {
    switch (groupType) {
      case DecisionTableColumnType.InputClause:
        return "input-";
      case DecisionTableColumnType.OutputClause:
        return "output-";
      case DecisionTableColumnType.Annotation:
        return "annotation-";
      default:
        return "column-";
    }
  }, []);

  const generateHandlerConfigurationByColumn = useCallback(
    () => [
      {
        group: i18n.decisionRule,
        items: [
          { name: i18n.rowOperations.insertAbove, type: TableOperation.RowInsertAbove },
          { name: i18n.rowOperations.insertBelow, type: TableOperation.RowInsertBelow },
          { name: i18n.rowOperations.delete, type: TableOperation.RowDelete },
          { name: i18n.rowOperations.duplicate, type: TableOperation.RowDuplicate },
        ],
      },
    ],
    [i18n]
  );

  const { setSupervisorHash } = useContext(BoxedExpressionGlobalContext);

  const getHandlerConfiguration = useMemo(() => {
    const configuration: { [columnGroupType: string]: GroupOperations[] } = {};
    configuration[EMPTY_SYMBOL] = generateHandlerConfigurationByColumn();
    configuration[DecisionTableColumnType.InputClause] = generateHandlerConfigurationByColumn();
    configuration[DecisionTableColumnType.OutputClause] = generateHandlerConfigurationByColumn();
    configuration[DecisionTableColumnType.Annotation] = generateHandlerConfigurationByColumn();
    return configuration;
  }, [generateHandlerConfigurationByColumn, i18n.inputClause, i18n.outputClause, i18n.ruleAnnotation]);

  const getEditColumnLabel = useMemo(() => {
    const editColumnLabel: { [columnGroupType: string]: string } = {};
    editColumnLabel[DecisionTableColumnType.InputClause] = i18n.editClause.input;
    editColumnLabel[DecisionTableColumnType.OutputClause] = i18n.editClause.output;
    return editColumnLabel;
  }, [i18n.editClause.input, i18n.editClause.output]);

  const memoColumns = useMemo(() => {
    const inputSection = (props.input ?? []).map(
      (inputClause) => {
        if (inputClause.insideProperties) {
          const insideProperties = inputClause.insideProperties.map((insideInputClauses) => {
            return ({
              label: insideInputClauses.name,
              accessor: insideInputClauses.name,
              dataType: insideInputClauses.dataType,
              width: insideInputClauses.width,
              groupType: DecisionTableColumnType.InputClause,
              cellDelegate: insideInputClauses.cellDelegate
            } as any);
          })
          return {
            groupType: DecisionTableColumnType.InputClause,
            label: inputClause.name,
            accessor: inputClause.name,
            cssClasses: "decision-table--input",
            columns: insideProperties,
            appendColumnsOnChildren: true,
          };
        }
        return {
          groupType: DecisionTableColumnType.InputClause,
          label: inputClause.name,
          accessor: inputClause.name,
          cssClasses: "decision-table--input",
          appendColumnsOnChildren: true,
          cellDelegate: inputClause.cellDelegate
        };

        // cssClasses: inputClause.insideProperties ? "" : "decision-table--input",

      }
    );
    const outputColumns = (props.output ?? []).map(
      (outputClause) =>
        ({
          label: outputClause.name,
          accessor: outputClause.name,
          dataType: outputClause.dataType,
          width: outputClause.width,
          groupType: DecisionTableColumnType.OutputClause,
          cssClasses: "decision-table--output",
        } as ColumnInstance)
    );

    const outputSection = {
      groupType: DecisionTableColumnType.OutputClause,
      label: " ",
      accessor: " ",
      cssClasses: "decision-table--output",
      columns: outputColumns,
      appendColumnsOnChildren: true,
    };

    return [...inputSection, outputSection] as ColumnInstance[];
  }, [props.input, props.output]);

  const memoRows = useMemo(() => {
    return (props.rules ?? []).map((rule) => {
      const rowArray = [...rule.inputEntries, ...rule.outputEntries];
      return getColumnsAtLastLevel(memoColumns).reduce((tableRow: any, column, columnIndex: number) => {
        tableRow[column.accessor] = rowArray[columnIndex] || EMPTY_SYMBOL;
        tableRow.rowDelegate = rule.rowDelegate;
        return tableRow;
      }, {});
    });
  }, [props.rules, memoColumns]);

  const onRowsUpdate = useCallback(
    (updatedRows) => {
      const newRows = updatedRows.map((row: any) =>
        getColumnsAtLastLevel(memoColumns).reduce((filledRow: DataRecord, column: ColumnInstance) => {
          if (row.rowDelegate) {
            filledRow[column.accessor] = row[column.accessor];
            filledRow.rowDelegate = row.rowDelegate;
          } else if (row[column.accessor] === null || row[column.accessor] === undefined) {
            filledRow[column.accessor] =
              column.groupType === DecisionTableColumnType.InputClause ? DASH_SYMBOL : EMPTY_SYMBOL;
          } else {
            filledRow[column.accessor] = row[column.accessor];
          }
          return filledRow;
        }, {})
      );
      props.onRowNumberUpdated?.(newRows.length);
    },
    [props.onRowNumberUpdated, memoColumns]
  );

  const onRowAdding = useCallback(() => {
    return getColumnsAtLastLevel(memoColumns).reduce((tableRow: DataRecord, column: ColumnInstance) => {
      tableRow[column.accessor] = EMPTY_SYMBOL;
      return tableRow;
    }, {} as DataRecord);
  }, [memoColumns]);

  useEffect(() => {
    console.log(document.getElementsByTagName("input"))
  }, []);

  return (
    <div className="expression-container">
      <div className="expression-name-and-logic-type">
        <span className="expression-title">{props?.name ?? ""}</span>
      </div>

      <div className="expression-container-box" data-ouia-component-id="expression-container">
        <div className={`decision-table-expression ${props.uid}`}>
          <div className={`logic-type-selector logic-type-selected`}>
            <Table
              editableHeader={false}
              headerLevels={1}
              headerVisibility={TableHeaderVisibility.Full}
              getColumnPrefix={getColumnPrefix}
              editColumnLabel={getEditColumnLabel}
              handlerConfiguration={getHandlerConfiguration}
              columns={memoColumns}
              rows={memoRows}
              onRowsUpdate={onRowsUpdate}
              onRowAdding={onRowAdding}
            />
          </div>
        </div>
      </div>
    </div>
  );
}
