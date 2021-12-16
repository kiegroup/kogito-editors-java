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

import { Divider, Select, SelectGroup, SelectOption, SelectVariant } from "@patternfly/react-core";
import * as React from "react";
import { useCallback, useContext, useState } from "react";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import * as _ from "lodash";
import { DataType, DataTypeProps } from "../../api";
import { BoxedExpressionGlobalContext } from "../../context";

export interface DataTypeSelectorProps {
  /** The pre-selected data type */
  selectedDataType: DataType;
  /** On DataType selection callback */
  onDataTypeChange: (dataType: DataType) => void;
  /** By default the menu will be appended inline, but it is possible to append on the parent or on other elements */
  menuAppendTo?: HTMLElement | "inline" | (() => HTMLElement) | "parent";
}

export const DataTypeSelector: React.FunctionComponent<DataTypeSelectorProps> = ({
  selectedDataType,
  onDataTypeChange,
  menuAppendTo,
}) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const { dataTypeProps } = useContext(BoxedExpressionGlobalContext);

  const [dataTypeSelectOpen, setDataTypeSelectOpen] = useState(false);

  const onDataTypeSelect = useCallback(
    (event, selection) => {
      setDataTypeSelectOpen(false);
      onDataTypeChange(selection);
    },
    [onDataTypeChange]
  );

  const buildOptionsByGroup = useCallback(
    (key: "default" | "custom", options: DataTypeProps[]) => {
      return (
        <SelectGroup label={i18n.dataTypeDropDown[key]} key={key}>
          {_.chain(options)
            .map(({ name }) => <SelectOption key={name} value={name} data-ouia-component-id={name} />)
            .value()}
        </SelectGroup>
      );
    },
    [i18n.dataTypeDropDown]
  );

  const getDataTypes = useCallback(() => {
    const [customDataTypes, defaultDataTypes] = _.chain(dataTypeProps).partition("isCustom").value();
    const defaultDataTypeOptions = buildOptionsByGroup("default", defaultDataTypes);
    const dataTypeGroups = [defaultDataTypeOptions];
    const customDataTypeOptions = buildOptionsByGroup("custom", customDataTypes);
    if (!_.isEmpty(customDataTypes)) {
      dataTypeGroups.push(<Divider key="divider" />);
      dataTypeGroups.push(customDataTypeOptions);
    }
    return dataTypeGroups;
  }, [buildOptionsByGroup, dataTypeProps]);

  const onFilteringDataTypes = useCallback(
    (_, textInput: string) => {
      if (textInput === "") {
        return getDataTypes();
      } else {
        return getDataTypes()
          .map((group) => {
            const filteredGroup = React.cloneElement(group, {
              children: group?.props?.children?.filter((item: React.ReactElement) => {
                return item.props.value.toLowerCase().includes(textInput.toLowerCase());
              }),
            });
            if (filteredGroup?.props?.children?.length > 0) return filteredGroup;
          })
          .filter(Boolean) as JSX.Element[];
      }
    },
    [getDataTypes]
  );

  const onDataTypeSelectToggle = useCallback((isOpen) => setDataTypeSelectOpen(isOpen), []);

  return (
    <Select
      menuAppendTo={menuAppendTo}
      ouiaId="edit-expression-data-type"
      variant={SelectVariant.typeahead}
      typeAheadAriaLabel={i18n.choose}
      onToggle={onDataTypeSelectToggle}
      onSelect={onDataTypeSelect}
      onFilter={onFilteringDataTypes}
      isOpen={dataTypeSelectOpen}
      selections={selectedDataType}
      isGrouped
      hasInlineFilter
      inlineFilterPlaceholderText={i18n.choose}
    >
      {getDataTypes()}
    </Select>
  );
};
