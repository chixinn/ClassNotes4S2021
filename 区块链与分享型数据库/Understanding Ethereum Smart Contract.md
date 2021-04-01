# Understanding Ethereum Smart Contract

[文章链接](http://www.gjermundbjaanes.com/understanding-ethereum-smart-contracts/)

- 智能合约只是在区块链上运行的一段代码。

- 区块链就是分布式账本，由 a list of transactions 组成

- 比特币的激励机制保证了不需要parties的互相可信。This distribution coupled with strong **monetary incentives** removes the need for trust between parties.

  这种激励机制保证了，cheat(破坏thrust)带来的结果还没有stay within rules高。

  

## 智能合约是什么？

智能合约既不智能也不是合约，他们是只可以在区块链上运行的code。

### 智能合约账户

#### 用户

账户：地址+余额

#### 智能合约账户

账户: 地址+余额+状态+代码

address+balance(of Ether,Ether在这里是以太币)+state+Code

### 地址：

所谓的identifier罢了

### Balance:

The only exciting thing is that a balance on a smart contract means that **code can own money**. It can handle money. And it can mishandle it if incorrectly coded.

智能合约code可以拥有money,他们可以很好的理财，但如果代码编写的不好，也失财:D

### state

**The state** of a smart contract account is the current state of **all fields and variables** declared in the smart contract. 

智能合约状态指，智能合约中当前中被声明的所有fields和variables.

It works the same way as **field variables** in a class in most programming languages.In fact, **an instantiated object** from a class is probably the easiest way to think about smart contracts. The only difference is that the **object lives foreve**r (unless programmed to self-destruct).

### Code

**The code** of the smart contract is compiled byte-code that Ethereum clients and nodes can run.

以太坊客户端和nodes都可以运行的二进制代码

It is the code that is executed on the creation of the smart contract, and it contains functions that you can call. 

一建立智能合约便execute the code，而且里面包含了一些你可以调用的函数(function)

Just like any object in an object-oriented programming language.

和很多OOP语言一样

## 智能合约和比特币中Transaction概念的不同--Ethereum vs. Bitcoin on the Transaction-Level

![截屏2021-04-01 12.57.34](https://tva1.sinaimg.cn/large/008eGmZEly1gp45miuiuyj30v60u0jyc.jpg)

| 类型                     | TO                   | DATA             | FROM | AMOUNT                         |
| ------------------------ | -------------------- | ---------------- | ---- | ------------------------------ |
| 转账                     | 接受人地址           | Any message      | You  | 以太币                         |
| 创建智能合约账户         | Null                 | 智能合约代码     | You  | 你想给你账户初始化的以太币数目 |
| 调用智能合约账户中的函数 | 智能合约合约账户地址 | 函数名和调用参数 | You  | 0，或你想让合约为你做什么服务  |



### Main Concept about 以太坊transaction

you send the transaction to the address of the smart contract you want to call and you put the function call in the **DATA** field.

## Gas

Gas is the fuel that runs the Ethereum Virtual Machine.

think of it as payment per execution of instruction (almost like a line of code).

代码执行一条要付一定量的gas

### Gas怎么决定的

The cost of gas (execution) is decided by the miners of the network - the nodes that are running the code.

你编写的代码的执行到底要消耗多少的gas，有network中的矿工节点决定。

在这里矿工节点指网络中执行代码的计算机节点

### Set max amount of gas in case of dead loop

### 智能合约调用

调用智能合约账户和调用某个object的某个方法是一个道理，唯一的不同在于，你所有的信息都要处于交易之中，像比特币一样，你也要形成交易，签名，发给网络执行。

Calling a smart contract is conceptually the same thing.

 The only difference is that you have to put all the information about the call into a transaction, sign it and send it to the network to execute.

## Call a smart contract

![截屏2021-04-01 14.17.44](https://tva1.sinaimg.cn/large/008eGmZEly1gp47xq1vgaj30l60p8dlh.jpg)

## 世界计算机

以太坊就如同被全世界维护的虚拟机一样。

World-running Virtual Machine.