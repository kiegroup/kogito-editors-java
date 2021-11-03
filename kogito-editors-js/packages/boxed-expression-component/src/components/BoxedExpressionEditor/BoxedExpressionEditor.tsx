/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import { I18nDictionariesProvider } from "@kogito-tooling/i18n/dist/react-components";
import * as _ from "lodash";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { ExpressionProps, PMMLParams } from "../../api";
import { BoxedExpressionGlobalContext } from "../../context";
import {
  boxedExpressionEditorDictionaries,
  BoxedExpressionEditorI18nContext,
  boxedExpressionEditorI18nDefaults,
} from "../../i18n";
import { ExpressionContainer } from "../ExpressionContainer";
import { hashfy, ResizerSupervisor } from "../Resizer";
import { CellSelectionBox } from "../SelectionBox";
import "./BoxedExpressionEditor.css";

export interface BoxedExpressionEditorProps {
  /** All expression properties used to define it */
  expressionDefinition: ExpressionProps;
  /**
   * A boolean used for making (or not) the clear button available on the root expression
   * Note that this parameter will be used only for the root expression.
   *
   * Each expression (internally) has a `noClearAction` property (ExpressionProps interface).
   * You can set directly it for enabling or not the clear button for such expression.
   * */
  clearSupportedOnRootExpression?: boolean;
  /** PMML parameters */
  pmmlParams?: PMMLParams;
}

export const BoxedExpressionEditor: (props: BoxedExpressionEditorProps) => JSX.Element = (
  props: BoxedExpressionEditorProps
) => {
  const [currentlyOpenedHandlerCallback, setCurrentlyOpenedHandlerCallback] = useState(() => _.identity);
  const boxedExpressionEditorRef = useRef<HTMLDivElement>(null);
  const [expressionDefinition, setExpressionDefinition] = useState<ExpressionProps>({
    ...props.expressionDefinition,
    noClearAction: props.clearSupportedOnRootExpression === false,
  });
  const [supervisorHash, setSupervisorHash] = useState(hashfy(props.expressionDefinition));

  useEffect(() => {
    setExpressionDefinition({
      ...props.expressionDefinition,
      noClearAction: props.clearSupportedOnRootExpression === false,
    });
    setSupervisorHash(hashfy(props.expressionDefinition));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [props.expressionDefinition]);

  const onExpressionChange = useCallback(
    (updatedExpression: ExpressionProps) => setExpressionDefinition(updatedExpression),
    []
  );

  return useMemo(
    () => (
      <I18nDictionariesProvider
        defaults={boxedExpressionEditorI18nDefaults}
        dictionaries={boxedExpressionEditorDictionaries}
        initialLocale={navigator.language}
        ctx={BoxedExpressionEditorI18nContext}
      >
        <BoxedExpressionGlobalContext.Provider
          value={{
            pmmlParams: props.pmmlParams,
            supervisorHash,
            setSupervisorHash,
            boxedExpressionEditorRef,
            currentlyOpenedHandlerCallback,
            setCurrentlyOpenedHandlerCallback,
          }}
        >
          <ResizerSupervisor>
            <div className="boxed-expression-editor" ref={boxedExpressionEditorRef}>
              <ExpressionContainer selectedExpression={expressionDefinition} onExpressionChange={onExpressionChange} />
            </div>
          </ResizerSupervisor>
          <CellSelectionBox />
        </BoxedExpressionGlobalContext.Provider>
      </I18nDictionariesProvider>
    ),
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [expressionDefinition]
  );
};
