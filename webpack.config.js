const webpack = require('webpack');
const path = require('path');

module.exports = {
    entry: './src/main/js/app.react.js',
    devtool: 'sourcemaps',
    cache: true,
    debug: true,
    output: {
        path: __dirname,
        filename: './src/main/resources/static/js/built/bundle.js'
    },
    module: {
        loaders: [
            {
                test: path.join(__dirname, '.'),
                exclude: /(node_modules)/,
                loader: 'babel-loader',
                query: {
                    cacheDirectory: true,
                    presets: ['es2015', 'react']
                }
            }
        ]
    },
    plugins: (() => {
        // props to https://gist.github.com/rafrex/f568711b86a09c8e4eae8fbe1eb7aeab/ for the idea
        if (process.argv.indexOf('-p') !== -1) {
            return [
                new webpack.DefinePlugin({
                    'process.env': {
                        NODE_ENV: JSON.stringify('production'),
                    },
                }),
                new webpack.optimize.UglifyJsPlugin(),
            ];
        }
        return [];
    })()
};
