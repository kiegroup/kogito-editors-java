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

export enum DMNSimpleType {
  NUMBER = "number",
  STRING = "string",
  BOOLEAN = "boolean",
  DURATION_DAYS_TIME = "days and time duration",
  DURATION_YEAR_MONTH = "years and months duration",
  TIME = "time",
  DATE_TIME = "date and time",
  ANY = "Any",
  DATE = "date",
  CONTEXT = "context"
}
//TODO: TO be checked (and extend to simple types ?)
export const JAVA_TO_DMN_MAP = {
  /* Number types */
  AtomicInteger: DMNSimpleType.NUMBER,
  AtomicLong: DMNSimpleType.NUMBER,
  BigDecimal: DMNSimpleType.NUMBER,
  BigInteger: DMNSimpleType.NUMBER,
  Byte: DMNSimpleType.NUMBER,
  Double: DMNSimpleType.NUMBER,
  DoubleAccumulator: DMNSimpleType.NUMBER,
  DoubleAdder: DMNSimpleType.NUMBER,
  Float: DMNSimpleType.NUMBER,
  Integer: DMNSimpleType.NUMBER,
  Long: DMNSimpleType.NUMBER,
  LongAccumulator: DMNSimpleType.NUMBER,
  LongAdder: DMNSimpleType.NUMBER,
  Number: DMNSimpleType.NUMBER,
  Short: DMNSimpleType.NUMBER,
  Striped64: DMNSimpleType.NUMBER,
  /* String types */
  Character: DMNSimpleType.STRING,
  String: DMNSimpleType.STRING,
  LocalDate: DMNSimpleType.DATE,
  LocalTime: DMNSimpleType.TIME,
  OffsetTime: DMNSimpleType.TIME,
  ZonedDateTime: DMNSimpleType.DATE_TIME,
  OffsetDateTime: DMNSimpleType.DATE_TIME,
  LocalDateTime: DMNSimpleType.DATE_TIME,
  Date: DMNSimpleType.DATE_TIME,
  Duration: DMNSimpleType.DURATION_DAYS_TIME,
  ChronoPeriod: DMNSimpleType.DURATION_YEAR_MONTH,
  /* Boolean */
  Boolean: DMNSimpleType.BOOLEAN,
};
