const path = require("path");

module.exports = {
  entry: {
    "api/index": "./src/api/index.ts",
    "components/index": "./src/components/index.ts",
    "context/index": "./src/context/index.ts",
    "hooks/index": "./src/hooks/index.ts",
    "index": "./src/index.tsx"
  },
  output: {
    path: path.resolve("dist"),
    filename: `[name].js`,
    library: {
      type: "umd",
      name: `__KIE__BoxedExpressionComponent__`,
    },
  },

  module: {
    rules: [
      {
        test: /\.tsx?$/i,
        loader: "ts-loader",
        options: {
          configFile: path.resolve("./tsconfig.json"),
          compilerOptions: {
            declaration: true,
            outDir: "dist",
          },
        },
      },
      {
        test: /\.css$/,
        use: ["style-loader", "css-loader"],
      },
    ],
  },
}

