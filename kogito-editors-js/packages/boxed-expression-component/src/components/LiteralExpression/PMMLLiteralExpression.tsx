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

import "./PMMLLiteralExpression.css";
import * as React from "react";
import { useCallback, useContext, useMemo, useState } from "react";
import { PMMLLiteralExpressionProps } from "../../api";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core";
import { BoxedExpressionGlobalContext } from "../../context";

export const PMMLLiteralExpression: React.FunctionComponent<PMMLLiteralExpressionProps> = (
  props: PMMLLiteralExpressionProps
) => {
  const [selectOpen, setSelectOpen] = useState(false);
  const globalContext = useContext(BoxedExpressionGlobalContext);
  const onSelectToggle = useCallback((isOpen) => setSelectOpen(isOpen), []);

  const onSelect = useCallback(
    (event, updatedSelection) => {
      setSelectOpen(false);
      props.onUpdatingRecursiveExpression?.({
        ...props,
        selected: updatedSelection,
      } as PMMLLiteralExpressionProps);
    },
    [props]
  );

  const getOptions = useMemo(() => {
    return props.getOptions().map((key) => (
      <SelectOption data-testid={`pmml-${key}`} key={key} value={key} data-ouia-component-id={key}>
        {key}
      </SelectOption>
    ));
  }, [props]);

  const showingPlaceholder = useMemo(() => props.selected === undefined, [props.selected]);

  return (
    <Select
      className={`pmml-literal-expression ${showingPlaceholder ? "showing-placeholder" : ""}`}
      menuAppendTo={globalContext.boxedExpressionEditorRef?.current ?? "inline"}
      ouiaId="pmml-literal-expression-selector"
      placeholderText={props.noOptionsLabel}
      aria-placeholder={props.noOptionsLabel}
      variant={SelectVariant.single}
      onToggle={onSelectToggle}
      onSelect={onSelect}
      isOpen={selectOpen}
      selections={props.selected}
    >
      {getOptions}
    </Select>
  );
};
