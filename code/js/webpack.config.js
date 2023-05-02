const webpack = require('webpack');

module.exports = {
    entry: './src/react-components/index.tsx',
    mode: "development",
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
