# -*- encoding: utf-8 -*-
'''
------------------------------------------------------------
-文件        : blockchain.py
-说明        : 区块链类的实现
-时间        : 2020/10/20 11:37:16
-作者        : newdao
------------------------------------------------------------
-版本号      : myBlockchainV4
------------------------------------------------------------
'''
from block import Block
from datetime import datetime
from proofofwork import ProofOfWork
from db import DB

class BlockChain(object):
    # 数据库中存储最后一个区块的散列值的key
    LAST_BLOCK_HASH_KEY = 'Last'
    # 数据库文件
    DB_FILE = './bc_blocks.db'

    def __init__(self):
        print('Created a new blockchain.\n')
        self.pow = ProofOfWork()  # 创建工作量证明类
        self._db = DB(BlockChain.DB_FILE) # 创建区块链数据库
        try:
            self._tail = self._db.get(BlockChain.LAST_BLOCK_HASH_KEY)
        except KeyError:
            genesis_block = self.pow.mine_block('This is a genesis block') # 通过工作量证明类来挖出创世区块
            self._put_block(genesis_block)

    # 将区块存放到数据库中，并更新_tail成员
    def _put_block(self, block):
        self._db.put(block.hash(), block)
        self._db.put(BlockChain.LAST_BLOCK_HASH_KEY, block.hash())
        self._db.commit()
        self._tail = block.hash()

    # 挖出一个数据为data的区块
    def mine_block(self, data): 
        new_block = self.pow.mine_block(data, self._tail)
        self._put_block(new_block)

    @property
    def blocks(self):
        current = self._tail
        while True:
            if not current: # 为空，表明已遍历到区块链的创世块
                return
            block = self._db.get(current)
            yield block
            current = block.prev_block_hash
            
    def print_chain(self):
        for block in self.blocks:
            print('Block Hash: {}'.format(block.hash()))
            print('Prev Block Hash: {}'.format(block.prev_block_hash))
            print('TimeStamp: {}'.format(datetime.fromtimestamp(block.timestamp)))
            print('Nonce: {}'.format(block.nonce)) # 增加打印 nonce值
            print('Data: {}'.format(block.data))
            print('POW: {}'.format(self.pow.validate_block(block)))
            print('')