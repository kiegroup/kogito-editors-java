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
import { useState } from "react";
import { Button, Wizard, WizardStep } from "@patternfly/react-core";

export interface ModalWizardProps {
  /** Text to apply to the Modal button */
  buttonText: string;
  /** Style to apply to the Modal button */
  buttonStyle: "primary" | "secondary" | "tertiary" | "danger" | "warning" | "link" | "plain" | "control";
  /** Icon to apply to the Modal button */
  buttonIcon?: React.ReactNode;
  /** Title of the Modal Wizard */
  wizardTitle: string;
  /** Title of the Modal Wizard */
  wizardDescription: string;
  /** Steps of the Modal Wizard */
  wizardSteps: WizardStep[];
}

export const ModalWizard: React.FunctionComponent<ModalWizardProps> = ({
  buttonText,
  buttonStyle,
  buttonIcon,
  wizardTitle,
  wizardDescription,
  wizardSteps,
}: ModalWizardProps) => {
  const [isOpen, setOpen] = useState(false);
  const handleModalToggle = () => setOpen(!isOpen);

  return (
    <>
      <Button variant={buttonStyle} icon={buttonIcon} onClick={handleModalToggle}>
        {buttonText}
      </Button>
      <Wizard
        title={wizardTitle}
        description={wizardDescription}
        steps={wizardSteps}
        onClose={handleModalToggle}
        isOpen={isOpen}
      />
    </>
  );
};
