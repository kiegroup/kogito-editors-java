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
import { render } from "@testing-library/react";
import {ImportJavaClasses} from "../../components";

describe("ImportJavaClasses component tests", () => {

    test("should render ImportJavaClasses Button component", () => {
        const { container } = render(<ImportJavaClasses />);

        expect(container).toMatchSnapshot();
    });

    test("Should show Modal after clicking on the button", () => {
        const { baseElement, getByText } = render(<ImportJavaClasses />);
        const modalWizardButton = getByText("Import Java classes")! as HTMLButtonElement;
        modalWizardButton.click();

        expect(baseElement).toMatchSnapshot();
    });

    test("Should show Modal after clicking on the button", () => {
        const { baseElement, getByText } = render(<ImportJavaClasses />);
        const modalWizardButton = getByText("Import Java classes")! as HTMLButtonElement;
        modalWizardButton.click();
        const cancelButton = getByText("Cancel") as HTMLButtonElement;
        cancelButton.click();

        expect(baseElement).toMatchSnapshot();
    });

});
