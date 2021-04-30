# web编程

## 1.最终项目成果展示demo

话不多说，先放展示结果～

## 2.博客阅读Outline与项目结构

因为整体的博客比较长，可以根据outline进行跳转阅读:D

![截屏2021-04-30 10.47.22](https://tva1.sinaimg.cn/large/008i3skNgy1gq1ktwjthyj30ac0zo0w4.jpg)

##3. 爬虫

### 3.1目标网站分析

[上海交通大学新闻学术网](https://news.sjtu.edu.cn/)

[中国青年网](http://www.youth.cn/)

[网易新闻](https://news.163.com/)

> 查看网页源码查看`Elements`均使用`Chrome`浏览器的`Inspect`功能，在项目实践过程中真情实感地觉得比`view-source`更有用，可以上下对照查看网页信息。

#### 3.1.1种子页面分析

![截屏2021-04-28 13.19.30](https://tva1.sinaimg.cn/large/008i3skNly1gpzgvcybr0j31xb0u0qil.jpg)

<img src="https://tva1.sinaimg.cn/large/008i3skNly1gpzgvb3d3rj31xb0u0kbx.jpg" alt="截屏2021-04-28 13.18.54" style="zoom:50%;" />

<img src="https://tva1.sinaimg.cn/large/008i3skNly1gpzgv98h2rj31n90u0kc8.jpg" alt="截屏2021-04-28 13.21.31" style="zoom:50%;" />

我最后爬取了三个新闻网站的视频，这三个新闻网站分别的方向有学术新闻类（SJTU)，综合新闻类(网易新闻)，政治正确新闻类（中国青年网），分别对这三个网站进行种子页面分析，发现首页上的跳转的Url_list都是使用的`a href=url`进行的跳转。

所以即可以确定`seedURL_format`的标签：

```javascript
var seedURL_format = "$('a')";
```

#### 3.1.2新闻页面分析

以网易新闻为例：

我在分析新闻页面的时候，首先会从`html`页面的`<head></head>`和`<body></body>`两个部分分别入手，这样会使我对目标网站的分析更加地具有逻辑。

<img src="https://tva1.sinaimg.cn/large/008i3skNly1gpzgv5tf2zj31z40nw13e.jpg" alt="截屏2021-04-28 13.27.00" style="zoom:50%;" />

在`<head></head>`中，一般会有我们想要的网页的`meta-data`信息.

<img src="https://tva1.sinaimg.cn/large/008i3skNly1gpzgv3p7jvj31i90u0qni.jpg" alt="截屏2021-04-28 13.29.30" style="zoom:50%;" />

在`<body></body>`中一般会有我们想要的`content`即网页内容的分析，虽然乍一看html的`elements`会有很多，会很难爬，但是实际上有很多`elements`都是`css/js`等的前端页面装饰或者是动态广告的导入，也就是说我们想要的新闻内容的部分反倒在跟这些音频/视频内容对比起来显得更加朴实无华了。

以下面的这个"日美勾结"的新闻为例，我们想要的大部分的content都在`<div class ="post_body">`里面了:D，被这些一个个的`<p></p>`包裹在里面。

<img src="https://tva1.sinaimg.cn/large/008i3skNly1gpzgv25b6rj31i90u0ww4.jpg" alt="截屏2021-04-28 13.35.02" style="zoom:50%;" />

### 3.2爬虫设计与mysql数据库设计

在分析完网页后，结合爬虫设计和我们想要在mysql数据库里填入什么字段就可以正式开始爬虫了。

数据库设计的**字段是什么**，就是我们**爬虫想要什么**。

一般对于新闻数据，我们想要的字段有标题`title`,作者`keywords`,新闻发布日期`publish_date`,描述信息`description`,网页内容`content`，网页链接`url`，网页来源`source_name`,网页编码`source_encoding`，所以这也就是我们数据库DDL中的字段。

#### 3.2.1爬虫设计

##### **Step1:网页Encoding的确定**

在`Chrome`浏览器中输入`document.charset`即可以确定网页的编码，定义在`myEncoding`中。

![截屏2021-04-28 14.19.29](https://tva1.sinaimg.cn/large/008i3skNgy1gq1kzabsf8j30m4028t8r.jpg)

```javascript
var source_name = "上海交大";
var myEncoding = "utf-8";
//种子页面URL
var seedURL = 'https://news.sjtu.edu.cn/';
//mysql写入功能
var mysql= require('/Users/chixinning/Desktop/web_ddl/mysql.js')
```

##### **Step2:jQuery选择器格式的确定**

在确定变量时，我参考了这个链接以确定了jQuery手册。Reference:[jQuery选择器参考手册](https://www.w3school.com.cn/jquery/jquery_ref_selectors.asp)

这一步跟`4.1`部分中的网页源码分析有很强烈的相关，下面以`sjtu`的网站为例子：

这个是`<head></head>`：

<img src="https://tva1.sinaimg.cn/large/008i3skNly1gpzguzdsjij31040emtdg.jpg" alt="截屏2021-04-28 14.43.04" style="zoom:50%;" />

这个是`<body></body>`：

<img src="https://tva1.sinaimg.cn/large/008i3skNly1gpzguwxswij30wa0h8q6a.jpg" alt="截屏2021-04-28 14.45.50" style="zoom:50%;" />

```javascript
var keywords_format = " $('meta[name=\"keywords\"]').eq(0).attr(\"content\")";
var desc_format = " $('meta[name=\"Description\"]').eq(0).attr(\"content\")";
var title_format = " $('meta[name=\"ArticleTitle\"]').eq(0).attr(\"content\")";
var date_format = " $('meta[name=\"PubDate\"]').eq(0).attr(\"content\")";
var author_format = " $('meta[name=\"Author\"]').eq(0).attr(\"content\")";
var content_format = "$('div.Article_content').text()";
```

##### **Step3:url_reg格式的确定**

因为我爬取了三个网页，结合老师上课所讲的中国新闻网例子中`url_reg`中，发现`url_reg`各不相同，如果一昧的使用同一个`url_reg`的话，会导致什么都爬不到的情况。也就是会出现`TypeError:Cannot read property 'length' of null`；

<img src="https://tva1.sinaimg.cn/large/008i3skNly1gpzfyuq7m1j30nk0o2n1e.jpg" alt="截屏2021-04-12 16.35.37" style="zoom:50%;" />

因为`javascript` 不是很好debug，用`console.log()`有时会更好的确定错误。

```javascript
 if (!url_reg.test(myURL)){
           console.log("REG格式检验不通过")
           return; //检验是否符合新闻url的正则表达式
 }
 else{
         	 console.log(myURL);
           newsGet(myURL); //读取新闻页面
        }
```

所以最后我参考了网页上其他处理正则表达式的代码，确定了如下三个`url_reg`的格式。

```javascript
var url_reg = /^((ht|f)tps?):\/\/[\w\-]+(\.[\w\-]+)+([\w\-\.,@?^=%&:\/~\+#]*[\w\-\@?^=%&\/~\+#])?$/;
var url_reg = /^http:\/\/[A-Za-z0-9]+\.[A-Za-z0-9]+[\/=\?%\-&_~`@[\]\':+!]*([^<>\"\"])*$/  ;
var url_reg = /\/(\d{2})\/(\d{4})\/(\d{2})\/([A-Z0-9]{16}).htm/;
```

##### Step4:相关依赖包的确定

```javascript
var fs = require('fs');
var myRequest = require('request')
var myCheerio = require('cheerio')
var myIconv = require('iconv-lite')
```

##### Step5:爬虫整体流程

使用流程图可以将爬虫的具体过程流程展示如下：

![未命名文件](https://tva1.sinaimg.cn/large/008i3skNly1gpzgurrn92j30go0b0aar.jpg)

下面的js代码只给出outline,具体的代码详见网页源码：

````javascript
var headers = {'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.65 Safari/537.36'}
function request(url, callback) {}
//构造一个模仿浏览器的request 模块异步fetchURL
request(seedURL, function(err, res, body){
  //todo
  //读取种子页面
  //解析出种子页面里所有<a href>链接
  //遍历种子页面里所有的<a href>链接
	//规整化所有链接，如果符合新闻URL的正则表达式就爬取
}
function newsGet(myURL) {
  request(myURL, function(err, res, body) {
     var $ = myCheerio.load(html_news, { decodeEntities: true });
     myhtml = html_news;
    //读取具体的新闻页面，构造一个空的fetch对象用于存储数据
    //读取新闻页面中的元素并保存到fetch对象里即可
     var fetch = {};
     fetch.title = "";
     fetch.content = "";
     fetch.publish_date = (new Date()).toFormat("YYYY-MM-DD");
     fetch.url = myURL;
     fetch.source_name = source_name;
     fetch.source_encoding = myEncoding; //编码
     fetch.crawltime = new Date();
    //将fetch对象存入mysql数据库
     mysql.query();
}}
```

##### Step6:爬虫扩展

1. 相同的URL不再继续重复爬取

   对于爬过的数据不再爬，当存入SQL中时，进行SQL检查该URL是否存在即可：或者，更改**URL设置为UniqueKey**也可以。其实本质是这个`qerr`的变化。

   ````javascript
   mysql.query(fetchAddSql, fetchAddSql_Params, function(qerr, vals, fields) {
               if (qerr) {
                   console.log(qerr);
               }
               else{
                   console.log("写入mysql数据库成功！")
               }
           }); //mysql写入
   ````

2. 爬虫定时功能

   ```javascript
   //！定时执行
   var rule = new schedule.RecurrenceRule();
   var times = [0, 12]; //每天2次自动执行
   var times2 = 5; //定义在第几分钟执行
   rule.hour = times;
   rule.minute = times2;
   //定时执行httpGet()函数
   schedule.scheduleJob(rule, function() {
       seedget();//seedget是request()函数的包装
   });
   ```

#### 3.2.2数据库DDL

```SQL
CREATE TABLE `fetches` (
  `id_fetches` int(11)  NOT NULL AUTO_INCREMENT,
  `url` varchar(200) DEFAULT NULL,
  `source_name` varchar(200) DEFAULT NULL,
  `source_encoding` varchar(45) DEFAULT NULL,
  `title` varchar(200) DEFAULT NULL,
  `keywords` varchar(200) DEFAULT NULL,
  `author` varchar(200) DEFAULT NULL,
  `publish_date` date DEFAULT NULL,
  `crawltime` datetime DEFAULT NULL,
  `content` longtext,
  `createtime` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_fetches`),
  UNIQUE KEY `id_fetches_UNIQUE` (`id_fetches`),
  UNIQUE KEY `url_UNIQUE` (`url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

### 3.3存储mysql数据库

在原有爬虫代码的基础上增加这一部分，即可使`node.js`写入数据库。

```mysql
var mysql= require('/Users/chixinning/Desktop/web_ddl/mysql.js')
//执行sql，数据库中fetch表里的url属性是unique的，不会把重复的url内容写入数据库
        mysql.query(fetchAddSql, fetchAddSql_Params, function(qerr, vals, fields) {
            if (qerr) {
                console.log(qerr);
            }
            else{
                console.log("写入mysql数据库成功！")
            }
        }); //mysql写入
```

最终爬取下来的部分数据的展示，应用navicat进行可视化

![截屏2021-04-28 15.50.55](https://tva1.sinaimg.cn/large/008i3skNgy1gpzid3jw22j31oo0u0hdt.jpg)

## 4.搜索网站Web

### 4.1前端

#### 4.1.1前端思路设计

不论是什么项目，前端UI的设计既是一门艺术也是一门学问，从用户浏览的角度出发，可以拥有更符合人性化逻辑设计的UI。

在大二下学期，也就是一年前的专业英语课程中，当时的第一个UI做的比较花，现在一年以后，学习了更多编程内容以后，真情实感地觉得UI做的还是需要以简洁舒适为主。

##### 基础搜索与表格展示

<img src="https://tva1.sinaimg.cn/large/008i3skNgy1gq1kl60jpdj31p90u049i.jpg" alt="截屏2021-04-30 10.39.01" style="zoom:50%;" />

**设计思路：**因为`content`/`desciption`字段都是比较长的text，一次性的显示在前端表格中会显得十分拥挤且不美观。

其中URL可以实现点击跳转到source新闻页。

##### 网页内容详情聚焦

![截屏2021-04-30 10.42.22](https://tva1.sinaimg.cn/large/008i3skNgy1gq1kokssurj31wc0u04e0.jpg)



##### 热力分析时序图展示

通过`mysql`数据库的简单`count`,或者其他新闻文本热度分析工具获得以后，即可展示时序图。

这里时序图的具体展示我选择了新建一个`jquery.min.js`文件，在`index.html`通过导入这个工具脚本`<script src="./jquery.min.js"></script>`实现，防止`index.html`的过度冗余。

在这个时序图的每一个点，都可以具体查看`count`信息，方便用户从统计数字的角度感受新闻关键词个数的变化。

![截屏2021-04-30 10.45.12](https://tva1.sinaimg.cn/large/008i3skNgy1gq1krj60wzj31xt0u0ti4.jpg)



#### 4.1.2express框架前后端相连

在实验中，我尝试了老师课上讲的使用express框架进行前后端相连。

后端的实现的功能是使用js从mysql数据库进行前后缀通配皮配的搜索，再进行渲染到html前端表格中进行展示。其实对于一些静态的网站，有一些网页内容的数据直接被写入到html中，也是我们为什么爬取网站可以获得内容的原理。

###4.2 搜索

其实前后端的搜索本质非常简单：

#### 4.2.1后端：

在express框架的`routes/index.js`路径中:

```javascript
var express = require('express');
var router = express.Router();
// var mysql = require('../mysql.js');

/* GET home page. */
router.get('/', function(req, res, next) {
    res.render('index', { title: 'Express' });
});

router.get('/process_get', function(request, response) {
    //sql字符串和参数
    var fetchSql = "select url,source_name,title,author,publish_date " +
        "from fetches where title like '%" + request.query.title + "%'";
    mysql.query(fetchSql, function(err, result, fields) {
        response.writeHead(200, {
            "Content-Type": "application/json"
        });
        response.write(JSON.stringify(result));
        response.end();
    });
});
module.exports = router;
```

#### 4.2.2前端:

在express框架的`public/index.html`路径中，新建一个`index.html`就可以了，这里不放项目最终具体代码的展示，完整代码可以查看源码~,而搜索部分的核心即是在`html`中引入`<script></script>`的`javascript`脚本

```javascript
 <script>
        $(document).ready(function() {
            $("input:button").click(function() {
                $.get('/process_get?title=' + $("input:text").val(), function(data) {
                    $("#record2").empty();
                    $("#record2").append('<tr class="cardLayout"><td>url</td><td>source_name</td>' +
                        '<td>title</td><td>author</td><td>publish_date</td></tr>');
                    for (let list of data) {
                        let table = '<tr class="cardLayout"><td>';
                        Object.values(list).forEach(element => {
                            table += (element + '</td><td>');
                        });
                        $("#record2").append(table + '</td></tr>');
                    }
                });
            });

        });
    </script>
```

##  5.热度分析

### 5.1 SQL直接统计不同publishDate的count

使用`SQL`进行查询：

【因为这里是web课不是数据库课，就直接使用前后缀匹配的模糊查询进行了:D】

```javascript
select title,content from fetches where title like ‘%上海%';
select title,content from fetches where title like ‘%健康%';
var mysql = require('./mysql.js');
var title = '上海';
var title = '健康';
var select_Sql = "select title,author,publish_date from fetches where title like '%" + title + "%'";

mysql.query(select_Sql, function(qerr, vals, fields) {
    console.log(vals);
});
```

这样存在一个问题，是因为无法统计单篇新闻内容中涉及到搜索词的个数，（即可能一篇文章出现了100个相同的新闻关键词）不是特别符合热度分析的逻辑。

### 5.2 使用python生成数据集进行新闻数据热度分析

虽然web作业是使用的node.js完成大部分代码，但是在热度分析部分，选择更适合于文本热度分析的python进行热度分析生成数据也是一种有用的尝试；使用js读入该数据集，最后再在前端效果的展示，作为本次项目的展示。虽然在真实的应用场景中，如果使用实时的爬虫效果，更好的当然还是采用js实时动态的根据后端数据库的变化，或者使用js进行mysql的分词与search操作，进行前端动态图的实时展示。

将使用`node.js`爬下来的数据导入`mysql`以后，再从`mysql`将爬下来的数据转成`csv`，即可以使用`python`进行热度分析。

> 事实上，如果对标题单纯进行关键词的搜索是没有意义的，虽然作为新闻稿，好的标题的要义是让读者只通过标题就可以了解文章内容，但是，标题毕竟作为新闻数据的特征不具有代表性。

python部分代码如下，详细过程可以查看完整代码中的`py`文件：

```python
import pandas as pd
import re
import jieba
from datetime import datetime
df=pd.DataFrame(pd.read_excel('fets.xls'))
# 处理文本中爬取下来的web杂质，更符合真实的web爬虫使用python的场景
def remove_punctuation(line):
  rule = re.compile(u"[^a-zA-Z0-9\u4e00-\u9fa5]")
  line = rule.sub('',line)
  return line
# 输出[分词后的关键词,文章发布时间]的k,v键值对
key_time_list=[]
for index, row in df.iterrows():
    tmp_dict={}
    # NLP简单处理
    # 这里直接将标题和内容作为合并
    keywords_raw=row['title']+row['content']
    keywords_after=remove_punctuation(keywords_raw)
    # 最简单的jieba分词，这里使用cut_all=True的模式
    keywords_seg=jieba.cut(keywords_after,cut_all=True)
    keywords=",".join(keywords_seg)# keywords这里是"str"类型
    timeStr=row['publish_date']
    # print(timeStr)
    try:
        y = datetime.strptime(timeStr, '%d/%m/%Y')
        if y.month>4:
             y = datetime.strptime(timeStr, '%m/%d/%Y')
    except:
        y = datetime.strptime(timeStr, '%m/%d/%Y')
    tmp_dict[keywords]=y
    key_time_list.append(tmp_dict)
# 使用python字典进行简单的keywords统计，以关键词为上海统计。
# 初始化[分词后的关键词,词频=0]的k,v键值对
time_dict={}
for i in key_time_list_all:
    for v in i.values():
        time_dict[v]=0
for i in key_time_list_all:
    for k in i.keys():
        if (k=='上海'):
            count1=time_dict[i[k]]
            count1=count1+1
            time_dict[i[k]]=count1
# 写出最后统计的结果到.csv文件
import csv
f = open('shanghai_count.csv','w',encoding='utf-8')
csv_writer = csv.writer(f)
csv_writer.writerow(["date","count"])
for k in sorted(time_dict.keys()):
    csv_writer.writerow([k,time_dict[k]])
f.close()
```

生成数据集以后，在前端获取该数据集，进行动态图表的生成。

下面是"健康关键词"的时序展示：在前端中，点击每个点，可以详细查看日期和记数过程。

![截屏2021-04-28 18.56.07](https://tva1.sinaimg.cn/large/008i3skNgy1gpznpqmqstj31ac0l0adu.jpg)

##6. 爬虫项目心得总结

### javaScript是世界上最好的语言！Debug

在爬虫运行时经常会遇到的Error是：`fetch.xxx = undefined.`
或者是`Cannot read property '0' of null`这个错误是我要定义的变量没有定义，根本的原因都可能是我解析我想要的目标失败。而失败的原因有很多：

比如3.2.1小节的`url_reg`的设置很重要，在种子页面获取二级页面url的时候，有些url是`http://`开头，有些是`https:/`/开头，有些是相对路径，有些是//开头；有些url以`.html	`结尾，有些url以`.shtml	`结尾等等。

原来的老师样例代码可能也会有相应的错误的：

- Undefined出错1：


![截屏2021-04-13 20.41.43](https://tva1.sinaimg.cn/large/008i3skNgy1gpzv6btwlkj30zu06gt9r.jpg)

- Undefined出错2：

![截屏2021-04-12 19.48.20](https://tva1.sinaimg.cn/large/008i3skNgy1gpzv6dn0kpj30zw0kuwil.jpg)

![截屏2021-04-12 19.52.07](https://tva1.sinaimg.cn/large/008i3skNgy1gpzv6boucbj30yy0bigoz.jpg)

同时不同二级页面源码可能具有不太一样的id/class，要多开几个报错的网页，读一下网页html源码，横向比较选择更有普遍性的id/name/class/tag用来定位；
有些网页的description、time等信息可能就是缺的，如下图所示：

![截屏2021-04-12 19.49.37](https://tva1.sinaimg.cn/large/008i3skNgy1gpzv6d4p7kj315c0bijtb.jpg)

这时候处理一下代码逻辑，可以先判断某网页基本的content是否读取到，如果有content但其他属性undefined，那很有可能这是一次正确的读取操作

![截屏2021-04-13 20.45.11](https://tva1.sinaimg.cn/large/008i3skNgy1gpzv6cpt1rj313i0cgwh4.jpg)

![截屏2021-04-13 22.09.49](https://tva1.sinaimg.cn/large/008i3skNgy1gpzv6f1hw8j31ac014t8z.jpg)

还有很多针对解析失败undefined的bug都记录在代码中了，爬虫就是需要根据不同的网站特点实时反应，进行灵活调整。

### express脚手架与node.js同步异步

在跟一些同学交流的过程中，发现在不用框架的情况下，用async/await方式将node.js的异步非阻塞式变成阻塞式，等待数据库query执行完毕后将结果写入response，才能执行response.end(). 否则在异步非阻塞式情况下，response.end()会直接执行，这时候query还在等待返回数据，数据被返回后无法再写入response。

虽然我没有遇到这个问题，但是还是要记录一下，进行深入学习和参考～

