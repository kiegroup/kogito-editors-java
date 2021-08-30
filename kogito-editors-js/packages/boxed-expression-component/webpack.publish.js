const path = require("path");

module.exports = {
  devtool: "inline-source-map",
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
    },
  },
  externals: [/^react.*/, /^@patternfly\/.+$/i],

  plugins: [
    new MiniCssExtractPlugin({
      filename: `[name].css`,
      chunkFilename: `[name].[id].css]`,
    }),
  ],

  module: {
    rules: [
      {
        test: /\.js$/,
        enforce: "pre",
        use: ["source-map-loader"],
      },
      {
        test: /\.tsx?$/i,
        loader: "ts-loader",
        options: {
          configFile: path.resolve("./tsconfig.json"),
        },
      },
      {
        test: /\.css$/,
        use: [MiniCssExtractPlugin.loader, "css-loader"],
      },
      {
        test: /\.(woff(2)?|ttf|eot)(\?v=\d+\.\d+\.\d+)?$/,
        use: [
          {
            loader: "file-loader",
            options: {
              name: "[name].[ext]",
              outputPath: "fonts",
            },
          },
        ],
      },
    ],
  },
  resolve: {
    extensions: [".tsx", ".ts", ".js", ".jsx"],
    modules: [path.resolve("../../node_modules"), path.resolve("../node_modules"), path.resolve("./src")],
  },
}

