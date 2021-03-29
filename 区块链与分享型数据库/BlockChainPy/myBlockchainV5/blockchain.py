# -*- encoding: utf-8 -*-
'''
------------------------------------------------------------
-文件        : blockchain.py
-说明        : 区块链类的实现
-时间        : 2020/10/20 11:37:16
-作者        : newdao
------------------------------------------------------------
-版本号      : myBlockchainV5
------------------------------------------------------------
'''
from collections import defaultdict
from block import Block
from datetime import datetime
from proofofwork import ProofOfWork
from db import DB
from transaction import *
from transaction_input import TXInput
from transaction_output import TXOutput

class BlockchainAlreadyExists(Exception):
    pass

class NeedInitAccount(Exception):
    pass

class BlockChain(object):
    # 数据库中存储最后一个区块的散列值的key
    LAST_BLOCK_HASH_KEY = 'Last'
    # 数据库文件
    DB_FILE = './bc_blocks.db'

    def __init__(self, init_account=None):
        self.pow = ProofOfWork()  # 创建工作量证明类
        self._db = DB(BlockChain.DB_FILE) # 创建区块链数据库
        try:
            self._tail = self._db.get(BlockChain.LAST_BLOCK_HASH_KEY)
            if init_account != None:
                raise BlockchainAlreadyExists()
        except KeyError:
            print('Create a new blockchain.\n')
            if init_account == None:
                raise NeedInitAccount()
            coinbase_tx = CoinbaseTx(init_account)
            genesis_block = self.pow.mine_block([coinbase_tx]) # 通过工作量证明类来挖出创世区块
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

    def find_unspent_transactions(self, account):
        unspent_transactions = []
        spent_outputs = defaultdict(list)

        for block in self.blocks:
            for tx in block.data:
                if not isinstance(tx, CoinbaseTx):
                    for vin in tx.vins:
                       if vin.used_by_account(account):
                           spent_outputs[vin.tx_id].append(vin.vout_idx)

                for idx, out in enumerate(tx.vouts):
                    if out.can_be_spent_by_account(account) == False:
                        continue
                    _spent_flag = False 
                    if len(spent_outputs[tx.id]) != 0:
                        for spent_idx in spent_outputs[tx.id]:
                            if spent_idx == idx:
                                _spent_flag = True
                                break
                    if _spent_flag==False:
                        unspent_transactions.append(tx) 
                
        return unspent_transactions

    def find_spendable_outputs(self, account, amount):
        accumulated = 0
        unspent_outputs = defaultdict(list)        

        unspent_txs = self.find_unspent_transactions(account)

        for tx in unspent_txs:
            for idx, out in enumerate(tx.vouts):
                if out.can_be_spent_by_account(account) and accumulated<amount:
                    accumulated += out.amount
                    unspent_outputs[tx.id].append(idx)
        
        return accumulated, unspent_outputs

    def find_utxo(self, account):
        utxos = []
        unspent_txs = self.find_unspent_transactions(account)

        for tx in unspent_txs:
            for out in tx.vouts:
                if out.can_be_spent_by_account(account):
                    utxos.append(out)
        
        return utxos

if __name__ == '__main__':
    bc = BlockChain('Newdao')
    print(bc.find_unspent_transactions('Newdao'))
    print(bc.find_spendable_outputs('Newdao', 30))