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

import { DataType } from "../../api";

/**
 * The constant below contains all built-in types currently supported by the Boxed Expression Editor
 * These values are used for testing purpose, and by the showcase
 */
export const dataTypeProps = [
  { typeRef: "Undefined", name: "<Undefined>", isCustom: false },
  { typeRef: "Any", name: "Any", isCustom: false },
  { typeRef: "Boolean", name: "boolean", isCustom: false },
  { typeRef: "Context", name: "context", isCustom: false },
  { typeRef: "Date", name: "date", isCustom: false },
  { typeRef: "DateTime", name: "date and time", isCustom: false },
  { typeRef: "DateTimeDuration", name: "days and time duration", isCustom: false },
  { typeRef: "Number", name: "number", isCustom: false },
  { typeRef: "String", name: "string", isCustom: false },
  { typeRef: "Time", name: "time", isCustom: false },
  { typeRef: "YearsMonthsDuration", name: "years and months duration", isCustom: false },
  { typeRef: "tPerson", name: "tPerson", isCustom: true },
];

/**
 * The constant below contains an example of PMML params to be used for testing purpose, and by the showcase
 */
export const pmmlParams = [
  {
    document: "document",
    modelsFromDocument: [
      { model: "model", parametersFromModel: [{ id: "p1", name: "p-1", dataType: DataType.Number }] },
    ],
  },
  {
    document: "mining pmml",
    modelsFromDocument: [
      {
        model: "MiningModelSum",
        parametersFromModel: [
          { id: "i1", name: "input1", dataType: DataType.Any },
          { id: "i2", name: "input2", dataType: DataType.Any },
          { id: "i3", name: "input3", dataType: DataType.Any },
        ],
      },
    ],
  },
  {
    document: "regression pmml",
    modelsFromDocument: [
      {
        model: "RegressionLinear",
        parametersFromModel: [
          { id: "i1", name: "i1", dataType: DataType.Number },
          { id: "i2", name: "i2", dataType: DataType.Number },
        ],
      },
    ],
  },
];
