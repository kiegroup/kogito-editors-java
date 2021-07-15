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

import * as React from "react";
import { useCallback, useContext, useRef } from "react";
import "./ExpressionContainer.css";
import { ExpressionProps, LogicType } from "../../api";
import { LogicTypeSelector } from "../LogicTypeSelector";
import { BoxedExpressionGlobalContext } from "../../context";

export const ExpressionContainer: () => JSX.Element = () => {
  const { expressionDefinition, setExpressionDefinition } = useContext(BoxedExpressionGlobalContext);

  const expressionContainerRef = useRef<HTMLDivElement>(null);

  const updateExpressionNameAndDataType = useCallback((updatedName, updatedDataType) => {
    setExpressionDefinition((previousSelectedExpression: ExpressionProps) => ({
      ...previousSelectedExpression,
      name: updatedName,
      dataType: updatedDataType,
    }));
  }, [setExpressionDefinition]);

  const onLogicTypeUpdating = useCallback((logicType) => {
    setExpressionDefinition((previousSelectedExpression: ExpressionProps) => ({
      ...previousSelectedExpression,
      logicType: logicType,
    }));
  }, [setExpressionDefinition]);

  const onLogicTypeResetting = useCallback(() => {
    setExpressionDefinition((previousSelectedExpression: ExpressionProps) => {
      const updatedExpression = {
        uid: previousSelectedExpression.uid,
        name: previousSelectedExpression.name,
        dataType: previousSelectedExpression.dataType,
        logicType: LogicType.Undefined,
      };
      window.beeApi?.resetExpressionDefinition?.(updatedExpression);
      return updatedExpression;
    });
  }, [setExpressionDefinition]);

  return (
    <div className="expression-container">
      <div className="expression-name-and-logic-type">
        <span className="expression-title">{expressionDefinition.name}</span>
        <span className="expression-type">({expressionDefinition.logicType || LogicType.Undefined})</span>
      </div>

      <div
        className="expression-container-box"
        ref={expressionContainerRef}
        data-ouia-component-id="expression-container"
      >
        <LogicTypeSelector
          selectedExpression={expressionDefinition}
          onLogicTypeUpdating={onLogicTypeUpdating}
          onLogicTypeResetting={onLogicTypeResetting}
          onUpdatingNameAndDataType={updateExpressionNameAndDataType}
          getPlacementRef={useCallback(() => expressionContainerRef.current!, [])}
        />
      </div>
    </div>
  );
};
