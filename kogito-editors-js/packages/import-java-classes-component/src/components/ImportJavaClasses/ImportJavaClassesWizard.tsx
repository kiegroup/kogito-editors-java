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
import { ModalWizard } from "../ModalWizard";
import { useImportJavaClassesWizardI18n } from "../../i18n";
import { ImportJavaClassesWizardFirstStep } from "./ImportJavaClassesWizardFirstStep";
import { ImportJavaClassesWizardSecondStep } from "./ImportJavaClassesWizardSecondStep";
import { ImportJavaClassesWizardThirdStep } from "./ImportJavaClassesWizardThirdStep";
import { useCallback, useState } from "react";
import { JavaClass } from "./Model/JavaClass";
import { JavaField } from "./Model/JavaField";
import { DMNSimpleType } from "./Model/DMNSimpleType";
import { getJavaClassSimpleName } from "./Model/JavaClassUtils";

export interface ImportJavaClassesWizardProps {
  /** Button disabled status */
  buttonDisabledStatus: boolean;
  /** Button tooltip message */
  buttonTooltipMessage?: string;
  /** Function to call to send selected Java Classes to GWT Editor */
  sendJavaClassesToEditor: (javaClasses: JavaClass[]) => void;
}

export const ImportJavaClassesWizard = ({
  buttonDisabledStatus,
  buttonTooltipMessage,
  sendJavaClassesToEditor,
}: ImportJavaClassesWizardProps) => {
  const { i18n } = useImportJavaClassesWizardI18n();
  const [javaClasses, setJavaClasses] = useState<JavaClass[]>([]);

  const updateJavaFieldsReferences = useCallback(
    (updatedJavaClasses: JavaClass[], previousJavaClasses: JavaClass[]) => {
      const updatedJavaClassesNames = updatedJavaClasses.map((javaClass) => javaClass.name);
      const previousJavaClassesNames = previousJavaClasses.map((javaClass) => javaClass.name);
      const allFields = javaClasses.map((javaClass) => javaClass.fields).flat(1);
      allFields.forEach((field) => {
        if (field.dmnTypeRef === DMNSimpleType.ANY && updatedJavaClassesNames.includes(field.type)) {
          field.dmnTypeRef = getJavaClassSimpleName(field.type);
        } else if (previousJavaClassesNames.includes(field.type) && !updatedJavaClassesNames.includes(field.type)) {
          field.dmnTypeRef = DMNSimpleType.ANY;
        }
      });
    },
    [javaClasses]
  );

  const addJavaClass = useCallback(
    (fullClassName: string) => {
      setJavaClasses((prevState) => {
        if (!prevState.some((javaClass) => javaClass.name === fullClassName)) {
          const updatedSelectedJavaClasses = [...prevState, new JavaClass(fullClassName)];
          updatedSelectedJavaClasses.sort((a, b) => (a.name < b.name ? -1 : 1));
          updateJavaFieldsReferences(updatedSelectedJavaClasses, prevState);
          return updatedSelectedJavaClasses;
        }
        return prevState;
      });
    },
    [updateJavaFieldsReferences]
  );

  const removeJavaClass = useCallback(
    (fullClassName: string) => {
      setJavaClasses((prevState) => {
        const updatedSelectedJavaClasses = prevState.filter((javaClass) => javaClass.name !== fullClassName);
        updateJavaFieldsReferences(updatedSelectedJavaClasses, prevState);
        return updatedSelectedJavaClasses;
      });
    },
    [updateJavaFieldsReferences]
  );

  const updateSelectedClassesFields = useCallback((fullClassName: string, fields: JavaField[]) => {
    setJavaClasses((prevState) => {
      const updatedJavaClasses = [...prevState];
      const javaClassIndex = updatedJavaClasses.findIndex((javaClass) => javaClass.name === fullClassName);
      if (javaClassIndex > -1) {
        updatedJavaClasses[javaClassIndex].setFields(fields);
      }
      return updatedJavaClasses;
    });
  }, []);

  const isSecondStepActivatable = useCallback(() => {
    return javaClasses.length > 0;
  }, [javaClasses]);

  const isThirdStepActivatable = useCallback(() => {
    return javaClasses.length > 0 && javaClasses.every((javaClass) => javaClass.fieldsLoaded);
  }, [javaClasses]);

  const resetJavaClassState = useCallback(() => {
    setJavaClasses([]);
  }, []);

  const onWizardFinishing = useCallback(() => {
    sendJavaClassesToEditor(javaClasses);
  }, [javaClasses, sendJavaClassesToEditor]);

  const steps = [
    {
      canJumpTo: true,
      component: (
        <ImportJavaClassesWizardFirstStep
          selectedJavaClasses={javaClasses}
          onAddJavaClass={addJavaClass}
          onRemoveJavaClass={removeJavaClass}
        />
      ),
      enableNext: isSecondStepActivatable(),
      hideBackButton: true,
      name: i18n.modalWizard.firstStep.stepName,
    },
    {
      canJumpTo: isSecondStepActivatable(),
      component: (
        <ImportJavaClassesWizardSecondStep
          selectedJavaClasses={javaClasses}
          onAddJavaClass={addJavaClass}
          onSelectedJavaClassedFieldsLoaded={updateSelectedClassesFields}
        />
      ),
      enableNext: isThirdStepActivatable(),
      name: i18n.modalWizard.secondStep.stepName,
    },
    {
      canJumpTo: isThirdStepActivatable(),
      component: <ImportJavaClassesWizardThirdStep selectedJavaClasses={javaClasses} />,
      name: i18n.modalWizard.thirdStep.stepName,
      nextButtonText: i18n.modalWizard.thirdStep.nextButtonText,
    },
  ];

  return (
    <ModalWizard
      buttonStyle="secondary"
      buttonText={i18n.modalButton.text}
      buttonDisabledStatus={buttonDisabledStatus}
      buttonTooltipMessage={buttonTooltipMessage}
      className={"import-java-classes"}
      onWizardClose={resetJavaClassState}
      onWizardSave={onWizardFinishing}
      wizardDescription={i18n.modalWizard.description}
      wizardSteps={steps}
      wizardTitle={i18n.modalWizard.title}
    />
  );
};
