var exec = require('cordova/exec');

exports.select = function (success, error, dbName, table) {
    exec(success, error, 'SQL2JSON', 'select', [dbName, table]);
};