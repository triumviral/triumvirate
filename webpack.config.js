const webpack = require('webpack');
const ForkTsCheckerWebpackPlugin = require('fork-ts-checker-webpack-plugin');
const path = require('path');
module.exports = {
    mode: 'development',
    entry: './src/main/js/index.tsx',
    devtool: 'source-map',
    module: {
        rules: [
            {
                test: /\.tsx?$/,
                use: [{
                    loader: "ts-loader",
                    options: {
                        transpileOnly: false,
                    },
                }],
                exclude: /node_modules|\.d\.ts$/,

            },
            /* {
                test: /\.d\.ts$/,
                use: 'ignore-loader',
            }, */
            {
                test: /\.d\.ts$/,
                use: [{
                    loader: 'babel-loader',
                    options: {
                        presets: [
                            ['@babel/preset-typescript'],
                        ],
                    },
                }],
                exclude: /node_modules/,
            },
            {
                test: /\.css$/i,
                use: ["style-loader", "css-loader", "postcss-loader"],
            },
        ],
    },
    resolve: {
        extensions: ['.tsx', '.ts', '.js', '.d.ts'],
    },
    output: {
        filename: '[name].js',
        path: path.resolve(__dirname, 'dist'),
        clean: true,
    },
    plugins: [
        //new ForkTsCheckerWebpackPlugin(),
    ],
};
