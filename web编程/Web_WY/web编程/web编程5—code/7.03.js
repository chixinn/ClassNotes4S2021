var express = require('express');
var mysql = require('./mysql.js')
var app = express();
//app.use(express.static('public'));
app.get('/7.03.html', function(req, res) {
    res.sendFile(__dirname + "/" + "7.03.html");
})
app.get('/7.04.html', function(req, res) {
    res.sendFile(__dirname + "/" + "7.04.html");
})
app.get('/process_get', function(req, res) {
    res.writeHead(200, { 'Content-Type': 'text/html;charset=utf-8' }); //设置res编码为utf-8
    //sql字符串和参数
    var fetchSql = "select url,source_name,title,author,publish_date from fetches where title like '%" +
        req.query.title + "%'";
    mysql.query(fetchSql, function(err, result, fields) {
        console.log(result);
        res.end(JSON.stringify(result));
    });
})
var server = app.listen(8080, function() {
    console.log("访问地址为 http://127.0.0.1:8080/7.03.html")

})