# 统计与机器学习Overview

统计更看重数据和模型是否匹配

机器学习更看重预测的精度。

如，在股票中更看重我有没有把股票预测对。

---

# 第一讲 ANOVA(Single Factor Anova)方差分析

Analysis of Variance(AOV)

## RA Fisher 农田试验，deciding which factor可以提高亩产量。

问：探究化肥A,B对水稻的亩产量的影响 c.f. 哪种广告投放效果最好

数据：

- 化肥A :$x_1,x_2,...,x_{10}$
- 化肥B :$y_1,y_2,...,y_{10}$

$H_0:\mu_x=\mu_y$,应用方差不已知时的两样本$t$检验。

$H_1:\mu_x\neq\mu_y$

检验统计量$?$

1. 点估计 ：$\hat{\mu_x}=\bar{x},\hat{\mu_y}=\bar{y}$,如果$\bar{x}$和$\bar{y}$很接近。
2. $\bar{x}-\bar{y}$的分布是什么？$\bar{x}$和$\bar{y}$独立，

> 两样本t和成对数据t

3. 标准化,$\sigma^2$未知，用样本方差$s^2$去代替,即$s^2=\hat{\sigma^2}$

所以得到了在$H_0$成立的情况下的检验统计量:
$$
t=\frac{\bar{x}-\bar{y}}{\sqrt{\frac{2s^2}{n}}},t\in t(2n-2)
$$

4. 确认拒绝域

   { $t_0 \le t_{\alpha/2}(2n-2)$}或{ $t_0 \ge t_{1-\alpha/2}(2n-2)$}

5. 确认p值

   $2P(t\ge|t_0|)$

## Further more:

设市场上有A种化肥$A_1,A_2,....,A_a$,同样的我们可以获得数据。

$a$种化肥，$m$块土地

1. 数据定义
   $$
   \begin{pmatrix}y_{11}&&y_{12}&&y_{1m}\\y_{21}&&y_{22}&&y_{2m}\\y_{i1}&&y_{i2}&&y_{im}\end{pmatrix}
   $$

第一个1表示化肥类型，第2个1表示第几块土地，

- $A_1,A_2,....,A_a$为因子。

- $m$为重复次数
- $y_{1m}$为观测值
- 亩产量为响应变量
- a种因子，取值，因子水平(level of factor)，处理(treatment)

认为第$i$行,$y_{i1},y_{i2},...,y_{im}\in N(\mu_i,\sigma^2)$表示某一个因子(化肥类型）下面的正态分布.

**每一行是iid,行与行之间是独立的。**

### 此时，我们的问题：

$H_0:\mu_0=\mu_1=...=\mu_a$

#### 建模

$y_{ij}=N(\mu_i,\sigma^2)$,$y_{ij}$之间独立

$y_{ij}=\mu_i+\epsilon_{ij}$=确定的+随机的，$\epsilon_{ij}$是独立同分布$N(0,\sigma^2)$

这个模型叫均值模型，因为它只对均值做了模型。

这里确定的$\mu$，一部分与$i$有关，一部分无关。$\mu_i=\mu+\alpha_i$,添加一个约束:$\mu=\frac{1}{m}\sum ^m\mu_i$

$\mu_i=\mu+\alpha_i$左右两边同时对i求和，可得$\sum\alpha_i=0$,这个$\sum\alpha_i=0$就是约束

$y_{ij}=\mu_i+\epsilon_{ij}+\alpha_i$

这个模型叫效率模型(effective model), **$\alpha_i$即为因子$A_i$的效率，即平均产量之上提升它或降低它的效应。**

此时，我们的假设变为检验$\alpha_i$是否为0.

$H_0:\mu_0=\mu_1=...=\mu_a $等价于$H_0:\alpha_i=0$

1. 如果$H_0$成立，所有样本是同分布的，同一总体。

   $\sum_{i=1}^{a}\sum_{j=1}^m(y_{ij}-\bar{y_{..}})^2$为总偏差平方和$SS_T$

2. 如果$H_0$不成立，所有样本不是同分布的，不同总体。我们去看各组数据的方差分别是什么，并将方差加起来。

   $\sum_{i=1}^{a}\sum_{j=1}^m(y_{ij}-\bar{y_{i.}})^2$为误差平方和$SS_E$

#### 引理

$SS_T=SS_E+SS_A$

$\sum_{i=1}^{a}\sum_{j=1}^m(y_{ij}-\bar{y_{..}})^2=\sum_{i=1}^{a}\sum_{j=1}^m(y_{ij}-\bar{y_{i.}})^2+m\sum_a(\bar{y_{i.}-\bar{y_{..}}})^2$

在$H_0$成立的情况下，$SS_T$约等于$SS_E$,$SS_A$很接近于0.反之,$SS_A$不会太小，但它一定不会超过$SS_T$

此时我们提出一个新的检验统计量$T=\frac{SS_A}{SS_E}$,若T很大我们认为它不成立。

对于这里的$T$我们要标准化,标准化即除以自由度，所以$T=\frac{SS_A/(a-1)}{SS_E/(a(m-1))}$,$SS_A$的自由度是$a-1$,$SS_E$的自由度是$a(m-1)$



