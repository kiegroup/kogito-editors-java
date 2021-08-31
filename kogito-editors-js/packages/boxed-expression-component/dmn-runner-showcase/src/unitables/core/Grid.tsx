import { Bridge, joinName } from "uniforms";
import * as React from "react";
import { AutoField } from "./AutoField";
import { DataType } from "boxed-expression-component/dist/api";
import { DmnRunnerClause } from "../../DmnRunnerTable/DmnRunnerTableTypes";
import { DecisionResult, Result } from "../dmn";

export class Grid {
  constructor(private readonly bridge: Bridge) {}

  public getBridge() {
    return this.bridge;
  }

  public removeInputName(fullName: string) {
    return fullName.match(/\./) ? fullName.split(".").slice(1).join(".") : fullName;
  }

  public getDataTypeProps(type: string | undefined) {
    let extractedType = (type ?? "").split("FEEL:").pop();
    if ((extractedType?.length ?? 0) > 1) {
      extractedType = (type ?? "").split(":").pop()?.split("}").join("").trim();
    }
    switch (extractedType) {
      case "<Undefined>":
        return { dataType: DataType.Undefined, width: 150 };
      case "Any":
        return { dataType: DataType.Any, width: 150 };
      case "boolean":
        return { dataType: DataType.Boolean, width: 150 };
      case "context":
        return { dataType: DataType.Context, width: 150 };
      case "date":
        return { dataType: DataType.Date, width: 180 };
      case "date and time":
        return { dataType: DataType.DateTime, width: 282 };
      case "days and time duration":
        return { dataType: DataType.DateTimeDuration, width: 150 };
      case "number":
        return { dataType: DataType.Number, width: 150 };
      case "string":
        return { dataType: DataType.String, width: 150 };
      case "time":
        return { dataType: DataType.Time, width: 150 };
      case "years and months duration":
        return { dataType: DataType.YearsMonthsDuration, width: 150 };
      default:
        return { dataType: (extractedType as DataType) ?? DataType.Undefined, width: undefined };
    }
  }

  public deepGenerateBoxed(fieldName: any, parentName = ""): any {
    const joinedName = joinName(parentName, fieldName);
    const field = this.bridge.getField(joinedName);

    if (field.type === "object") {
      const insideProperties = this.bridge.getSubfields(joinedName).reduce((acc: any[], subField: string) => {
        const field = this.deepGenerateBoxed(subField, joinedName);
        if (field.insideProperties) {
          return [...acc, ...field.insideProperties];
        }
        return [...acc, field];
      }, []);
      return {
        ...this.getDataTypeProps(field["x-dmn-type"]),
        insideProperties,
        name: joinedName,
      };
    }
    return {
      ...this.getDataTypeProps(field["x-dmn-type"]),
      name: this.removeInputName(joinedName),
      cellDelegate: ({ formId }: any) => <AutoField key={joinedName} name={joinedName} form={formId} />,
    };
  }

  public generateBoxedInputs(): DmnRunnerClause[] {
    let myGrid: DmnRunnerClause[] = [];
    const subfields = this.bridge.getSubfields();
    const inputs = subfields.reduce(
      (acc: DmnRunnerClause[], fieldName: string) => [...acc, this.deepGenerateBoxed(fieldName)],
      [] as DmnRunnerClause[]
    );
    if (inputs.length > 0) {
      myGrid = inputs;
    }
    return myGrid;
  }

  public generateBoxedOutputs(
    schema: any,
    decisionResults: Array<DecisionResult[] | undefined>
  ): [Map<string, DmnRunnerClause>, Result[]] {
    const outputTypeMap = Object.entries(schema?.definitions?.OutputSet?.properties ?? []).reduce(
      (acc: Map<string, DataType>, [name, properties]: [string, any]) => {
        if (properties["x-dmn-type"]) {
          acc.set(name, this.getDataTypeProps(properties["x-dmn-type"]).dataType);
        } else {
          const path = properties.$ref.split("/").slice(1); // remove #
          const type = path.reduce((acc: any, property: string) => acc[property], schema);
          acc.set(name, this.getDataTypeProps(type["x-dmn-type"]).dataType);
        }

        return acc;
      },
      new Map<string, DataType>()
    );

    const outputSet = decisionResults.reduce(
      (acc: Map<string, DmnRunnerClause>, decisionResult: DecisionResult[] | undefined) => {
        if (decisionResult) {
          decisionResult.forEach(({ decisionName }) => {
            acc.set(decisionName, {
              name: decisionName,
              dataType: outputTypeMap.get(decisionName) ?? DataType.Undefined,
            });
          });
        }
        return acc;
      },
      new Map<string, DmnRunnerClause>()
    );

    const outputEntries = decisionResults.reduce((acc: Result[], decisionResult: DecisionResult[] | undefined) => {
      if (decisionResult) {
        const outputResults = decisionResult.map(({ result }) => {
          if (result === null) {
            return "null";
          }
          if (result === true) {
            return "true";
          }
          if (result === false) {
            return "false";
          }
          return result;
        });
        return [...acc, outputResults];
      }
      return acc;
    }, []);

    return [outputSet, outputEntries];
  }
}
