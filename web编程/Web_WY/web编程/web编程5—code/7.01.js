var mysql = require('./mysql.js');
var title = '新冠';
var select_Sql = "select title,author,publish_date from fetches where title like '%" + title + "%'";

mysql.query(select_Sql, function(qerr, vals, fields) {
    console.log(vals);
});