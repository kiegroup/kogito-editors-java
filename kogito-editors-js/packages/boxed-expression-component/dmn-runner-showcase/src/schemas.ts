export const simpleCustomDataType = {
  definitions: {
    OutputSet: {
      type: "object",
      properties: {
        customDataType: {
          $ref: "#/definitions/tCustom",
        },
      },
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_6BB79E50-C30C-40DC-9354-897CE17749F3 : OutputSet }",
      "x-dmn-descriptions": {},
    },
    InputSet: {
      required: ["customDataType"],
      type: "object",
      properties: {
        customDataType: {
          $ref: "#/definitions/tCustom",
        },
      },
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_6BB79E50-C30C-40DC-9354-897CE17749F3 : InputSet }",
      "x-dmn-descriptions": {},
    },
    tCustom: {
      type: "object",
      properties: {
        tAge: {
          type: "number",
          "x-dmn-type": "FEEL:number",
        },
      },
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_6BB79E50-C30C-40DC-9354-897CE17749F3 : tCustom }",
    },
  },
  $ref: "#/definitions/InputSet",
};

export const complexCustomDataType = {
  definitions: {
    InputSet: {
      required: ["customDataType"],
      type: "object",
      properties: {
        customDataType: {
          $ref: "#/definitions/tCustom",
        },
      },
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_6BB79E50-C30C-40DC-9354-897CE17749F3 : InputSet }",
      "x-dmn-descriptions": {},
    },
    tCustom__tComplex: {
      type: "object",
      properties: {
        tAge: {
          type: "number",
          "x-dmn-type": "FEEL:number",
        },
      },
    },
    tCustom: {
      type: "object",
      properties: {
        tComplex: {
          $ref: "#/definitions/tCustom__tComplex",
        },
      },
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_6BB79E50-C30C-40DC-9354-897CE17749F3 : tCustom }",
    },
    OutputSet: {
      type: "object",
      properties: {
        customDataType: {
          $ref: "#/definitions/tCustom",
        },
      },
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_6BB79E50-C30C-40DC-9354-897CE17749F3 : OutputSet }",
      "x-dmn-descriptions": {},
    },
  },
  $ref: "#/definitions/InputSet",
};

export const normalDataType = {
  definitions: {
    InputSet: {
      required: ["stringInput"],
      type: "object",
      properties: {
        stringInput: {
          type: "string",
          "x-dmn-type": "FEEL:string",
        },
      },
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_6BB79E50-C30C-40DC-9354-897CE17749F3 : InputSet }",
      "x-dmn-descriptions": {},
    },
    OutputSet: {
      type: "object",
      properties: {
        stringInput: {
          type: "string",
          "x-dmn-type": "FEEL:string",
        },
      },
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_6BB79E50-C30C-40DC-9354-897CE17749F3 : OutputSet }",
      "x-dmn-descriptions": {},
    },
  },
  $ref: "#/definitions/InputSet",
};

export const multipleInputs = {
  definitions: {
    OutputSet: {
      type: "object",
      properties: {
        stringInput: {
          type: "string",
          "x-dmn-type": "FEEL:string",
        },
        numberInput: {
          type: "number",
          "x-dmn-type": "FEEL:number",
        },
        dateAndTimeInput: {
          format: "date-time",
          type: "string",
          "x-dmn-type": "FEEL:date and time",
        },
      },
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_6BB79E50-C30C-40DC-9354-897CE17749F3 : OutputSet }",
      "x-dmn-descriptions": {},
    },
    InputSet: {
      required: ["stringInput", "numberInput", "dateAndTimeInput"],
      type: "object",
      properties: {
        stringInput: {
          type: "string",
          "x-dmn-type": "FEEL:string",
        },
        numberInput: {
          type: "number",
          "x-dmn-type": "FEEL:number",
        },
        dateAndTimeInput: {
          format: "date-time",
          type: "string",
          "x-dmn-type": "FEEL:date and time",
        },
      },
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_6BB79E50-C30C-40DC-9354-897CE17749F3 : InputSet }",
      "x-dmn-descriptions": {},
    },
  },
  $ref: "#/definitions/InputSet",
};

export const customWithMultiple = {
  definitions: {
    tCustom: {
      type: "object",
      properties: {
        tAge: {
          type: "number",
          "x-dmn-type": "FEEL:number",
        },
        tName: {
          type: "string",
          "x-dmn-type": "FEEL:string",
        },
      },
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_8AC41608-8F62-417C-AF24-706D60E0C1DA : tCustom }",
    },
    OutputSet: {
      type: "object",
      properties: {
        customInput1: {
          $ref: "#/definitions/tCustom",
        },
      },
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_8AC41608-8F62-417C-AF24-706D60E0C1DA : OutputSet }",
      "x-dmn-descriptions": {},
    },
    InputSet: {
      required: ["customInput1"],
      type: "object",
      properties: {
        customInput1: {
          $ref: "#/definitions/tCustom",
        },
      },
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_8AC41608-8F62-417C-AF24-706D60E0C1DA : InputSet }",
      "x-dmn-descriptions": {},
    },
  },
  $ref: "#/definitions/InputSet",
};

export const multipleCustomNormal = {
  definitions: {
    OutputSet: {
      type: "object",
      properties: {
        customInput: {
          $ref: "#/definitions/tCustom",
        },
        anotherInput: {
          $ref: "#/definitions/tAnother",
        },
      },
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_8AC41608-8F62-417C-AF24-706D60E0C1DA : OutputSet }",
      "x-dmn-descriptions": {},
    },
    tCustom: {
      type: "object",
      properties: {
        tName: {
          type: "string",
          "x-dmn-type": "FEEL:string",
        },
      },
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_8AC41608-8F62-417C-AF24-706D60E0C1DA : tCustom }",
    },
    InputSet: {
      required: ["customInput", "anotherInput"],
      type: "object",
      properties: {
        customInput: {
          $ref: "#/definitions/tCustom",
        },
        anotherInput: {
          $ref: "#/definitions/tAnother",
        },
      },
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_8AC41608-8F62-417C-AF24-706D60E0C1DA : InputSet }",
      "x-dmn-descriptions": {},
    },
    tAnother: {
      type: "object",
      properties: {
        tAge: {
          type: "number",
          "x-dmn-type": "FEEL:number",
        },
      },
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_8AC41608-8F62-417C-AF24-706D60E0C1DA : tAnother }",
    },
  },
  $ref: "#/definitions/InputSet",
};

export const multipleCustomEnum = {
  definitions: {
    tCustom: {
      type: "object",
      properties: {
        tName: {
          type: "string",
          "x-dmn-type": "FEEL:string",
        },
      },
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_8AC41608-8F62-417C-AF24-706D60E0C1DA : tCustom }",
    },
    tAnother__tCountry: {
      enum: ["Brazil", "US"],
      type: "string",
      "x-dmn-type": "FEEL:string",
      "x-dmn-allowed-values": '"Brazil", "US"',
    },
    tAnother: {
      type: "object",
      properties: {
        tCountry: {
          $ref: "#/definitions/tAnother__tCountry",
        },
      },
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_8AC41608-8F62-417C-AF24-706D60E0C1DA : tAnother }",
    },
    OutputSet: {
      type: "object",
      properties: {
        customInput: {
          $ref: "#/definitions/tCustom",
        },
        anotherInput: {
          $ref: "#/definitions/tAnother",
        },
      },
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_8AC41608-8F62-417C-AF24-706D60E0C1DA : OutputSet }",
      "x-dmn-descriptions": {},
    },
    InputSet: {
      required: ["customInput", "anotherInput"],
      type: "object",
      properties: {
        customInput: {
          $ref: "#/definitions/tCustom",
        },
        anotherInput: {
          $ref: "#/definitions/tAnother",
        },
      },
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_8AC41608-8F62-417C-AF24-706D60E0C1DA : InputSet }",
      "x-dmn-descriptions": {},
    },
  },
  $ref: "#/definitions/InputSet",
};
