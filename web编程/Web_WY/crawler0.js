var myRequest = require('request')
var myCheerio = require('cheerio')
var myURL = 'https://www.zhihu.com/question/28306141'
function request(url, callback) {//request module fetching url
    var options = {
        url: url,  encoding: null, headers: null
    }
    myRequest(options, callback)
}


request(myURL, function (err, res, body) {
    var html = body;  
    var $ = myCheerio.load(html, { decodeEntities: false });
    console.log($.html());            
})  
