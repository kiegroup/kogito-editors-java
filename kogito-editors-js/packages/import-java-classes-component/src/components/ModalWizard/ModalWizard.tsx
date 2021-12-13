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
import { useState, useCallback } from "react";
import { Button, Tooltip, Wizard, WizardStep } from "@patternfly/react-core";

type ModalWizardButtonStyle =
  | "primary"
  | "secondary"
  | "tertiary"
  | "danger"
  | "warning"
  | "link"
  | "plain"
  | "control";

export interface ModalWizardProps {
  /** Text to apply to the Modal button */
  buttonText: string;
  /** Style to apply to the Modal button */
  buttonStyle: ModalWizardButtonStyle;
  /** Icon to apply to the Modal button */
  buttonIcon?: React.ReactNode;
  /** Button disabled status */
  buttonDisabledStatus: boolean;
  /** Button tooltip message */
  buttonTooltipMessage?: string;
  /** Additional className to assign to the Wizard */
  className?: string;
  /** Title of the Modal Wizard */
  wizardTitle: string;
  /** Title of the Modal Wizard */
  wizardDescription: string;
  /** Steps of the Modal Wizard */
  wizardSteps: WizardStep[];
  /** Action to apply at Wizard closure */
  onWizardClose?: () => void;
  /** Action to apply at Wizard final step action button pressed */
  onWizardSave?: () => void;
}

export const ModalWizard = ({
  buttonText,
  buttonStyle,
  buttonIcon,
  buttonDisabledStatus,
  buttonTooltipMessage,
  className,
  wizardTitle,
  wizardDescription,
  wizardSteps,
  onWizardClose,
  onWizardSave,
}: ModalWizardProps) => {
  const [isOpen, setOpen] = useState(false);

  const handleButtonClick = useCallback(() => setOpen((prevState) => !prevState), []);

  const handleWizardClose = useCallback(() => {
    handleButtonClick();
    if (onWizardClose) {
      onWizardClose();
    }
  }, [handleButtonClick, onWizardClose]);

  const handleWizardSave = useCallback(() => {
    handleWizardClose();
    if (onWizardSave) {
      onWizardSave();
    }
  }, [handleWizardClose, onWizardSave]);

  return (
    <>
      {buttonTooltipMessage ? (
        <WizardButtonWithTooltip
          buttonDisabledStatus={buttonDisabledStatus}
          buttonIcon={buttonIcon}
          buttonStyle={buttonStyle}
          buttonText={buttonText}
          buttonTooltipMessage={buttonTooltipMessage}
          onButtonClick={handleButtonClick}
        />
      ) : (
        <WizardButton
          buttonDisabledStatus={buttonDisabledStatus}
          buttonIcon={buttonIcon}
          buttonStyle={buttonStyle}
          buttonText={buttonText}
          onButtonClick={handleButtonClick}
        />
      )}
      {isOpen ? (
        <Wizard
          className={className}
          description={wizardDescription}
          isOpen={isOpen}
          onClose={handleWizardClose}
          onSave={handleWizardSave}
          steps={wizardSteps}
          title={wizardTitle}
        />
      ) : null}
    </>
  );
};

const WizardButton = ({
  buttonDisabledStatus,
  buttonIcon,
  buttonStyle,
  buttonText,
  onButtonClick,
}: {
  buttonDisabledStatus: boolean;
  buttonIcon?: React.ReactNode;
  buttonStyle: ModalWizardButtonStyle;
  buttonText: string;
  onButtonClick: () => void;
}) => {
  return (
    <Button
      data-testid={"modal-wizard-button"}
      icon={buttonIcon}
      isDisabled={buttonDisabledStatus}
      onClick={onButtonClick}
      variant={buttonStyle}
    >
      {buttonText}
    </Button>
  );
};

const WizardButtonWithTooltip = ({
  buttonDisabledStatus,
  buttonTooltipMessage,
  buttonIcon,
  buttonStyle,
  buttonText,
  onButtonClick,
}: {
  buttonDisabledStatus: boolean;
  buttonTooltipMessage: string;
  buttonIcon?: React.ReactNode;
  buttonStyle: "primary" | "secondary" | "tertiary" | "danger" | "warning" | "link" | "plain" | "control";
  buttonText: string;
  onButtonClick: () => void;
}) => {
  return (
    <Tooltip content={buttonTooltipMessage}>
      <Button
        variant={buttonStyle}
        icon={buttonIcon}
        onClick={onButtonClick}
        isAriaDisabled={buttonDisabledStatus}
        data-testid={"modal-wizard-button"}
      >
        {buttonText}
      </Button>
    </Tooltip>
  );
};
