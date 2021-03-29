import datetime
from block import Block
from ProofOfWork import ProofOfWork
class BlockChain(object):
    def __init__(self):
        print("创建一个新区块链\n")
        self.chain=[]
        # genesis_block=Block('创世区块')
        self.pow=ProofOfWork()
        genesis_block=self.pow.mine_block('通过工作量证明类挖出创世区块')
        self.chain.append(genesis_block)
    
    #挖出一个数据为data的区块
    def mine_block(self,data):
        new_block=self.pow.mine_block(data,self.chain[-1].hash())
        self.chain.append(new_block)

    # def add_block(self,data):
    #     new_block=Block(data,self.chain[-1].hash()) #上一个区块链的prevBlockHash
    #     self.chain.append(new_block)

    def print_chain(self):
        for block in self.chain:
            print('Block Hash: {}'.format(block.hash()))
            print('Prev Block Hash: {}'.format(block.prev_block_hash))
            print('TimeStamp: {}'.format(block.timestamp))
            print('Data: {}'.format(block.data))
            print('Nonce: {}'.format(block.nonce))
            print('POW: {}'.format(self.pow.validate_block(block)))
            print('')

