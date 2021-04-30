# 要添加一个新单元，输入 '# %%'
# 要添加一个新的标记单元，输入 '# %% [markdown]'
# %%
import pandas as pd
import re
import jieba
from datetime import datetime
df=pd.DataFrame(pd.read_excel('fets.xls'))


# %%
df.sample()


# %%
df.info()


# %%
def remove_punctuation(line):
  rule = re.compile(u"[^a-zA-Z0-9\u4e00-\u9fa5]")
  line = rule.sub('',line)
  return line


# %%
key_time_list=[]
for index, row in df.iterrows():
    tmp_dict={}
    keywords_raw=row['title']+row['content']
    keywords_after=remove_punctuation(keywords_raw)
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


# %%
key_time_list_all=[]
for i in key_time_list:
    for j in i.keys():
        keywords_list=j.split(",")
        for k in keywords_list:
            tmp_dict={}
            tmp_dict[k]=i[j]
            key_time_list_all.append(tmp_dict)


# %%
time_dict={}
for i in key_time_list_all:
    for v in i.values():
        time_dict[v]=0


# %%
time_dictsss={}
for i in key_time_list_all:
    for v in i.values():
        time_dictsss[v]=0


# %%
len(key_time_list_all)


# %%
for i in key_time_list_all:
    for k in i.keys():
        if (k=='上海'):
            count1=time_dict[i[k]]
            count1=count1+1
            time_dict[i[k]]=count1


# %%
for i in key_time_list_all:
    for k in i.keys():
        if (k=='健康'):
            count=time_dictsss[i[k]]
            count=count+1
            time_dictsss[i[k]]=count


# %%
#!/usr/bin/python3
# -*- coding: utf-8 -*-

# 导入CSV安装包
import csv

# 1. 创建文件对象
f = open('healthy_count.csv','w',encoding='utf-8')

# 2. 基于文件对象构建 csv写入对象
csv_writer = csv.writer(f)

# 3. 构建列表头
csv_writer.writerow(["date","count"])

# 4. 写入csv文件内容
for k in sorted(time_dictsss.keys()):
    csv_writer.writerow([k,time_dictsss[k]])


# 5. 关闭文件
f.close()


# %%
#!/usr/bin/python3
# -*- coding: utf-8 -*-

# 导入CSV安装包
import csv

# 1. 创建文件对象
f = open('shanghai_count.csv','w',encoding='utf-8')

# 2. 基于文件对象构建 csv写入对象
csv_writer = csv.writer(f)

# 3. 构建列表头
csv_writer.writerow(["date","count"])

# 4. 写入csv文件内容
for k in sorted(time_dict.keys()):
    csv_writer.writerow([k,time_dict[k]])


# 5. 关闭文件
f.close()


# %%



# %%
# 导入CSV安装包
import csv

# 1. 创建文件对象
f = open('words.csv','w',encoding='utf-8')

# 2. 基于文件对象构建 csv写入对象
csv_writer = csv.writer(f)

# 3. 构建列表头
csv_writer.writerow(["keywords"])

# 4. 写入csv文件内容
for i in key_time_list_all:
    for k in i.keys():
        csv_writer.writerow([k])


# 5. 关闭文件
f.close()


