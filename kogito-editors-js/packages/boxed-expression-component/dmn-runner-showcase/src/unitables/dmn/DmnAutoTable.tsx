import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { Clause, LogicType } from "../../../../dist/api";
import { DmnValidator } from "./DmnValidator";
import { AutoRow } from "../core";
import { createPortal } from "react-dom";
import { context as UniformsContext } from "uniforms";
import { ErrorBoundary } from "../common/ErrorBoundary";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { ExclamationIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-icon";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { DmnGrid } from "./DmnGrid";
import { DmnRunnerRule } from "../../DmnRunnerTable/DmnRunnerTableTypes";
import { DmnRunnerTable, DmnRunnerTableProps } from "../../DmnRunnerTable";
import { NotificationSeverity } from "@kogito-tooling/notifications/dist/api";
import { dmnAutoTableDictionaries, DmnAutoTableI18nContext, dmnAutoTableI18nDefaults } from "../i18n";
import { I18nDictionariesProvider } from "@kogito-tooling/i18n/dist/react-components";
import nextId from "react-id-generator";
import { BoxedExpressionProvider } from "../../../../dist/components";

export enum EvaluationStatus {
  SUCCEEDED = "SUCCEEDED",
  SKIPPED = "SKIPPED",
  FAILED = "FAILED",
}

export interface DecisionResultMessage {
  severity: NotificationSeverity;
  message: string;
  messageType: string;
  sourceId: string;
  level: string;
}

export type Result = boolean | number | null | object | object[] | string;

export interface DecisionResult {
  decisionId: string;
  decisionName: string;
  result: Result;
  messages: DecisionResultMessage[];
  evaluationStatus: EvaluationStatus;
}

export interface DmnResult {
  details?: string;
  stack?: string;
  decisionResults?: DecisionResult[];
  messages: DecisionResultMessage[];
}

export interface Something {
  grid: DmnGrid;
  model?: any[];
  setModel: (model: (previous: any[]) => any[]) => void;
}

interface Props {
  schema: any;
  tableData?: any;
  setTableData?: any;
  results?: Array<DecisionResult[] | undefined>;
  formError: boolean;
  setFormError: React.Dispatch<any>;
}

const FORMS_ID = "forms";

export function DmnAutoTable(props: Props) {
  const bridge = useMemo(() => new DmnValidator().getBridge(props.schema ?? {}), [props.schema]);
  const grid = useMemo(() => (bridge ? new DmnGrid(bridge) : undefined), [bridge]);
  const errorBoundaryRef = useRef<ErrorBoundary>(null);

  const onSubmit = useCallback(
    (model: any, index) => {
      props.setTableData((previousTableData: any) => {
        const newTableData = [...previousTableData];
        newTableData[index] = model;
        return newTableData;
      });
    },
    [props]
  );

  // const onValidate = useCallback((model: any, error: any, index) => {
  //   props.setTableData((previousTableData: any) => {
  //     const newTableData = [...previousTableData];
  //     newTableData[index] = model;
  //     return newTableData;
  //   });
  // }, []);

  const [inputSize, setInputSize] = useState<number>(1);

  const shouldRender = useMemo(() => (grid?.generateBoxedInputs().length ?? 0) > 0, [grid]);

  const onRowNumberUpdated = useCallback((rowNumber) => setInputSize(rowNumber), []);

  const selectedExpression: DmnRunnerTableProps | undefined = useMemo(() => {
    if (grid && props.results) {
      const input = grid.generateBoxedInputs();
      const [outputSet, outputEntries] = grid.generateBoxedOutputs(props.schema ?? {}, props.results);
      const output: Clause[] = Array.from(outputSet.values());

      const rules = [];
      for (let i = 0; i < inputSize; i++) {
        const rule: DmnRunnerRule = {
          inputEntries: [""],
          outputEntries: (outputEntries?.[i] as string[]) ?? [],
        };
        rule.rowDelegate = ({ children }: any) => (
          <AutoRow
            schema={bridge}
            model={props.tableData[i]}
            autosave={true}
            autosaveDelay={500}
            onSubmit={(model: any) => onSubmit(model, i)}
            // onValidate={(model: any, error: any) => onValidate(model, error, i)}
            placeholder={true}
          >
            <UniformsContext.Consumer>
              {(ctx: any) => (
                <>
                  {createPortal(
                    <form id={`dmn-auto-form-${i}`} onSubmit={ctx?.onSubmit} />,
                    document.getElementById(FORMS_ID)!
                  )}
                  {children}
                </>
              )}
            </UniformsContext.Consumer>
          </AutoRow>
        );
        rules.push(rule);
      }

      return {
        name: "DMN Runner",
        logicType: LogicType.DecisionTable,
        input,
        output,
        rules,
        uid: selectedExpression?.uid ?? nextId(),
        onRowNumberUpdated,
      };
    }
  }, [grid, bridge, onSubmit, props.tableData, props.schema, props.results, inputSize, onRowNumberUpdated]);

  useEffect(() => {
    errorBoundaryRef.current?.reset();
  }, [props.formError]);

  const formErrorMessage = useMemo(
    () => (
      <div>
        <EmptyState>
          <EmptyStateIcon icon={ExclamationIcon} />
          <TextContent>
            <Text component={"h2"}>Error</Text>
          </TextContent>
          <EmptyStateBody>
            <p>something happened</p>
          </EmptyStateBody>
        </EmptyState>
      </div>
    ),
    []
  );

  // Resets the ErrorBoundary everytime the FormSchema is updated
  useEffect(() => {
    errorBoundaryRef.current?.reset();
  }, [bridge]);

  useEffect(() => {
    console.log(selectedExpression);
  }, [selectedExpression]);

  return (
    <>
      {shouldRender && bridge && selectedExpression && (
        <ErrorBoundary ref={errorBoundaryRef} setHasError={props.setFormError} error={formErrorMessage}>
          <I18nDictionariesProvider
            defaults={dmnAutoTableI18nDefaults}
            dictionaries={dmnAutoTableDictionaries}
            initialLocale={navigator.language}
            ctx={DmnAutoTableI18nContext}
          >
            <BoxedExpressionProvider expressionDefinition={selectedExpression}>
              <DmnRunnerTable {...selectedExpression} />
            </BoxedExpressionProvider>
          </I18nDictionariesProvider>
        </ErrorBoundary>
      )}
      <div id={FORMS_ID} />
    </>
  );
}
