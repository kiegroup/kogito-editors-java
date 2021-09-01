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

import "./RelationExpression.css";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef } from "react";
import "@patternfly/react-styles/css/utilities/Text/text.css";
import {
  Column as RelationColumn,
  DataType,
  executeIfExpressionDefinitionChanged,
  RelationProps,
  Row,
  TableOperation,
} from "../../api";
import { Table } from "../Table";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import * as _ from "lodash";
import { Column, ColumnInstance, DataRecord } from "react-table";

export const RelationExpression: React.FunctionComponent<RelationProps> = (relationProps: RelationProps) => {
  const FIRST_COLUMN_NAME = "column-1";
  const { i18n } = useBoxedExpressionEditorI18n();

  useEffect(() => {
    console.log(relationProps);
  }, [relationProps])

  const handlerConfiguration = [
    {
      group: i18n.columns,
      items: [
        { name: i18n.columnOperations.insertLeft, type: TableOperation.ColumnInsertLeft },
        { name: i18n.columnOperations.insertRight, type: TableOperation.ColumnInsertRight },
        { name: i18n.columnOperations.delete, type: TableOperation.ColumnDelete },
      ],
    },
    {
      group: i18n.rows,
      items: [
        { name: i18n.rowOperations.insertAbove, type: TableOperation.RowInsertAbove },
        { name: i18n.rowOperations.insertBelow, type: TableOperation.RowInsertBelow },
        { name: i18n.rowOperations.delete, type: TableOperation.RowDelete },
      ],
    },
  ];

  const columns: RelationColumn[] = useMemo(
    () =>
      relationProps.columns === undefined
        ? [{ name: FIRST_COLUMN_NAME, dataType: DataType.Undefined }]
        : relationProps.columns,
    [relationProps]
  );

  const rows: Row[] = useMemo(() => (relationProps.rows === undefined ? [[""]] : relationProps.rows), [relationProps]);

  const spreadRelationExpressionDefinition = useCallback(
    (newColumns?: RelationColumn[], newRows?: Row[]) => {
      const expressionDefinition = {
        ...relationProps,
        columns: newColumns ?? columns,
        rows: newRows ?? rows,
      };

      executeIfExpressionDefinitionChanged(
        relationProps,
        expressionDefinition,
        () => {
          if (relationProps.isHeadless) {
            relationProps.onUpdatingRecursiveExpression?.(expressionDefinition);
          } else {
            window.beeApi?.broadcastRelationExpressionDefinition?.(expressionDefinition);
          }
        },
        ["columns", "rows"]
      );
    },
    [relationProps, columns, rows]
  );

  const convertColumnsForTheTable = useMemo(
    () =>
      _.map(
        columns,
        (column: RelationColumn) =>
          ({
            label: column.name,
            accessor: column.name,
            dataType: column.dataType,
            ...(column.width ? { width: column.width } : {}),
          } as Column)
      ),
    [columns]
  );

  const convertRowsForTheTable = useMemo(
    () =>
      _.map(rows, (row) =>
        _.reduce(
          columns,
          (tableRow: DataRecord, column, columnIndex) => {
            tableRow[column.name] = row[columnIndex] || "";
            return tableRow;
          },
          {}
        )
      ),
    [rows, columns]
  );

  const onRowsUpdate = useCallback(
    (rows: DataRecord[]) => {
      const newRows = _.map(rows, (tableRow: DataRecord) =>
        _.reduce(
          columns,
          (row: string[], column: RelationColumn) => {
            row.push((tableRow[column.name]! as string) || "");
            return row;
          },
          []
        )
      );
      spreadRelationExpressionDefinition(undefined, newRows);
    },
    [spreadRelationExpressionDefinition, columns]
  );

  const onColumnsUpdate = useCallback(
    (columns) => {
      const newColumns = _.map(columns, (columnInstance: ColumnInstance) => ({
        name: columnInstance.accessor,
        dataType: columnInstance.dataType,
        width: columnInstance.width,
      }));
      const newRows = rows.map((tableRow: Row) =>
        newColumns.reduce((row: string[], column: RelationColumn) => {
          row.push((tableRow[column.name as any]! as string) || "");
          return row;
        }, [])
      );
      spreadRelationExpressionDefinition(newColumns, newRows);
    },
    [spreadRelationExpressionDefinition, rows, columns]
  );

  useEffect(() => {
    /** Function executed only the first time the component is loaded */
    spreadRelationExpressionDefinition();
  }, [spreadRelationExpressionDefinition]);

  return (
    <div className="relation-expression">
      <Table
        editColumnLabel={i18n.editRelation}
        columns={convertColumnsForTheTable}
        rows={convertRowsForTheTable}
        onColumnsUpdate={onColumnsUpdate}
        onRowsUpdate={onRowsUpdate}
        handlerConfiguration={handlerConfiguration}
      />
    </div>
  );
};
