/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import "@patternfly/react-core/dist/styles/base.css";
import React, { useCallback, useState } from "react";
import ReactDOM from "react-dom";
import "./index.css";
import { ImportJavaClasses } from "./import_java_classes";

const Showcase: React.FunctionComponent = () => {
  const LSP_SERVER_NOT_AVAILABLE = "Java LSP Server is not available. Please install Java Extension";
  const [buttonDisableStatus, setButtonDisableStatus] = useState(true);
  const [buttonTooltipMessage, setButtonTooltipMessage] = useState(LSP_SERVER_NOT_AVAILABLE);
  const onSelectChange = useCallback((event) => setButtonDisableStatus(event.target.value === "true"), []);
  const onInputChange = useCallback((event) => setButtonTooltipMessage(event.target.value), []);

  return (
    <div className="showcase">
      <div className="menu">
        <h1>Import Java classes button state</h1>
        <select onChange={onSelectChange}>
          <option value="true">Disabled</option>
          <option value="false">Enabled</option>
        </select>
        <h1>Tooltip Message (Optional)</h1>
        <input value={buttonTooltipMessage} onChange={onInputChange} />
      </div>
      <div className="import-java-classes">
        <ImportJavaClasses buttonDisabledStatus={buttonDisableStatus} buttonTooltipMessage={buttonTooltipMessage} />
      </div>
    </div>
  );
};

ReactDOM.render(<Showcase />, document.getElementById("root"));
