'use strict';

const path = require('path'),
  fs = require('fs'),
  webpack = require('webpack'),
  merge = require('webpack-merge'),
  HtmlWebpackPlugin = require('html-webpack-plugin'),
  ExtractTextPlugin = require('extract-text-webpack-plugin'),
  CopyWebpackPlugin = require('copy-webpack-plugin');

let TARGET = process.env.npm_lifecycle_event;

// Determinate application environment
let configPath = `${__dirname}/src/config`;
let envPos = process.argv.indexOf('--env');
let env = (envPos !== -1 && fs.existsSync(`${configPath}/config.${process.argv[++envPos]}.json`)) ? process.argv[envPos] : 'dev';

let common = {
  entry: path.join(__dirname, 'src/app.js'),

  module: {
    loaders: [{
      test: /\.js$/,
      loaders: ['ng-annotate-loader', 'babel-loader'],
      exclude: /node_modules/
    },
      {
        test: /\.html$/,
        loader: 'raw-loader'
      },
      {
        test: /\.css$/,
        loaders: ['style-loader', 'css-loader?importLoaders=1']
      },
      {
        test: /\.json$/,
        loader: 'json-loader'
      },
      {
        test: /\.(png|jpg|jpeg|gif|svg|woff|woff2|ttf|eot)$/,
        loader: 'file-loader'
      }]
  },

  plugins: [
    new HtmlWebpackPlugin({
      template: './src/public/index.html',
      inject: 'body'
    }),

    new webpack.DefinePlugin({
      'process.env': {
        'ENV_NAME': JSON.stringify(env)
      }
    }),

    new CopyWebpackPlugin([
      {from: './src/i18n'}
    ])
  ],

  devServer: {
    contentBase: './src/public'
  },

  resolve: {
    alias: {
      'app.config': `${configPath}/config.${env}.json`
    }
  }
};

// Development
if (TARGET !== undefined && TARGET.startsWith('serve')) {
  module.exports = merge.smart(common, {
    module: {
      loaders: [
        {
          test: /\.css$/,
          loaders: ['style-loader', 'css-loader?importLoaders=1']
        }]
    },
    devtool: 'cheap-module-eval-source-map'
  });
}

// Production
if (TARGET !== undefined && TARGET.startsWith('build')) {
  module.exports = merge.smart(common, {
    output: {
      path: __dirname + '/dist',
      publicPath: '/',
      filename: '[name].[hash].js',
      chunkFilename: '[name].[hash].js'
    },

    module: {
      loaders: [
        {
          test: /\.css$/,
          loader: ExtractTextPlugin.extract('style-loader', ['css-loader?importLoaders=1'])
        }
      ]
    },

    plugins: [
      new webpack.NoErrorsPlugin(),
      new webpack.optimize.DedupePlugin(),
      new webpack.optimize.UglifyJsPlugin(),
      new CopyWebpackPlugin([{
        from: __dirname + '/src/public'
      }]),
      new ExtractTextPlugin('[name].[hash].css')
    ],
  });
}

