
# Week7 Multicollinearity 
## 背景描述  
McDonald和Schwing(1973)提出了一项研究，将总死亡率与气候、社会经济和污染变量联系起来，本研究选择了 15 个自变量列于下表。因变量是以上原因在年龄调整后的总死亡率。我们不对该研究的流行病学方面作评论，而仅仅作为变量选择的说明性例子使用这些数据。  
由此我们构造了 60 个观测的 15 个变量，具体请见下表：

## 数据描述
| 变量名 | 变量含义 | 变量类型 | 变量取值范围 |
| :----------: | :--------: | :----------: | :----------: |
| （自变量1）X1 | 年平均降水量(英寸) | continuous variable | $\mathbb{R}^+$ |
| （自变量2）X2 | 一月平均气温(华氏度) | continuous variable | $\mathbb{R}^+$ |
| （自变量3）X3 | 七月平均气温(华氏度) | continuous variable | $\mathbb{R}^+$ |
| （自变量4）X4 | 占65岁以上人口的比例 | continuous variable | $\mathbb{R}^+$ |
| （自变量5）X5 | 每个家庭人口 | continuous variable | $\mathbb{R}^+$ |
| （自变量6）X6 | 完成的平均学制 | continuous variable | $\mathbb{R}^+$ |
| （自变量7）X7 | 健全的住房单位的百分比 | continuous variable | $\mathbb{R}^+$ |
| （自变量8）X8 | 每平方英里人口 | continuous variable | $\mathbb{R}^+$ |
| （自变量9）X9 | 占非白人人口的比例 | continuous variable | $\mathbb{R}^+$ |
| （自变量10）X10 | 白领工作的就业率 | continuous variable | $\mathbb{R}^+$ |
| （自变量11）X11 | 收入在3000美元以下家庭的百分比 | continuous variable | $\mathbb{R}^+$ |
| （自变量12）X12 | 碳氢化合物的相对污染潜力 | continuous variable | $\mathbb{R}^+$ |
| （自变量13）X13 | 氮氧化物的相对污染潜力 | continuous variable | $\mathbb{R}^+$ |
| （自变量14）X14 | 二氧化硫的相对污染潜力 | continuous variable | $\mathbb{R}^+$ |
| （自变量15）X15 | 相对湿度百分比 | continuous variable | $\mathbb{R}^+$ |
| （因变量）Y | 所有原因经年龄调整的总死亡率 | continuous variable | $\mathbb{R}^+$ |

## 问题
这里使用 $\alpha=0.05$ 的水平
1. 判断变量间是否具有多重共线性.
2. 如果存在多重共线性，如何消除多重共线性/选择变量. 

## 解决方案

**Q1：**  


```python
# Import standard packages
import numpy as np
import pandas as pd
import scipy.stats as stats
import matplotlib.pyplot as plt
import math

# Import additional packages
from itertools import combinations
import statsmodels.api as sm
from statsmodels.stats.outliers_influence import variance_inflation_factor

alpha = 0.05
p = 15
n = 60

x = pd.read_csv('Air_Pollution.csv')
x.insert(0, 'intercept', np.ones(len(x))) 
data = x.values * 1
df = pd.DataFrame(data)
print(df.head())

X = data[:,0:p+1]
Y = data[:,-1]


```

        0     1     2     3     4     5     6     7       8     9     10    11  \
    0  1.0  36.0  27.0  71.0   8.1  3.34  11.4  81.5  3243.0   8.8  42.6  11.7   
    1  1.0  35.0  23.0  72.0  11.1  3.14  11.0  78.8  4281.0   3.5  50.7  14.4   
    2  1.0  44.0  29.0  74.0  10.4  3.21   9.8  81.6  4260.0   0.8  39.4  12.4   
    3  1.0  47.0  45.0  79.0   6.5  3.41  11.1  77.5  3125.0  27.1  50.2  20.6   
    4  1.0  43.0  35.0  77.0   7.6  3.44   9.6  84.6  6441.0  24.4  43.7  14.3   
    
         12    13     14    15       16  
    0  21.0  15.0   59.0  59.0   921.87  
    1   8.0  10.0   39.0  57.0   997.88  
    2   6.0   6.0   33.0  54.0   962.35  
    3  18.0   8.0   24.0  56.0   982.29  
    4  43.0  38.0  206.0  55.0  1071.29  
    

**数据预处理：**


```python
# 对自变量 X 进行标准化
# 自变量 X 的均值
X_mean = []
for i in range(p):
    X_mean.append(np.mean(X[:, i+1])) 

# 自变量 X 的标准差
X_L = []
for i in range(p):
    X_L.append(sum((X[:, i+1] - X_mean[i]) ** 2))  

# 对自变量 X 标准化(截距项不用标准化)
X_std = X * 1.0
X_std[:,1:p+1] = (X[:,1:p+1] - X_mean) / np.sqrt(X_L)


```

**做多元线性回归分析**


```python
# Do the multiple linear regression
# OLS（endog,exog=None,missing='none',hasconst=None) (endog:因变量，exog=自变量）
model = sm.OLS(Y, X).fit()
Y_hat = model.fittedvalues
model.summary()
```




<table class="simpletable">
<caption>OLS Regression Results</caption>
<tr>
  <th>Dep. Variable:</th>            <td>y</td>        <th>  R-squared:         </th> <td>   0.765</td>
</tr>
<tr>
  <th>Model:</th>                   <td>OLS</td>       <th>  Adj. R-squared:    </th> <td>   0.685</td>
</tr>
<tr>
  <th>Method:</th>             <td>Least Squares</td>  <th>  F-statistic:       </th> <td>   9.542</td>
</tr>
<tr>
  <th>Date:</th>             <td>Tue, 13 Apr 2021</td> <th>  Prob (F-statistic):</th> <td>2.19e-09</td>
</tr>
<tr>
  <th>Time:</th>                 <td>17:18:10</td>     <th>  Log-Likelihood:    </th> <td> -289.03</td>
</tr>
<tr>
  <th>No. Observations:</th>      <td>    60</td>      <th>  AIC:               </th> <td>   610.1</td>
</tr>
<tr>
  <th>Df Residuals:</th>          <td>    44</td>      <th>  BIC:               </th> <td>   643.6</td>
</tr>
<tr>
  <th>Df Model:</th>              <td>    15</td>      <th>                     </th>     <td> </td>   
</tr>
<tr>
  <th>Covariance Type:</th>      <td>nonrobust</td>    <th>                     </th>     <td> </td>   
</tr>
</table>
<table class="simpletable">
<tr>
    <td></td>       <th>coef</th>     <th>std err</th>      <th>t</th>      <th>P>|t|</th>  <th>[0.025</th>    <th>0.975]</th>  
</tr>
<tr>
  <th>const</th> <td> 1763.9979</td> <td>  437.330</td> <td>    4.034</td> <td> 0.000</td> <td>  882.617</td> <td> 2645.379</td>
</tr>
<tr>
  <th>x1</th>    <td>    1.9054</td> <td>    0.924</td> <td>    2.063</td> <td> 0.045</td> <td>    0.044</td> <td>    3.767</td>
</tr>
<tr>
  <th>x2</th>    <td>   -1.9376</td> <td>    1.108</td> <td>   -1.748</td> <td> 0.087</td> <td>   -4.171</td> <td>    0.296</td>
</tr>
<tr>
  <th>x3</th>    <td>   -3.1004</td> <td>    1.902</td> <td>   -1.630</td> <td> 0.110</td> <td>   -6.933</td> <td>    0.732</td>
</tr>
<tr>
  <th>x4</th>    <td>   -9.0652</td> <td>    8.486</td> <td>   -1.068</td> <td> 0.291</td> <td>  -26.168</td> <td>    8.038</td>
</tr>
<tr>
  <th>x5</th>    <td> -106.8310</td> <td>   69.780</td> <td>   -1.531</td> <td> 0.133</td> <td> -247.464</td> <td>   33.801</td>
</tr>
<tr>
  <th>x6</th>    <td>  -17.1569</td> <td>   11.860</td> <td>   -1.447</td> <td> 0.155</td> <td>  -41.059</td> <td>    6.746</td>
</tr>
<tr>
  <th>x7</th>    <td>   -0.6511</td> <td>    1.768</td> <td>   -0.368</td> <td> 0.714</td> <td>   -4.214</td> <td>    2.912</td>
</tr>
<tr>
  <th>x8</th>    <td>    0.0036</td> <td>    0.004</td> <td>    0.894</td> <td> 0.376</td> <td>   -0.005</td> <td>    0.012</td>
</tr>
<tr>
  <th>x9</th>    <td>    4.4596</td> <td>    1.327</td> <td>    3.360</td> <td> 0.002</td> <td>    1.785</td> <td>    7.134</td>
</tr>
<tr>
  <th>x10</th>   <td>   -0.1871</td> <td>    1.662</td> <td>   -0.113</td> <td> 0.911</td> <td>   -3.536</td> <td>    3.162</td>
</tr>
<tr>
  <th>x11</th>   <td>   -0.1674</td> <td>    3.227</td> <td>   -0.052</td> <td> 0.959</td> <td>   -6.672</td> <td>    6.337</td>
</tr>
<tr>
  <th>x12</th>   <td>   -0.6722</td> <td>    0.491</td> <td>   -1.369</td> <td> 0.178</td> <td>   -1.662</td> <td>    0.317</td>
</tr>
<tr>
  <th>x13</th>   <td>    1.3401</td> <td>    1.006</td> <td>    1.333</td> <td> 0.190</td> <td>   -0.687</td> <td>    3.367</td>
</tr>
<tr>
  <th>x14</th>   <td>    0.0863</td> <td>    0.148</td> <td>    0.585</td> <td> 0.562</td> <td>   -0.211</td> <td>    0.384</td>
</tr>
<tr>
  <th>x15</th>   <td>    0.1067</td> <td>    1.169</td> <td>    0.091</td> <td> 0.928</td> <td>   -2.250</td> <td>    2.464</td>
</tr>
</table>
<table class="simpletable">
<tr>
  <th>Omnibus:</th>       <td> 2.991</td> <th>  Durbin-Watson:     </th> <td>   2.129</td>
</tr>
<tr>
  <th>Prob(Omnibus):</th> <td> 0.224</td> <th>  Jarque-Bera (JB):  </th> <td>   2.088</td>
</tr>
<tr>
  <th>Skew:</th>          <td> 0.359</td> <th>  Prob(JB):          </th> <td>   0.352</td>
</tr>
<tr>
  <th>Kurtosis:</th>      <td> 3.566</td> <th>  Cond. No.          </th> <td>4.05e+05</td>
</tr>
</table><br/><br/>Notes:<br/>[1] Standard Errors assume that the covariance matrix of the errors is correctly specified.<br/>[2] The condition number is large, 4.05e+05. This might indicate that there are<br/>strong multicollinearity or other numerical problems.




```python
# Do the multiple linear regression
# OLS（endog,exog=None,missing='none',hasconst=None) (endog:因变量，exog=自变量）
model_std = sm.OLS(Y, X_std).fit()
Y_std_hat = model_std.fittedvalues
model_std.summary()
```




<table class="simpletable">
<caption>OLS Regression Results</caption>
<tr>
  <th>Dep. Variable:</th>            <td>y</td>        <th>  R-squared:         </th> <td>   0.765</td>
</tr>
<tr>
  <th>Model:</th>                   <td>OLS</td>       <th>  Adj. R-squared:    </th> <td>   0.685</td>
</tr>
<tr>
  <th>Method:</th>             <td>Least Squares</td>  <th>  F-statistic:       </th> <td>   9.542</td>
</tr>
<tr>
  <th>Date:</th>             <td>Tue, 13 Apr 2021</td> <th>  Prob (F-statistic):</th> <td>2.19e-09</td>
</tr>
<tr>
  <th>Time:</th>                 <td>17:18:06</td>     <th>  Log-Likelihood:    </th> <td> -289.03</td>
</tr>
<tr>
  <th>No. Observations:</th>      <td>    60</td>      <th>  AIC:               </th> <td>   610.1</td>
</tr>
<tr>
  <th>Df Residuals:</th>          <td>    44</td>      <th>  BIC:               </th> <td>   643.6</td>
</tr>
<tr>
  <th>Df Model:</th>              <td>    15</td>      <th>                     </th>     <td> </td>   
</tr>
<tr>
  <th>Covariance Type:</th>      <td>nonrobust</td>    <th>                     </th>     <td> </td>   
</tr>
</table>
<table class="simpletable">
<tr>
    <td></td>       <th>coef</th>     <th>std err</th>      <th>t</th>      <th>P>|t|</th>  <th>[0.025</th>    <th>0.975]</th>  
</tr>
<tr>
  <th>const</th> <td>  940.3585</td> <td>    4.509</td> <td>  208.538</td> <td> 0.000</td> <td>  931.271</td> <td>  949.446</td>
</tr>
<tr>
  <th>x1</th>    <td>  146.1294</td> <td>   70.845</td> <td>    2.063</td> <td> 0.045</td> <td>    3.351</td> <td>  288.908</td>
</tr>
<tr>
  <th>x2</th>    <td> -151.3453</td> <td>   86.575</td> <td>   -1.748</td> <td> 0.087</td> <td> -325.826</td> <td>   23.135</td>
</tr>
<tr>
  <th>x3</th>    <td> -113.4333</td> <td>   69.576</td> <td>   -1.630</td> <td> 0.110</td> <td> -253.654</td> <td>   26.787</td>
</tr>
<tr>
  <th>x4</th>    <td> -101.9781</td> <td>   95.465</td> <td>   -1.068</td> <td> 0.291</td> <td> -294.376</td> <td>   90.419</td>
</tr>
<tr>
  <th>x5</th>    <td> -110.9860</td> <td>   72.494</td> <td>   -1.531</td> <td> 0.133</td> <td> -257.088</td> <td>   35.116</td>
</tr>
<tr>
  <th>x6</th>    <td> -111.3974</td> <td>   77.006</td> <td>   -1.447</td> <td> 0.155</td> <td> -266.593</td> <td>   43.798</td>
</tr>
<tr>
  <th>x7</th>    <td>  -25.7135</td> <td>   69.812</td> <td>   -0.368</td> <td> 0.714</td> <td> -166.410</td> <td>  114.983</td>
</tr>
<tr>
  <th>x8</th>    <td>   40.2161</td> <td>   44.979</td> <td>    0.894</td> <td> 0.376</td> <td>  -50.434</td> <td>  130.866</td>
</tr>
<tr>
  <th>x9</th>    <td>  305.5909</td> <td>   90.946</td> <td>    3.360</td> <td> 0.002</td> <td>  122.300</td> <td>  488.881</td>
</tr>
<tr>
  <th>x10</th>   <td>   -6.6313</td> <td>   58.879</td> <td>   -0.113</td> <td> 0.911</td> <td> -125.295</td> <td>  112.032</td>
</tr>
<tr>
  <th>x11</th>   <td>   -5.3495</td> <td>  103.126</td> <td>   -0.052</td> <td> 0.959</td> <td> -213.187</td> <td>  202.488</td>
</tr>
<tr>
  <th>x12</th>   <td> -474.8764</td> <td>  346.905</td> <td>   -1.369</td> <td> 0.178</td> <td>-1174.016</td> <td>  224.264</td>
</tr>
<tr>
  <th>x13</th>   <td>  476.9329</td> <td>  357.884</td> <td>    1.333</td> <td> 0.190</td> <td> -244.334</td> <td> 1198.200</td>
</tr>
<tr>
  <th>x14</th>   <td>   42.0025</td> <td>   71.829</td> <td>    0.585</td> <td> 0.562</td> <td> -102.759</td> <td>  186.764</td>
</tr>
<tr>
  <th>x15</th>   <td>    4.4026</td> <td>   48.236</td> <td>    0.091</td> <td> 0.928</td> <td>  -92.810</td> <td>  101.615</td>
</tr>
</table>
<table class="simpletable">
<tr>
  <th>Omnibus:</th>       <td> 2.991</td> <th>  Durbin-Watson:     </th> <td>   2.129</td>
</tr>
<tr>
  <th>Prob(Omnibus):</th> <td> 0.224</td> <th>  Jarque-Bera (JB):  </th> <td>   2.088</td>
</tr>
<tr>
  <th>Skew:</th>          <td> 0.359</td> <th>  Prob(JB):          </th> <td>   0.352</td>
</tr>
<tr>
  <th>Kurtosis:</th>      <td> 3.566</td> <th>  Cond. No.          </th> <td>    111.</td>
</tr>
</table><br/><br/>Notes:<br/>[1] Standard Errors assume that the covariance matrix of the errors is correctly specified.



**预判变量间是否存在多重共线性**  

**方法1： 直观判定法**


```python
# 相关系数
r = df.corr()
r
```




<div>
<style scoped>
    .dataframe tbody tr th:only-of-type {
        vertical-align: middle;
    }

    .dataframe tbody tr th {
        vertical-align: top;
    }

    .dataframe thead th {
        text-align: right;
    }
</style>
<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th>0</th>
      <th>1</th>
      <th>2</th>
      <th>3</th>
      <th>4</th>
      <th>5</th>
      <th>6</th>
      <th>7</th>
      <th>8</th>
      <th>9</th>
      <th>10</th>
      <th>11</th>
      <th>12</th>
      <th>13</th>
      <th>14</th>
      <th>15</th>
      <th>16</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>0</th>
      <td>NaN</td>
      <td>NaN</td>
      <td>NaN</td>
      <td>NaN</td>
      <td>NaN</td>
      <td>NaN</td>
      <td>NaN</td>
      <td>NaN</td>
      <td>NaN</td>
      <td>NaN</td>
      <td>NaN</td>
      <td>NaN</td>
      <td>NaN</td>
      <td>NaN</td>
      <td>NaN</td>
      <td>NaN</td>
      <td>NaN</td>
    </tr>
    <tr>
      <th>1</th>
      <td>NaN</td>
      <td>1.000000</td>
      <td>0.092208</td>
      <td>0.503273</td>
      <td>0.101113</td>
      <td>0.263444</td>
      <td>-0.490425</td>
      <td>-0.490759</td>
      <td>-0.003515</td>
      <td>0.413204</td>
      <td>-0.297291</td>
      <td>0.506585</td>
      <td>-0.531760</td>
      <td>-0.487321</td>
      <td>-0.106924</td>
      <td>-0.077343</td>
      <td>0.509500</td>
    </tr>
    <tr>
      <th>2</th>
      <td>NaN</td>
      <td>0.092208</td>
      <td>1.000000</td>
      <td>0.346282</td>
      <td>-0.398099</td>
      <td>-0.209212</td>
      <td>0.116284</td>
      <td>0.014852</td>
      <td>-0.100051</td>
      <td>0.453774</td>
      <td>0.237992</td>
      <td>0.565314</td>
      <td>0.350809</td>
      <td>0.321014</td>
      <td>-0.107810</td>
      <td>0.067872</td>
      <td>-0.030022</td>
    </tr>
    <tr>
      <th>3</th>
      <td>NaN</td>
      <td>0.503273</td>
      <td>0.346282</td>
      <td>1.000000</td>
      <td>-0.434040</td>
      <td>0.262280</td>
      <td>-0.238544</td>
      <td>-0.415032</td>
      <td>-0.060994</td>
      <td>0.575309</td>
      <td>-0.021412</td>
      <td>0.619308</td>
      <td>-0.356494</td>
      <td>-0.337668</td>
      <td>-0.099348</td>
      <td>-0.452809</td>
      <td>0.277018</td>
    </tr>
    <tr>
      <th>4</th>
      <td>NaN</td>
      <td>0.101113</td>
      <td>-0.398099</td>
      <td>-0.434040</td>
      <td>1.000000</td>
      <td>-0.509087</td>
      <td>-0.138862</td>
      <td>0.065010</td>
      <td>0.161991</td>
      <td>-0.637821</td>
      <td>-0.117715</td>
      <td>-0.309771</td>
      <td>-0.020486</td>
      <td>-0.002082</td>
      <td>0.017248</td>
      <td>0.112426</td>
      <td>-0.174593</td>
    </tr>
    <tr>
      <th>5</th>
      <td>NaN</td>
      <td>0.263444</td>
      <td>-0.209212</td>
      <td>0.262280</td>
      <td>-0.509087</td>
      <td>1.000000</td>
      <td>-0.395075</td>
      <td>-0.410590</td>
      <td>-0.184332</td>
      <td>0.419410</td>
      <td>-0.425723</td>
      <td>0.259904</td>
      <td>-0.388205</td>
      <td>-0.358429</td>
      <td>-0.004084</td>
      <td>-0.135740</td>
      <td>0.357307</td>
    </tr>
    <tr>
      <th>6</th>
      <td>NaN</td>
      <td>-0.490425</td>
      <td>0.116284</td>
      <td>-0.238544</td>
      <td>-0.138862</td>
      <td>-0.395075</td>
      <td>1.000000</td>
      <td>0.552237</td>
      <td>-0.243883</td>
      <td>-0.208774</td>
      <td>0.703196</td>
      <td>-0.403338</td>
      <td>0.286835</td>
      <td>0.224402</td>
      <td>-0.234346</td>
      <td>0.176491</td>
      <td>-0.510988</td>
    </tr>
    <tr>
      <th>7</th>
      <td>NaN</td>
      <td>-0.490759</td>
      <td>0.014852</td>
      <td>-0.415032</td>
      <td>0.065010</td>
      <td>-0.410590</td>
      <td>0.552237</td>
      <td>1.000000</td>
      <td>0.181881</td>
      <td>-0.410334</td>
      <td>0.338745</td>
      <td>-0.680680</td>
      <td>0.386767</td>
      <td>0.348250</td>
      <td>0.117952</td>
      <td>0.121901</td>
      <td>-0.426821</td>
    </tr>
    <tr>
      <th>8</th>
      <td>NaN</td>
      <td>-0.003515</td>
      <td>-0.100051</td>
      <td>-0.060994</td>
      <td>0.161991</td>
      <td>-0.184332</td>
      <td>-0.243883</td>
      <td>0.181881</td>
      <td>1.000000</td>
      <td>-0.005678</td>
      <td>-0.031765</td>
      <td>-0.162945</td>
      <td>0.120282</td>
      <td>0.165312</td>
      <td>0.432086</td>
      <td>-0.124976</td>
      <td>0.265503</td>
    </tr>
    <tr>
      <th>9</th>
      <td>NaN</td>
      <td>0.413204</td>
      <td>0.453774</td>
      <td>0.575309</td>
      <td>-0.637821</td>
      <td>0.419410</td>
      <td>-0.208774</td>
      <td>-0.410334</td>
      <td>-0.005678</td>
      <td>1.000000</td>
      <td>-0.004387</td>
      <td>0.704915</td>
      <td>-0.025865</td>
      <td>0.018385</td>
      <td>0.159293</td>
      <td>-0.117957</td>
      <td>0.643742</td>
    </tr>
    <tr>
      <th>10</th>
      <td>NaN</td>
      <td>-0.297291</td>
      <td>0.237992</td>
      <td>-0.021412</td>
      <td>-0.117715</td>
      <td>-0.425723</td>
      <td>0.703196</td>
      <td>0.338745</td>
      <td>-0.031765</td>
      <td>-0.004387</td>
      <td>1.000000</td>
      <td>-0.185161</td>
      <td>0.203672</td>
      <td>0.160034</td>
      <td>-0.068461</td>
      <td>0.060713</td>
      <td>-0.284805</td>
    </tr>
    <tr>
      <th>11</th>
      <td>NaN</td>
      <td>0.506585</td>
      <td>0.565314</td>
      <td>0.619308</td>
      <td>-0.309771</td>
      <td>0.259904</td>
      <td>-0.403338</td>
      <td>-0.680680</td>
      <td>-0.162945</td>
      <td>0.704915</td>
      <td>-0.185161</td>
      <td>1.000000</td>
      <td>-0.129784</td>
      <td>-0.102544</td>
      <td>-0.096483</td>
      <td>-0.152223</td>
      <td>0.410490</td>
    </tr>
    <tr>
      <th>12</th>
      <td>NaN</td>
      <td>-0.531760</td>
      <td>0.350809</td>
      <td>-0.356494</td>
      <td>-0.020486</td>
      <td>-0.388205</td>
      <td>0.286835</td>
      <td>0.386767</td>
      <td>0.120282</td>
      <td>-0.025865</td>
      <td>0.203672</td>
      <td>-0.129784</td>
      <td>1.000000</td>
      <td>0.983840</td>
      <td>0.282296</td>
      <td>-0.020178</td>
      <td>-0.177242</td>
    </tr>
    <tr>
      <th>13</th>
      <td>NaN</td>
      <td>-0.487321</td>
      <td>0.321014</td>
      <td>-0.337668</td>
      <td>-0.002082</td>
      <td>-0.358429</td>
      <td>0.224402</td>
      <td>0.348250</td>
      <td>0.165312</td>
      <td>0.018385</td>
      <td>0.160034</td>
      <td>-0.102544</td>
      <td>0.983840</td>
      <td>1.000000</td>
      <td>0.409394</td>
      <td>-0.045914</td>
      <td>-0.077382</td>
    </tr>
    <tr>
      <th>14</th>
      <td>NaN</td>
      <td>-0.106924</td>
      <td>-0.107810</td>
      <td>-0.099348</td>
      <td>0.017248</td>
      <td>-0.004084</td>
      <td>-0.234346</td>
      <td>0.117952</td>
      <td>0.432086</td>
      <td>0.159293</td>
      <td>-0.068461</td>
      <td>-0.096483</td>
      <td>0.282296</td>
      <td>0.409394</td>
      <td>1.000000</td>
      <td>-0.102554</td>
      <td>0.425898</td>
    </tr>
    <tr>
      <th>15</th>
      <td>NaN</td>
      <td>-0.077343</td>
      <td>0.067872</td>
      <td>-0.452809</td>
      <td>0.112426</td>
      <td>-0.135740</td>
      <td>0.176491</td>
      <td>0.121901</td>
      <td>-0.124976</td>
      <td>-0.117957</td>
      <td>0.060713</td>
      <td>-0.152223</td>
      <td>-0.020178</td>
      <td>-0.045914</td>
      <td>-0.102554</td>
      <td>1.000000</td>
      <td>-0.088501</td>
    </tr>
    <tr>
      <th>16</th>
      <td>NaN</td>
      <td>0.509500</td>
      <td>-0.030022</td>
      <td>0.277018</td>
      <td>-0.174593</td>
      <td>0.357307</td>
      <td>-0.510988</td>
      <td>-0.426821</td>
      <td>0.265503</td>
      <td>0.643742</td>
      <td>-0.284805</td>
      <td>0.410490</td>
      <td>-0.177242</td>
      <td>-0.077382</td>
      <td>0.425898</td>
      <td>-0.088501</td>
      <td>1.000000</td>
    </tr>
  </tbody>
</table>
</div>



1. 当与因变量之间的简单相关系数绝对值很大的自变量在回归方程中没有通过显著性检验时，可初步判断存在严重的多重共线性。


```python
r_xy = np.array(r.iloc[1:p+1][p+1])
print('因变量和每个自变量之间的相关系数: \n', r_xy)

judge_xy = True
for i in range(p):
    if (abs(r_xy[i]) >= 0.5) & (model_std.pvalues[i+1] >= alpha):
        judge_xy = False
        print('自变量 %d 与因变量之间的简单相关系数为: %.4f, tPal: %.4f.' % (i+1, r_xy[i], model_std.pvalues[i+1]))
        
if judge_xy:
    print('\n自变量之间不存在多重共线性。')
else:
    print('\n自变量之间存在多重共线性。')
```

    因变量和每个自变量之间的相关系数: 
     [ 0.50949986 -0.03002188  0.27701762 -0.17459291  0.35730691 -0.51098849
     -0.42682123  0.2655034   0.64374176 -0.28480459  0.41049037 -0.17724211
     -0.07738243  0.42589789 -0.08850055]
    自变量 6 与因变量之间的简单相关系数为: -0.5110, tPal: 0.1551.
    
    自变量之间存在多重共线性。
    

2. 在自变量的相关矩阵中，当自变量间的相关系数较大时会出现多重共线性的问题。


```python
judge_xx = True
for (i, j) in combinations(range(1, p+1), 2):
    if(r.iloc[i][j] >= 0.7):
        judge_xx = False
        print('变量(%d,%d)之间相关系数较大，为：%.4f'% (i, j, r.iloc[i][j]))
        
if judge_xx:
    print('\n自变量之间不存在多重共线性。')
else:
    print('\n自变量之间存在多重共线性。')
```

    变量(6,10)之间相关系数较大，为：0.7032
    变量(9,11)之间相关系数较大，为：0.7049
    变量(12,13)之间相关系数较大，为：0.9838
    
    自变量之间存在多重共线性。
    

**方法2：方差扩大因子法**  
1. 计算自变量 $x_j$ 的方差扩大因子 $\mathsf{VIF_j}$，$j=1,\cdots,p$.


```python
# 法1：
c = np.dot(X_std.T, X_std)
C = np.linalg.inv(c)  # 求逆
C_list = []
for i in range(p):
    C_list.append(C[i + 1][i + 1])

# 法2：
vif = [variance_inflation_factor(X_std[:,1:p + 1], i) for i in range(p)]

print('C主对角线元素  方差扩大因子：')
for i in range(p):
    print('%d. %.4f        %.4f' % (i+1, C_list[i], vif[i]))
```

    C主对角线元素  方差扩大因子：
    1. 4.1139        4.1139
    2. 6.1436        6.1436
    3. 3.9678        3.9678
    4. 7.4700        7.4700
    5. 4.3076        4.3076
    6. 4.8605        4.8605
    7. 3.9948        3.9948
    8. 1.6583        1.6583
    9. 6.7796        6.7796
    10. 2.8416        2.8416
    11. 8.7171        8.7171
    12. 98.6399        98.6399
    13. 104.9824        104.9824
    14. 4.2289        4.2289
    15. 1.9071        1.9071
    

2. 通过 $\mathsf{VIF_j}$ 的大小判断自变量之间是否存在多重共线性.  
如果VIF值大于10说明共线性很严重，这种情况需要处理，如果VIF值在5以下不需要处理，如果VIF介于5~10之间视情况而定。


```python
thres_vif = 5
for i in range(p):
    if vif[i] >= thres_vif:
        print('自变量 x%d 与其余自变量之间存在多重共线性，其中VIF值为：%.4f' % (i + 1, vif[i]))

```

    自变量 x2 与其余自变量之间存在多重共线性，其中VIF值为：6.1436
    自变量 x4 与其余自变量之间存在多重共线性，其中VIF值为：7.4700
    自变量 x9 与其余自变量之间存在多重共线性，其中VIF值为：6.7796
    自变量 x11 与其余自变量之间存在多重共线性，其中VIF值为：8.7171
    自变量 x12 与其余自变量之间存在多重共线性，其中VIF值为：98.6399
    自变量 x13 与其余自变量之间存在多重共线性，其中VIF值为：104.9824
    

**方法3：特征值判定法**  
1. 计算自变量 $x_j$ 的条件数 $\kappa_j = \sqrt{\frac{\lambda_1}{\lambda_j}}$，$j=1,\cdots,p$.


```python
corr = np.corrcoef(X_std[:,1:p+1], rowvar = 0) # 相关系数矩阵
w, v = np.linalg.eig(corr) # 特征值 & 特征向量

kappa = []
for i in range(p):
    kappa.append(np.sqrt(max(w) / w[i]))
    print('特征值%d: %.4f, kappa%d: %.4f' %(i + 1, w[i], i + 1, kappa[i]))
```

    特征值1: 4.5284, kappa1: 1.0000
    特征值2: 2.7548, kappa2: 1.2821
    特征值3: 2.0545, kappa3: 1.4846
    特征值4: 1.3484, kappa4: 1.8326
    特征值5: 1.2232, kappa5: 1.9241
    特征值6: 0.9604, kappa6: 2.1714
    特征值7: 0.6127, kappa7: 2.7185
    特征值8: 0.4720, kappa8: 3.0974
    特征值9: 0.3709, kappa9: 3.4944
    特征值10: 0.0049, kappa10: 30.5051
    特征值11: 0.0460, kappa11: 9.9176
    特征值12: 0.2164, kappa12: 4.5746
    特征值13: 0.1664, kappa13: 5.2175
    特征值14: 0.1140, kappa14: 6.3030
    特征值15: 0.1270, kappa15: 5.9712
    

2. 通过 $\kappa_p$ 的大小判断自变量之间是否存在多重共线性以及多重共线性的严重程度.  
记 $\kappa=\lambda_{max}/ \lambda_{min}$，从实际应用的角度,一般若 $\kappa<100$，则认为多重共线性的程度很小，若是 $100<=\kappa<=1000$，则认为存在一般程度上的多重共线性，若是 $\kappa>1000$，则就认为存在严重的多重共线性.  
$\kappa >= c_{\kappa}$时，自变量之间存在多重共线性，$c_{\kappa}$常见取值为10，100，1000.


```python
thres_kappa = 10
if np.max(kappa) >= thres_kappa:
    print('设计矩阵 X 存在多重共线性，其中kappa值为：%.4f' % np.max(kappa))
else:
    print('设计矩阵 X 不存在多重共线性，其中kappa值为：%.4f' % np.max(kappa))
```

    设计矩阵 X 存在多重共线性，其中kappa值为：30.5051
    

**Q2：**  
消除多重共线性：
1. 剔除不重要的解释变量
2. 增大样本量

**方法1：剔除不重要的解释变量**


```python
# 利用VIF删除导致高共线性的变量
col = list(range(X_std.shape[1]))
dropped = True
while dropped:
    dropped = False
    vif_drop = [variance_inflation_factor(X_std[:,col], i) for i in range(X_std[:,col].shape[1])]
    maxvif = max(vif_drop)
    maxix = vif_drop.index(maxvif)
    if maxvif > thres_vif:
        del col[maxix]
        dropped = True
    
    if dropped:
        print('剔除剩余变量中第 %d 列变量：' % maxix, '剩余变量：', col)
        
        # 利用 AIC、BIC 准则做变量选择的一个参考
        X_std_vif = X_std[:, col]
        model_vif = sm.OLS(Y, X_std_vif).fit()
        print('此时模型的AIC值为：%.4f'% model_vif.aic)
 
```

    剔除剩余变量中第 13 列变量： 剩余变量： [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15]
    此时模型的AIC值为：610.4349
    剔除剩余变量中第 11 列变量： 剩余变量： [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 14, 15]
    此时模型的AIC值为：608.4349
    剔除剩余变量中第 4 列变量： 剩余变量： [0, 1, 2, 3, 5, 6, 7, 8, 9, 10, 12, 14, 15]
    此时模型的AIC值为：607.3130
    


```python
# Do the multiple linear regression
X_std_vif = X_std[:, col]
model_vif = sm.OLS(Y, X_std_vif).fit()
model_vif.summary()
```




<table class="simpletable">
<caption>OLS Regression Results</caption>
<tr>
  <th>Dep. Variable:</th>            <td>y</td>        <th>  R-squared:         </th> <td>   0.752</td>
</tr>
<tr>
  <th>Model:</th>                   <td>OLS</td>       <th>  Adj. R-squared:    </th> <td>   0.688</td>
</tr>
<tr>
  <th>Method:</th>             <td>Least Squares</td>  <th>  F-statistic:       </th> <td>   11.86</td>
</tr>
<tr>
  <th>Date:</th>             <td>Tue, 13 Apr 2021</td> <th>  Prob (F-statistic):</th> <td>1.67e-10</td>
</tr>
<tr>
  <th>Time:</th>                 <td>13:53:24</td>     <th>  Log-Likelihood:    </th> <td> -290.66</td>
</tr>
<tr>
  <th>No. Observations:</th>      <td>    60</td>      <th>  AIC:               </th> <td>   607.3</td>
</tr>
<tr>
  <th>Df Residuals:</th>          <td>    47</td>      <th>  BIC:               </th> <td>   634.5</td>
</tr>
<tr>
  <th>Df Model:</th>              <td>    12</td>      <th>                     </th>     <td> </td>   
</tr>
<tr>
  <th>Covariance Type:</th>      <td>nonrobust</td>    <th>                     </th>     <td> </td>   
</tr>
</table>
<table class="simpletable">
<tr>
    <td></td>       <th>coef</th>     <th>std err</th>      <th>t</th>      <th>P>|t|</th>  <th>[0.025</th>    <th>0.975]</th>  
</tr>
<tr>
  <th>const</th> <td>  940.3585</td> <td>    4.483</td> <td>  209.767</td> <td> 0.000</td> <td>  931.340</td> <td>  949.377</td>
</tr>
<tr>
  <th>x1</th>    <td>  114.4016</td> <td>   53.971</td> <td>    2.120</td> <td> 0.039</td> <td>    5.827</td> <td>  222.977</td>
</tr>
<tr>
  <th>x2</th>    <td> -121.1191</td> <td>   57.930</td> <td>   -2.091</td> <td> 0.042</td> <td> -237.660</td> <td>   -4.579</td>
</tr>
<tr>
  <th>x3</th>    <td>  -96.5763</td> <td>   65.378</td> <td>   -1.477</td> <td> 0.146</td> <td> -228.101</td> <td>   34.948</td>
</tr>
<tr>
  <th>x4</th>    <td>  -61.0044</td> <td>   54.900</td> <td>   -1.111</td> <td> 0.272</td> <td> -171.449</td> <td>   49.440</td>
</tr>
<tr>
  <th>x5</th>    <td>  -63.9241</td> <td>   70.253</td> <td>   -0.910</td> <td> 0.368</td> <td> -205.256</td> <td>   77.407</td>
</tr>
<tr>
  <th>x6</th>    <td>  -30.6498</td> <td>   53.258</td> <td>   -0.575</td> <td> 0.568</td> <td> -137.792</td> <td>   76.492</td>
</tr>
<tr>
  <th>x7</th>    <td>   43.5835</td> <td>   44.288</td> <td>    0.984</td> <td> 0.330</td> <td>  -45.512</td> <td>  132.679</td>
</tr>
<tr>
  <th>x8</th>    <td>  351.4467</td> <td>   66.359</td> <td>    5.296</td> <td> 0.000</td> <td>  217.950</td> <td>  484.944</td>
</tr>
<tr>
  <th>x9</th>    <td>  -28.6629</td> <td>   55.417</td> <td>   -0.517</td> <td> 0.607</td> <td> -140.147</td> <td>   82.822</td>
</tr>
<tr>
  <th>x10</th>   <td>  -31.6548</td> <td>   61.719</td> <td>   -0.513</td> <td> 0.610</td> <td> -155.818</td> <td>   92.508</td>
</tr>
<tr>
  <th>x11</th>   <td>  113.3616</td> <td>   46.157</td> <td>    2.456</td> <td> 0.018</td> <td>   20.506</td> <td>  206.217</td>
</tr>
<tr>
  <th>x12</th>   <td>   -2.5819</td> <td>   46.842</td> <td>   -0.055</td> <td> 0.956</td> <td>  -96.817</td> <td>   91.653</td>
</tr>
</table>
<table class="simpletable">
<tr>
  <th>Omnibus:</th>       <td> 6.868</td> <th>  Durbin-Watson:     </th> <td>   2.055</td>
</tr>
<tr>
  <th>Prob(Omnibus):</th> <td> 0.032</td> <th>  Jarque-Bera (JB):  </th> <td>   6.115</td>
</tr>
<tr>
  <th>Skew:</th>          <td> 0.624</td> <th>  Prob(JB):          </th> <td>  0.0470</td>
</tr>
<tr>
  <th>Kurtosis:</th>      <td> 3.942</td> <th>  Cond. No.          </th> <td>    21.9</td>
</tr>
</table><br/><br/>Notes:<br/>[1] Standard Errors assume that the covariance matrix of the errors is correctly specified.




```python
# 后退法
col0 = list(range(X_std.shape[1]))
col1 = col0 * 1
dropped1 = True
aic_model = sm.OLS(Y, X_std).fit().aic
while dropped1:
    X_std_aic = X_std[:, col1]
    model_aic = sm.OLS(Y, X_std_aic).fit().aic
    aic = []
    for i in col1:
        col2 = col0 * 1
        del col2[i]
        aic.append(sm.OLS(Y, X_std[:, col2]).fit().aic)
    minaic = min(aic[1:len(aic)])
    minaic_ix = aic.index(minaic)
    if minaic < model_aic:
        del col1[minaic_ix]
    else:
        dropped1 = False
    
    if dropped1:
        print('剔除剩余变量中第 %d 列变量：' % minaic_ix, '剩余变量：', col1)
        print('此时模型的AIC值为：%.4f'% minaic)

        
```

    剔除剩余变量中第 11 列变量： 剩余变量： [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 14, 15]
    此时模型的AIC值为：608.0644
    


```python
# Do the multiple linear regression
X_std_aic = X_std[:, col1]
model_aic = sm.OLS(Y, X_std_aic).fit()
model_aic.summary()
```




<table class="simpletable">
<caption>OLS Regression Results</caption>
<tr>
  <th>Dep. Variable:</th>            <td>y</td>        <th>  R-squared:         </th> <td>   0.765</td>
</tr>
<tr>
  <th>Model:</th>                   <td>OLS</td>       <th>  Adj. R-squared:    </th> <td>   0.692</td>
</tr>
<tr>
  <th>Method:</th>             <td>Least Squares</td>  <th>  F-statistic:       </th> <td>   10.46</td>
</tr>
<tr>
  <th>Date:</th>             <td>Tue, 13 Apr 2021</td> <th>  Prob (F-statistic):</th> <td>6.58e-10</td>
</tr>
<tr>
  <th>Time:</th>                 <td>13:53:30</td>     <th>  Log-Likelihood:    </th> <td> -289.03</td>
</tr>
<tr>
  <th>No. Observations:</th>      <td>    60</td>      <th>  AIC:               </th> <td>   608.1</td>
</tr>
<tr>
  <th>Df Residuals:</th>          <td>    45</td>      <th>  BIC:               </th> <td>   639.5</td>
</tr>
<tr>
  <th>Df Model:</th>              <td>    14</td>      <th>                     </th>     <td> </td>   
</tr>
<tr>
  <th>Covariance Type:</th>      <td>nonrobust</td>    <th>                     </th>     <td> </td>   
</tr>
</table>
<table class="simpletable">
<tr>
    <td></td>       <th>coef</th>     <th>std err</th>      <th>t</th>      <th>P>|t|</th>  <th>[0.025</th>    <th>0.975]</th>  
</tr>
<tr>
  <th>const</th> <td>  940.3585</td> <td>    4.459</td> <td>  210.888</td> <td> 0.000</td> <td>  931.378</td> <td>  949.339</td>
</tr>
<tr>
  <th>x1</th>    <td>  146.9747</td> <td>   68.177</td> <td>    2.156</td> <td> 0.036</td> <td>    9.659</td> <td>  284.291</td>
</tr>
<tr>
  <th>x2</th>    <td> -154.3382</td> <td>   63.829</td> <td>   -2.418</td> <td> 0.020</td> <td> -282.895</td> <td>  -25.781</td>
</tr>
<tr>
  <th>x3</th>    <td> -113.9592</td> <td>   68.066</td> <td>   -1.674</td> <td> 0.101</td> <td> -251.051</td> <td>   23.133</td>
</tr>
<tr>
  <th>x4</th>    <td> -103.7001</td> <td>   88.510</td> <td>   -1.172</td> <td> 0.248</td> <td> -281.968</td> <td>   74.568</td>
</tr>
<tr>
  <th>x5</th>    <td> -111.3815</td> <td>   71.289</td> <td>   -1.562</td> <td> 0.125</td> <td> -254.964</td> <td>   32.201</td>
</tr>
<tr>
  <th>x6</th>    <td> -111.2812</td> <td>   76.116</td> <td>   -1.462</td> <td> 0.151</td> <td> -264.586</td> <td>   42.024</td>
</tr>
<tr>
  <th>x7</th>    <td>  -23.5572</td> <td>   55.462</td> <td>   -0.425</td> <td> 0.673</td> <td> -135.264</td> <td>   88.150</td>
</tr>
<tr>
  <th>x8</th>    <td>   40.4875</td> <td>   44.176</td> <td>    0.917</td> <td> 0.364</td> <td>  -48.488</td> <td>  129.463</td>
</tr>
<tr>
  <th>x9</th>    <td>  303.1662</td> <td>   77.146</td> <td>    3.930</td> <td> 0.000</td> <td>  147.786</td> <td>  458.546</td>
</tr>
<tr>
  <th>x10</th>   <td>   -6.0447</td> <td>   57.139</td> <td>   -0.106</td> <td> 0.916</td> <td> -121.129</td> <td>  109.040</td>
</tr>
<tr>
  <th>x11</th>   <td> -473.4673</td> <td>  341.986</td> <td>   -1.384</td> <td> 0.173</td> <td>-1162.262</td> <td>  215.327</td>
</tr>
<tr>
  <th>x12</th>   <td>  476.1708</td> <td>  353.597</td> <td>    1.347</td> <td> 0.185</td> <td> -236.011</td> <td> 1188.352</td>
</tr>
<tr>
  <th>x13</th>   <td>   42.3034</td> <td>   70.796</td> <td>    0.598</td> <td> 0.553</td> <td> -100.288</td> <td>  184.895</td>
</tr>
<tr>
  <th>x14</th>   <td>    4.8405</td> <td>   46.962</td> <td>    0.103</td> <td> 0.918</td> <td>  -89.747</td> <td>   99.428</td>
</tr>
</table>
<table class="simpletable">
<tr>
  <th>Omnibus:</th>       <td> 2.959</td> <th>  Durbin-Watson:     </th> <td>   2.126</td>
</tr>
<tr>
  <th>Prob(Omnibus):</th> <td> 0.228</td> <th>  Jarque-Bera (JB):  </th> <td>   2.058</td>
</tr>
<tr>
  <th>Skew:</th>          <td> 0.359</td> <th>  Prob(JB):          </th> <td>   0.357</td>
</tr>
<tr>
  <th>Kurtosis:</th>      <td> 3.555</td> <th>  Cond. No.          </th> <td>    111.</td>
</tr>
</table><br/><br/>Notes:<br/>[1] Standard Errors assume that the covariance matrix of the errors is correctly specified.




```python
# 方差扩大因子法
print('方差扩大因子：')
for i in range(X_std_vif.shape[1] - 1):
    print('%.4f' % vif_drop[i + 1])

print('\n')
for i in range(X_std_vif.shape[1] - 1):
    if vif_drop[i] >= thres_vif:
        print('自变量 x%d 与其余自变量之间存在多重共线性，其中VIF值为: %.4f' % (i + 1, vif_drop[i + 1]))
```

    方差扩大因子：
    2.4158
    2.7832
    3.5449
    2.4997
    4.0933
    2.3524
    1.6267
    3.6520
    2.5470
    3.1592
    1.7669
    1.8198
    
    
    


```python
# 特征值判定法
corr_ = np.corrcoef(X_std_vif[:,1:p+1], rowvar = 0) # 相关系数矩阵
w_, v_ = np.linalg.eig(corr_) # 特征值 & 特征向量

kappa_ = []
for i in range(X_std_vif.shape[1] - 1):
    kappa_.append(np.sqrt(w_[0] / w_[i]))
    print('特征值%d: %.4f, kappa%d: %.4f' %(i + 1, w_[i], i + 1, kappa_[i]))
    
if max(kappa_) >= thres_kappa:
    print('\n设计矩阵 X 存在多重共线性，其中 kappa 值为: %.4f' % max(kappa_))
else:
    print('\n设计矩阵 X 不存在多重共线性，其中 kappa 值为: %.4f' % max(kappa_))
```

    特征值1: 3.6174, kappa1: 1.0000
    特征值2: 1.9819, kappa2: 1.3510
    特征值3: 1.7561, kappa3: 1.4353
    特征值4: 1.1025, kappa4: 1.8114
    特征值5: 0.9685, kappa5: 1.9326
    特征值6: 0.8119, kappa6: 2.1108
    特征值7: 0.5317, kappa7: 2.6083
    特征值8: 0.4441, kappa8: 2.8540
    特征值9: 0.3198, kappa9: 3.3634
    特征值10: 0.2123, kappa10: 4.1277
    特征值11: 0.1246, kappa11: 5.3887
    特征值12: 0.1292, kappa12: 5.2923
    
    设计矩阵 X 不存在多重共线性，其中 kappa 值为: 5.3887
    

综上，剔除原始数据的第 4、11 和 13 个变量，可以一定程度上消除变量间的多重共线性。

**方法2：增大样本量**  
这里不用增大样本量，原因有二：  
其一，这个数据集中数据量是充足的，而且不是因为样本量过少而导致的多重共线性问题，更多是因为这个变量之间的相关性很强造成的；  
其二，增加变量的方法，只是在于采集数据时，如果样本量过小可能会产生多重共线性的问题，因此需要采集足够多的样本。在实际分析阶段，往往无法增加样本量。

## 第七周练习题
数据集：Project7.csv(内附文档)  
统计方法：Multicollinearity  
软件：Jupyter Notebook  
作业发到钉钉群  
Deadline：下周一晚上10：00之前交  
注：要有完整的解题过程，不能只有代码


```python

```
