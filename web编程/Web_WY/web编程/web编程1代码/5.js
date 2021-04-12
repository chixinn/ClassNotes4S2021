var myRequest = require('request')
var myCheerio = require('cheerio')
var myURL = 'https://www.ecnu.edu.cn/e5/bc/c1950a255420/page.htm'
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
    //console.log("title: " + $('title').text());     
    //console.log("description: " + $('meta[name="description"]').eq(0).attr("content"));
    
})   
