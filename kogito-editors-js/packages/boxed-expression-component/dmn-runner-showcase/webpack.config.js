const CopyPlugin = require("copy-webpack-plugin");
const path = require("path");

module.exports = () => {
  return {
    mode: "development",
    entry: {
      index: "./src/index.tsx",
    },
    output: {
      path: path.resolve("./dist"),
      filename: "[name].js",
      chunkFilename: "[name].bundle.js",
    },
    plugins: [
      new CopyPlugin({
        patterns: [
          { from: "./public", to: "./" },
        ],
      }),
    ],
    module: {
      rules: [
        {
          test: /\.s[ac]ss$/i,
          use: ["style-loader", "css-loader", "sass-loader"],
        },
        {
          test: /\.css$/,
          use: ["style-loader", "css-loader"],
        },
        {
          test: /\.js$/,
          enforce: "pre",
          use: ["source-map-loader"],
        },
        {
          test: /\.tsx?$/,
          use: [
            {
              loader: "ts-loader",
              options: {
                compilerOptions: {
                  sourceMap: true,
                },
              },
            },
          ],
        },
      ],
    },
    devtool: "inline-source-map",
    devServer: {
      historyApiFallback: false,
      disableHostCheck: true,
      watchContentBase: true,
      contentBase: [path.join(__dirname, "./dist"), path.join(__dirname, "./public")],
      compress: true,
      port: 4444,
    },
    resolve: {
      fallback: { path: require.resolve("path-browserify") }, // Required for `minimatch`, as Webpack 5 doesn't add polyfills automatically anymore.
      extensions: [".tsx", ".ts", ".js", ".jsx"],
      modules: [path.resolve("../../../node_modules"), path.resolve("../../node_modules"), path.resolve("./node_modules"), path.resolve("./src")],
    },
  };
};
