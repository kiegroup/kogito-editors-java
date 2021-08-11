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
import "@patternfly/react-core/dist/styles/base.css";
import * as React from "react";
import { useState } from "react";
import * as ReactDOM from "react-dom";
import "./index.css";
import {
  ContextProps,
  DecisionTableProps,
  ExpressionProps,
  FunctionProps,
  InvocationProps,
  ListProps,
  LiteralExpressionProps,
  RelationProps,
} from "../../dist/api";
import { DmnAutoTable } from "./unitables";

export const App: React.FunctionComponent = () => {
  const schema = {
    definitions: {
      InputSet: {
        required: ["InputData-1"],
        type: "object",
        properties: {
          "InputData-1": {
            "x-dmn-type": "FEEL:Any",
          },
        },
        "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_1481BD07-15EA-41EC-8612-8D5691B7A74E : InputSet }",
        "x-dmn-descriptions": {},
      },
      OutputSet: {
        type: "object",
        properties: {
          "Decision-1": {
            "x-dmn-type": "FEEL:Any",
          },
          "InputData-1": {
            "x-dmn-type": "FEEL:Any",
          },
        },
        "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_1481BD07-15EA-41EC-8612-8D5691B7A74E : OutputSet }",
        "x-dmn-descriptions": {},
      },
    },
    $ref: "#/definitions/InputSet",
  };

  //Defining global function that will be available in the Window namespace and used by the BoxedExpressionEditor component
  window.beeApi = {
    resetExpressionDefinition: (definition: ExpressionProps) => definition,
    broadcastLiteralExpressionDefinition: (definition: LiteralExpressionProps) => definition,
    broadcastRelationExpressionDefinition: (definition: RelationProps) => definition,
    broadcastContextExpressionDefinition: (definition: ContextProps) => definition,
    broadcastListExpressionDefinition: (definition: ListProps) => definition,
    broadcastInvocationExpressionDefinition: (definition: InvocationProps) => definition,
    broadcastFunctionExpressionDefinition: (definition: FunctionProps) => definition,
    broadcastDecisionTableExpressionDefinition: (definition: DecisionTableProps) => definition,
  };

  const [tableData, setTableData] = useState([]);
  const [formError, setFormError] = useState(false);

  return (
    <div className="showcase">
      <div className="boxed-expression">
        <DmnAutoTable
          schema={schema}
          tableData={tableData}
          setTableData={setTableData}
          results={[]}
          formError={formError}
          setFormError={setFormError}
        />
      </div>
    </div>
  );
};

ReactDOM.render(<App />, document.getElementById("root"));
