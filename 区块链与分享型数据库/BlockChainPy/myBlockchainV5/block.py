# -*- encoding: utf-8 -*-
'''
------------------------------------------------------------
-文件        : block.py
-说明        : 区块的实现类
-时间        : 2020/10/20 11:37:16
-作者        : newdao
------------------------------------------------------------
-版本号      : myBlockchainV5
------------------------------------------------------------
'''
import time
import hashlib
from transaction import *

class Block(object):
    def __init__(self, transaction_list, prev_block_hash=''):
        self.timestamp = int(time.time())
        self.prev_block_hash = prev_block_hash
        self.data = transaction_list
        self.nonce = 0 # 添加 nonce 成员，初始值为 0
        self.data_hash = hashlib.sha256(''.join([tx.hash() for tx in transaction_list]).encode('utf-8')).hexdigest()
        
    def hash(self): # 计算散列值时，同样加入 nonce值
        data_list = [str(self.nonce),str(self.timestamp), self.prev_block_hash, self.data_hash]
        return hashlib.sha256(''.join(data_list).encode('utf-8')).hexdigest()

    def __repr__(self): # 打印输出，同样加入 nonce值
        return 'Block(Hash={}, TimeStamp={}, PrevBlockHash={}, Nonce={}, Data={}, DataHash={})'.format(
                self.hash(), self.timestamp, self.prev_block_hash, self.nonce, self.data, self.data_hash)

if __name__ == '__main__':
    block = Block([])
    print(block)