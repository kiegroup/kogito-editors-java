import { BoxedExpressionProvider } from "./BoxedExpressionProvider";
import { ExpressionContainer } from "../ExpressionContainer";
import * as React from "react";
import { ExpressionProps, PMMLParams } from "../../api";
import {
  boxedExpressionEditorDictionaries,
  BoxedExpressionEditorI18nContext,
  boxedExpressionEditorI18nDefaults,
} from "../../i18n";
import { I18nDictionariesProvider } from "@kogito-tooling/i18n/dist/react-components";

export interface BoxedExpressionEditorProps {
  /** All expression properties used to define it */
  expressionDefinition: ExpressionProps;
  /** PMML parameters */
  pmmlParams?: PMMLParams;
}

export function BoxedExpressionEditor(props: BoxedExpressionEditorProps) {
  return (
    <I18nDictionariesProvider
      defaults={boxedExpressionEditorI18nDefaults}
      dictionaries={boxedExpressionEditorDictionaries}
      initialLocale={navigator.language}
      ctx={BoxedExpressionEditorI18nContext}
    >
      <BoxedExpressionProvider expressionDefinition={props.expressionDefinition} pmmlParams={props.pmmlParams}>
        <ExpressionContainer />
      </BoxedExpressionProvider>
    </I18nDictionariesProvider>
  );
}
