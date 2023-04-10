module.exports = {
    mode: "development",
    resolve: {
        extensions: [".js", ".ts", ".tsx",".css"]
    },
    devServer: {
        port: process.env.PORT ? process.env.PORT : 3000,
        historyApiFallback: true,
        proxy: {
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true,
            }
        }
    },
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