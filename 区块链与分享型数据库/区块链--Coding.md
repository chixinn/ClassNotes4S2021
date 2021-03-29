# 区块链--Coding

## 文章目标

1. 使用Java完成自己的区块链模拟系统(MiniChain)
2. 华东师范大学2021S-DaSE-本科生-研究生-专业选修课课程：《区块链与分享型数据库》张召老师
3. 笔记/备忘录使用/借鉴多方汇总而成的自己的理解。
4. 希望对你有用。

## 密码学基础

### 单向散列函数(密码学哈希)

数字信息->单向散列函数->散列值

密码学哈希和数据结构中的哈希有明显的不同。

密码学哈希不可逆，也不会像数据结构哈希这么容易碰撞，其次，当输入信息发生微小变化时，会导致通过单向散列函数计算得到的散列值发生巨大的改变。

### 对称加密

只有两个人懂的共同语言

### 非对称加密--常见方法rsa

````python
import rsa
# 1. 生成密钥对：pubkey
(pubkey, privkey) = rsa.newkeys(512) 

# 2. 明文信息
message1 = 'I like blockchain!'.encode('utf8')
print(message1)

# 3. 加密过程，由明文生成密文
crypto = rsa.encrypt(message1, pubkey)
print(crypto)

# 4. 解密过程，由密文得到明文
message2 = rsa.decrypt(crypto, privkey)
print(message2.decode('utf8'))
````

一句话通俗易懂版：

给你一把打开的锁，用它锁住重要的东西寄回给我。钥匙我自己留着谁也不给。

锁=公钥；钥匙=私钥

### 密钥对(pub key, priv key)=rsa.newkeys()

公钥加密的只能用私钥解密；私钥加密的只能用公钥解密

公钥的作用：对内容本身加密，保证不被其他人看到。

私钥的作用：证明内容的来源

### 数字签名(PDF合同如何像纸质合同一样保证其法律效益？)

存在的意义：

1. 防否认(防止他不承认啊)
2. 身份鉴别

### 发送者和接受者的数字签名的流程

#### 生成签名

1. 发送者要发送："今天晚上要去跳超级猩猩"
2. 消息经过散列函数
3. 消息使用发送者的私钥加密
4. 得到数字签名
5. 将消息和数字签名一起发给接受者

#### 验证签名

1. 计算消息的散列值
2. 使用发送者的公钥解密数字签名
3. 散列值和解密后的数字签名进行对比，如果相等则验证成功

![截屏2021-03-19 15.01.16](https://tva1.sinaimg.cn/large/008eGmZEly1gop851cp8kj30oq0c6gn7.jpg)

- 如果信息被篡改，那么肯定会验证失败
- 如果用正确的密钥验证伪装者的数字签名呢？(发送者的私钥只有发送者自己持有，无法被伪装)

```python
import rsa
(pub,pri)=rsa.newkeys(512)
(fakepub,fakepri)=rsa.newkeys(512)#模仿A，只能获得A的公钥而无法获得A的私钥
m='Btc is cool'.encoded('utf-8')
mh=rsa.compute_hash(m,'SHA-1')
real_sign=rsa.sign_hash(mh,fakepri,'SHA-1')#其实重点就在于这个
```

![截屏2021-03-19 15.14.10](https://tva1.sinaimg.cn/large/008eGmZEly1gop8if3k4mj313m056mzs.jpg)

在这里，双向加密是用发送者的私钥加密而不是公钥加密，如果用公钥加密跟对称又有啥区别了呢。

## 区块链技术

### 区块链

区块链：一个个区块串成的链。(一条散列值组成的链)

一种特殊的分布式数据库，仅提供增查功能。

![截屏2021-03-26 13.40.40](https://tva1.sinaimg.cn/large/008eGmZEgy1gox95du082j30q608ydhc.jpg)

### 区块

![截屏2021-03-26 13.41.13](https://tva1.sinaimg.cn/large/008eGmZEgy1gox95ux8y9j30es0cogm6.jpg)

区块的结构定义：

| 域名                             | 描述      |
| -------------------------------- | --------- |
| Block Header                     | Meta data |
| Transaction Counter (Block Body) |           |
| Transactions (Block Body)        |           |

### BlockHeader

- Version
- Previous Block Hash 前一块区块头的散列值
- Merkle Root Hash 记录该区块的区块体的散列值
- Timestamp
- Difficulty Target
- Nonce

![截屏2021-03-26 13.45.09](https://tva1.sinaimg.cn/large/008eGmZEgy1gox9a1h1asj30pa0codhq.jpg)

区块体中任何一个字节变化->Merkle Root Hash的值会发生变化，Merkle Root Hash在区块头里，区块头的散列值就会变化

> Genesis Block：比较特殊的区块，它的Pervious Block Hash指向的值便为空。

![截屏2021-03-26 14.42.29](https://tva1.sinaimg.cn/large/008eGmZEgy1goxaxt3n4lj31020k00wh.jpg)

### Coding in Python

#### Block

```python
class Block(object):
	def __init__(self,data,prev_block_hash=''):
		self.timestamp=int(time.time())
		self.prev_block_hash=prev_block_hash
		self.data=data
		self.data_hash=hashlib.sha256(data.encode('utf-8')).hexdigest() ##计算区块头和区块体的散列值

	def hash(self):
		data_list=[str(self.timestamp),self.prev_block_hash,self.data_hash]
		return hashlib.sha256(''.join(data_list).encode('utf-8')).hexdigest()

	def __repr__(self):
		s='Block(Hash={},TimeStamp={},PrevBlockHash={},Data={},DataHash={})'
		return s.format(self.hash(),self.timestamp,self.prev_block_hash,self.data,self.data_hash)
```

### Coding in Java--区块

```java
public class Block{
  //成员变量
  private final BlockHeader blockHeader;//区块头
    private final BlockBody blockBody;//区块体
  
  //Constructor
    public Block(BlockHeader blockHeader, BlockBody blockBody) {
        this.blockHeader = blockHeader;
        this.blockBody = blockBody;
    }
}

public class BlockHeader {

    private final int version = 1;
    private final String preBlockHash;
    private final String merkleRootHash;
    private final long timestamp;
    private final int difficulty = MiniChainConfig.DIFFICULTY;
    private long nonce;

    public BlockHeader(String preBlockHash, String merkleRootHash, long nonce) {
        this.preBlockHash = preBlockHash;
        this.merkleRootHash = merkleRootHash;
        this.timestamp = System.currentTimeMillis();
        this.nonce = nonce;
    }
}
public class BlockBody {

    private final Transaction[] transactions;
    private final String merkleRootHash;

    public BlockBody(String merkleRootHash, Transaction[] transactions) {
        this.merkleRootHash = merkleRootHash;
        this.transactions = transactions;
    }
}
```

> public String toString(){ return 自定义字符串； }有什么意义
>
> .toString 方法是将对象及其他转换成字符串的形式表达， 这个方法重新定义toString ，是返回值是自身期望的。我自己经常在枚举的过程中会有用到。

## 工作量证明

为了使区块链这个数据库具有公信力，我们必须引入工作量证明的概念。

### 如何像区块链添加新区块

规定新区块的区块头的散列值必须满足某种特性。

想添加新区块需要付出一定的代价。

### 挖矿

穷举计算散列值的过程。

挖矿过程中通过改变该区域的值来改变区块头的散列值。

**挖矿的本质，为区块头找到一个合适的Nonce值，使得区块头的散列值是一定数目的前导0**。

## 比特币的交易

实现比特币的发行，支付和余额查询等功能。

- 交易输入：比特币的来源

- 交易输出：交易后比特币的去向

- 交易结构

  ![截屏2021-03-28 08.36.22](https://tva1.sinaimg.cn/large/008eGmZEly1gozblat8h2j30h80bijtc.jpg)

  比特币交易的实现结构就是一个交易输入列表和一个交易输出列表。

- 铸币交易：在真实场景中，有0个交易输入交易是一种特殊的交易。铸币交易的比特币凭空产生，可以理解成比特币的发行过程。

  真实场景中，比特币的每个区块中都会包含一个铸币交易，每当有一个新的区块加入区块链中，就会产生一定数量的新比特币。

  这些新产生的比特币的所有者就是将区块加入区块链的矿工，他们必须完成pow

  ![img](https://doc.shiyanlou.com/courses/3056/298389/dee6a00fdcecbe7e576153a9a2a703f5-0)

- 找零

- 未花费输出模型

## 比特币地址与钱包

身份认证

1. 防止身份伪装
2. 防止否认![截屏2021-03-28 22.10.00](https://tva1.sinaimg.cn/large/008eGmZEgy1gozz3v1ez9j30os0hsacf.jpg)

### 如何将数字签名技术融合到交易模型中？

![截屏2021-03-28 22.07.35](/Users/chixinning/Library/Application Support/typora-user-images/截屏2021-03-28 22.09.36.png)

![截屏2021-03-28 22.18.37](/Users/chixinning/Library/Application Support/typora-user-images/截屏2021-03-28 22.18.37.png)

![截屏2021-03-28 22.18.48](https://tva1.sinaimg.cn/large/008eGmZEgy1gozzd1dgr5j30os0gin1k.jpg)

![截屏2021-03-28 22.19.00](https://tva1.sinaimg.cn/large/008eGmZEgy1gozzg3vk1uj30os05u76i.jpg)

![截屏2021-03-28 22.36.17](https://tva1.sinaimg.cn/large/008eGmZEgy1gozzvurqo4j315w0kywm2.jpg)

## Conclusion

- 匿名、安全、随机生成的地址
- 工作量证明系统
- 基于密码学技术的交易系统
- 区块链存储交易数据