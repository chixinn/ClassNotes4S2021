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