var http = require('http');
var fs = require('fs');
var url = require('url');
var mysql = require('./mysql.js');
http.createServer(function(request, response) {
    var pathname = url.parse(request.url).pathname;
    var params = url.parse(request.url, true).query;
    fs.readFile(pathname.substr(1), function(err, data) {
        response.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
        if ((params.title === undefined) && (data !== undefined))
            response.write(data.toString());
        else {
            response.write(JSON.stringify(params));
            var select_Sql = "select title,author,publish_date from fetches where title like '%" +
                params.title + "%'";
            mysql.query(select_Sql, function(qerr, vals, fields) {
                console.log(vals);
            });
        }
        response.end();
    });
}).listen(8080);
console.log('Server running at http://127.0.0.1:8080/');