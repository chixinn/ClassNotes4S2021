var express = require('express');
var app = express();
var bodyParser = require('body-parser');
// 创建 application/x-www-form-urlencoded 编码解析
var urlencodedParser = bodyParser.urlencoded({ extended: false })
app.use('/public', express.static('public'));
app.get('/index', function(req, res) {
    res.sendFile(__dirname + "/public/" + "4.21.html");
})
app.post('/process_post', urlencodedParser, function(req, res) {
    // 输出 JSON 格式
    var response = {
        "title": req.body.title,
        "author": req.body.author
    };
    console.log(response);
    res.end(JSON.stringify(response));
})
var server = app.listen(8081, function() {
    var host = server.address().address
    var port = server.address().port
    console.log("应用实例，访问地址为 http://%s:%s", host, port)

})