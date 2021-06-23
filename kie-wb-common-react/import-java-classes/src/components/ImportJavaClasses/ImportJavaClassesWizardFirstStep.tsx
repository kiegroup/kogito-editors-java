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
import { EmptyState, EmptyStateIcon, EmptyStateBody, Title, SearchInput } from "@patternfly/react-core";
import CubesIcon from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { useImportJavaClassesWizardI18n } from "../../i18n";
import { useCallback, useState } from "react";

export const ImportJavaClassesWizardFirstStepContent: React.FunctionComponent = () => {
  const EMPTY_SEARCH_VALUE = "";
  const { i18n } = useImportJavaClassesWizardI18n();
  const [searchValue, setSearchValue] = useState(EMPTY_SEARCH_VALUE);
  const onSearchValueChange = useCallback((value: string) => setSearchValue(value), [searchValue]);

  return (
    <>
      <Title headingLevel="h6" size="md">
        {i18n.modalWizard.firstStep.input.title}
      </Title>
      <SearchInput
        placeholder={i18n.modalWizard.firstStep.input.placeholder}
        value={searchValue}
        onChange={onSearchValueChange}
        onClear={() => onSearchValueChange(EMPTY_SEARCH_VALUE)}
        autoFocus
      />
      <EmptyState>
        <EmptyStateIcon icon={CubesIcon} />
        <Title headingLevel="h6" size="md">
          {i18n.modalWizard.firstStep.emptyState.title}
        </Title>
        <EmptyStateBody>{i18n.modalWizard.firstStep.emptyState.body}</EmptyStateBody>
      </EmptyState>
    </>
  );
};
