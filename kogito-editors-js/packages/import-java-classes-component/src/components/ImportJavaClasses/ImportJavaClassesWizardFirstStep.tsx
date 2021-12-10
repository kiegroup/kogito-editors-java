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
import { SearchInput } from "@patternfly/react-core";
import CubesIcon from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { useImportJavaClassesWizardI18n } from "../../i18n";
import { useCallback, useState } from "react";
import { EmptyStateWidget } from "../EmptyStateWidget";
import { ImportJavaClassesWizardClassListTable } from "./ImportJavaClassesWizardClassListTable";
import { JavaClass } from "./Model/JavaClass";

export interface ImportJavaClassesWizardFirstStepProps {
  /** List of the selected classes by user */
  selectedJavaClasses: JavaClass[];
  /** Function to be called when adding a Java Class */
  onAddJavaClass: (fullClassName: string) => void;
  /** Function to be called when removing a Java Class */
  onRemoveJavaClass: (fullClassName: string) => void;
}

export const ImportJavaClassesWizardFirstStep = ({
  selectedJavaClasses,
  onAddJavaClass,
  onRemoveJavaClass,
}: ImportJavaClassesWizardFirstStepProps) => {
  const { i18n } = useImportJavaClassesWizardI18n();
  const [searchValue, setSearchValue] = useState("");
  const [retrievedJavaClasses, setRetrievedJavaClasses] = useState<string[]>([]);

  const retrieveJavaClasses = useCallback((value: string) => {
    setSearchValue(value);
    const retrieved = window.envelopeMock.lspGetClassServiceMocked(value);
    if (retrieved) {
      setRetrievedJavaClasses(retrieved);
    }
  }, []);

  const handleSearchValueChange = useCallback((value) => retrieveJavaClasses(value), [retrieveJavaClasses]);

  const handleClearSearch = useCallback(() => {
    setSearchValue("");
    setRetrievedJavaClasses([]);
  }, []);

  const EmptyStep = () => {
    return (
      <EmptyStateWidget
        emptyStateIcon={CubesIcon}
        emptyStateTitleHeading={"h6"}
        emptyStateTitleSize={"md"}
        emptyStateTitleText={i18n.modalWizard.firstStep.emptyState.title}
        emptyStateBodyText={i18n.modalWizard.firstStep.emptyState.body}
      />
    );
  };

  return (
    <>
      <SearchInput
        placeholder={i18n.modalWizard.firstStep.input.placeholder}
        value={searchValue}
        onChange={handleSearchValueChange}
        onClear={handleClearSearch}
        autoFocus
      />
      {retrievedJavaClasses.length > 0 || selectedJavaClasses.length > 0 ? (
        <ImportJavaClassesWizardClassListTable
          selectedJavaClasses={selectedJavaClasses}
          retrievedJavaClasses={retrievedJavaClasses}
          onAddJavaClass={onAddJavaClass}
          onRemoveJavaClass={onRemoveJavaClass}
        />
      ) : (
        <EmptyStep />
      )}
    </>
  );
};
