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

import groupBy from "lodash/groupBy";
import * as React from "react";
import { useCallback, useContext, useEffect, useMemo } from "react";
import { ColumnInstance, DataRecord } from "react-table";
import { ExpressionProps, GroupOperations, LogicType, TableHeaderVisibility, TableOperation } from "../../../dist/api";
import { BoxedExpressionGlobalContext } from "../../../dist/context";
import { getColumnsAtLastLevel, Table } from "../../../dist/components";
import "./DmnRunnerTable.css";
import { DmnRunnerClause, DmnRunnerRule } from "./DmnRunnerTableTypes";
import { useDmnAutoTableI18n } from "../unitables";
import "../../../src/components/ExpressionContainer/ExpressionContainer.css";
import "../../../src/components/LogicTypeSelector/LogicTypeSelector.css";

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
    (groupName: string) => [
      {
        group: groupName,
        items: [
          { name: i18n.columnOperations.insertLeft, type: TableOperation.ColumnInsertLeft },
          { name: i18n.columnOperations.insertRight, type: TableOperation.ColumnInsertRight },
          { name: i18n.columnOperations.delete, type: TableOperation.ColumnDelete },
        ],
      },
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
    configuration[EMPTY_SYMBOL] = generateHandlerConfigurationByColumn(i18n.ruleAnnotation);
    configuration[DecisionTableColumnType.InputClause] = generateHandlerConfigurationByColumn(i18n.inputClause);
    configuration[DecisionTableColumnType.OutputClause] = generateHandlerConfigurationByColumn(i18n.outputClause);
    configuration[DecisionTableColumnType.Annotation] = generateHandlerConfigurationByColumn(i18n.ruleAnnotation);
    return configuration;
  }, [generateHandlerConfigurationByColumn, i18n.inputClause, i18n.outputClause, i18n.ruleAnnotation]);

  const getEditColumnLabel = useMemo(() => {
    const editColumnLabel: { [columnGroupType: string]: string } = {};
    editColumnLabel[DecisionTableColumnType.InputClause] = i18n.editClause.input;
    editColumnLabel[DecisionTableColumnType.OutputClause] = i18n.editClause.output;
    return editColumnLabel;
  }, [i18n.editClause.input, i18n.editClause.output]);

  const spreadDecisionTableExpressionDefinition = useCallback((columns: ColumnInstance[], rows: DataRecord[]) => {
    const groupedColumns = groupBy(getColumnsAtLastLevel(columns), (column) => column.groupType);
    const newInput: DmnRunnerClause[] = (groupedColumns[DecisionTableColumnType.InputClause] ?? []).map(
      (inputClause) => ({
        name: inputClause.accessor,
        dataType: inputClause.dataType,
        width: inputClause.width,
        cellDelegate: (inputClause as any)?.cellDelegate,
      })
    );
    const newOutput: DmnRunnerClause[] = (groupedColumns[DecisionTableColumnType.OutputClause] ?? []).map(
      (outputClause) => ({
        name: outputClause.accessor,
        dataType: outputClause.dataType,
        width: outputClause.width,
      })
    );
    const newRules: DmnRunnerRule[] = rows.map((row: DataRecord) => ({
      inputEntries: newInput.map((inputClause) => row[inputClause.name] as string),
      outputEntries: newOutput.map((outputClause) => row[outputClause.name] as string),
      rowDelegate: row.rowDelegate as any,
    }));

    // window.beeApi?.broadcastDmnRunnerTable?.(newRules.length);
  }, []);

  const memoColumns = useMemo(() => {
    const inputColumns = (props.input ?? []).map(
      (inputClause) =>
        ({
          label: inputClause.name,
          accessor: inputClause.name,
          dataType: inputClause.dataType,
          width: inputClause.width,
          groupType: DecisionTableColumnType.InputClause,
          cssClasses: "decision-table--output",
          cellDelegate: inputClause.cellDelegate,
        } as any)
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

    const inputSection = {
      groupType: DecisionTableColumnType.InputClause,
      label: "Test",
      accessor: "Something",
      cssClasses: "decision-table--output",
      columns: inputColumns,
      appendColumnsOnChildren: true,
    };
    const outputSection = {
      groupType: DecisionTableColumnType.OutputClause,
      label: " ",
      accessor: " ",
      cssClasses: "decision-table--output",
      columns: outputColumns,
      appendColumnsOnChildren: true,
    };

    return [inputSection, outputSection] as ColumnInstance[];
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
      spreadDecisionTableExpressionDefinition(memoColumns, newRows);
    },
    [spreadDecisionTableExpressionDefinition, memoColumns]
  );

  const onRowAdding = useCallback(() => {
    return getColumnsAtLastLevel(memoColumns).reduce((tableRow: DataRecord, column: ColumnInstance) => {
      tableRow[column.accessor] = EMPTY_SYMBOL;
      return tableRow;
    }, {} as DataRecord);
  }, [memoColumns]);

  useEffect(() => {
    console.log("TABLE", props.name, props.input, props.output, props.rules);
  }, [props.name, props.input, props.output, props.rules]);

  return (
    <div className="expression-container">
      <div className="expression-name-and-logic-type">
        <span className="expression-title">{props?.name ?? ""}</span>
        <span className="expression-type">({props?.logicType ?? LogicType.Undefined})</span>
      </div>

      <div className="expression-container-box" data-ouia-component-id="expression-container">
        <div className={`decision-table-expression ${props.uid}`}>
          <div className={`logic-type-selector logic-type-selected`}>
            <Table
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
