# 统计与机器学习FinalLecture--聚类

## Pre:Basic Concept

距离是聚类中的核心概念。

两种距离：

- 个体与个体之间的距离
- 类与类之间的距离

Jaccard距离要求各个分量之间都是正向的。

## 点间距离

连续变量间的距离。

### 斯皮尔曼秩相关系数

用rank代替原始数据，可以增加数据稳定性，但原始数据中有某些outlier过大，用rank替代可以降低这些outlier的干扰。

![截屏2021-04-27 18.25.14](https://tva1.sinaimg.cn/large/008i3skNly1gpyh79mc39j30w20gegpk.jpg)

### 如果特征里不全部都是连续变量->混合变量的点间距离

1. 定义的距离的尺度范围的差别(定义在(0,1)的距离和(0,100)的距离，(0,100)的距离很容易受取值过大的特征的影响)。需要控制量纲/数值的差异化，所以统计中应用马氏距离比欧式距离更多。马氏距离有一个类似于标准化的操作。
2. 距离->相关系数->距离(基于相似性定义距离)，定性，是否一致。
3. 定量变量

![截屏2021-04-27 18.30.23](https://tva1.sinaimg.cn/large/008i3skNly1gpyhcmobszj30xy0h60vq.jpg)

4. 定序变量

   取值可能不是分类的，但确实有顺序。（有顺序和传统的分类变量？)

5. 数据分析：数据加权算平均的概念

## 类间距离:Linkage Criteria，关联规则

- 两个类里各取一个点算距离：SimpleLinkage
- 两个类里最长的距离点:CompleteLinkage
- 将两个类中所有点的重心的距离定义为类间距离:centroid linkage。(两个重心之间的距离，也是点间距离，c.f.组合与搭配)

### 关联准则

这个公式可以涵盖上面的特例，以及有一些常见的Linkage的想法。

![截屏2021-04-27 18.46.21](https://tva1.sinaimg.cn/large/008i3skNly1gpyht8axz3j30yg0j878c.jpg)
$$
min(a,b)=\begin{cases}a=\frac{a+b-(b-a)}{2}\\b=\frac{a+b-(a-b)}{2}\end{cases}
$$

## 层次聚类

![截屏2021-04-27 18.56.39](https://tva1.sinaimg.cn/large/008i3skNly1gpyi3xoun5j30tc0j8n28.jpg)

### 自下而上

1和2近，1和2的其他距离怎么定义的问题？3到1和2这个类，4到1和2这个类的距离怎么定义？

如果我们用SimpleLinkage的话，3到1，2是0.1，4到1，2是0.4，所以就把3和（1，2)合并

![238971619521417_.pic](https://tva1.sinaimg.cn/large/008i3skNly1gpyibbjt35j31400u0q58.jpg)

自下而上复杂度高，我们一般都是自上而下。

## K-Mean聚类

聚类的本质：把{1,2,3,...,n}个人划分成k类。

“分割” ：$\Omega$样本空间里的分割指什么？我们要去找k个分割，$(C_1,C_2,...,C_k)$

“分割” ：1. 首先分割要两两不相容。2.每个个体落进去，没有遗漏(高中分班)

K-means的目的：目标:我们希望能够找到一个最优划分 C∗，使得类内距离足够小而类间距离足够大;

![截屏2021-04-27 19.29.11](https://tva1.sinaimg.cn/large/008i3skNly1gpyj1so1ccj30tc0leq68.jpg)

### 什么时候算法无法收敛

非凸。

某一个点在分类边界反复跳动。

## GMM模型

GMM模型是**基于模型**的聚类算法。

基于模型：有正态分布假定，通常假定第i个样本来自第k个正太分布。

![截屏2021-04-27 19.31.42](https://tva1.sinaimg.cn/large/008i3skNly1gpyj4dipx5j30sw0istbk.jpg)

这个密度函数，有点像马氏距离。

![截屏2021-04-27 19.34.35](https://tva1.sinaimg.cn/large/008i3skNly1gpyj7emkiaj30sy0kuq6d.jpg)

多维分布与多项分布：$\delta_i$记成二点分布，n=1的二项分布

向量$\delta_i=(\delta_{i1},\delta_{i2},...,\delta_{iK})$可以理解成空间中K面的骰子。

多项分布中的k维向量，虽然是k维向量，但它是存在约束的，即这个样本一定是落在这k个分布中的一个。第i个都可以由其他k-1个线性表出。不是所有$\pi$都是自由的。

![截屏2021-04-27 19.39.47](https://tva1.sinaimg.cn/large/008i3skNly1gpyjcsgh7yj30sy0ccq4g.jpg)

- 理解$\delta_i=()^T$;$x^\theta(1-x)^{1-\theta}$

$\delta_i=1$只有这个连乘括号里的。

在$\delta_i$给定，一定存在某一个$\delta_i=1$,$\delta_{i1}=1||\delta_{i2}=1||\delta_{i3}=1$,写成k，就这样连在一起了。

![截屏2021-04-27 19.47.34](/Users/chixinning/Library/Application Support/typora-user-images/截屏2021-04-27 19.47.34.png)

对于这个联合密度而言，里面的参数的估计，这个联合密度相当于这个参数的似然函数。所以在知道$x_1,\delta_i$时，$\theta$就可以估计了。上帝视角：直到所有数据的联合信息。

在聚类问题里，$\delta_{ik}$是不知道的，这就相当于存在缺失，不完整的数据下如何知道呢？应用EM算法，EM算法是为了处理数据不存在完整信息提出的。

![截屏2021-04-27 19.50.45](https://tva1.sinaimg.cn/large/008i3skNly1gpyjo8n5fqj30ti0h8dj1.jpg)

把$\delta_i$积掉以后$x_i$的边际密度？

我们的样本是k个正态加权求到的。

把$\delta_{ik}$当作潜变量

## 聚类方法的评价

![截屏2021-04-27 20.03.41](https://tva1.sinaimg.cn/large/008i3skNly1gpyk1pes18j30ti09wgni.jpg)

聚类，样本空间的分割。

聚类评价，卡方统计量去计算。

![截屏2021-04-27 20.16.49](https://tva1.sinaimg.cn/large/008i3skNly1gpykfc41hxj30ti0g4gne.jpg)

c.f. SSE,内部组内求和，外部每一个组加起来。

c.f. 方差分析，化成a各类。

聚类，数据分成a个类，验证a个类本质是否有显著差异。

![截屏2021-04-27 20.23.02](https://tva1.sinaimg.cn/large/008i3skNly1gpyklu0cxtj30qg0d6ac7.jpg)

从哪个角度解决这个问题？

1. K超参数的角度来看，一般的超参数如何选择？

2. K模型复杂的角度来看k-means的复杂度与聚类的个数有关，k越大，模型越复杂，**模型复杂度**的标准？如何解决模型越来越复杂的问题？

   在kmeans选k也有AIC/BIC的方法

