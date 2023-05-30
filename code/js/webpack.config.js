const webpack = require('webpack');
const path = require('path');

module.exports = {
    entry: './src/react-components/index.tsx',
    output: {
        filename: 'main.js',
        path: path.resolve(__dirname, 'public'),
    },
    mode: "production",
    resolve: {
        extensions: [".js", ".ts", ".tsx",".css"]
    },
    devServer: {
        port: process.env.PORT ? process.env.PORT : 3000,
        historyApiFallback: true,
        allowedHosts: [".ngrok-free.app"],
        proxy: {
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true
            }
        }
    },
    plugins: [
        new webpack.DefinePlugin({
            /** For testing production, comment the line to not use **/
            //"process.env.NGROK_URI": JSON.stringify("http://localhost:3000"),
            "process.env.NGROK_URI": JSON.stringify(process.env.NGROK_URI)
        }),
    ],
    module: {
        rules: [
            {
                test: /\.(png|jpg|svg|jpeg|gif|ico)$/,
                use: {
                    loader: 'url-loader',
                },
                exclude: /node_modules/
            },
            {
                test: /\.tsx?$/,
                use: 'ts-loader',
                exclude: /node_modules/
            },
            {
                test: /\.css$/i,
                use: ["css-loader"],
                exclude: /node_modules/
            },

            {
                test: /\.scss$/,
                use: [
                    {
                        loader: "css-loader",
                        options: {
                            sourceMap: true,
                            importLoader: 2
                        }
                    },
                    {
                        loader: 'sass-loader',
                        options: {
                            sourceMap: true
                        }
                    },
                    'import-glob-loader'
                ]
            }
        ]
    }
}
