# 区块链--Coding 

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
# 1. 生成密钥对
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