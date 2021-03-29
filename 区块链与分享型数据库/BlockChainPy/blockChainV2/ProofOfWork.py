import sys
from block import Block

class ProofOfWork(object):
    MAX_NONCE=sys.maxsize
    
    def __init__(self,difficulty_bits=12) :
        self._target=1 <<(256-difficulty_bits)

    def mine_block(self,data,prev_hash=''):
        # 准备区块头
        tmp_block=Block(data,prev_hash)
        # 计算区块头的散列值，将其转化为整数形式
        while tmp_block.nonce<ProofOfWork.MAX_NONCE:
            hash_int=int(tmp_block.hash(),16)
            if hash_int<self._target:
                break
            else:
                tmp_block.nonce+=1
        return tmp_block
    
    def validate_block(self,block):
        hash_int=int(block.hash(),16)
        return True if hash_int<self._target else False