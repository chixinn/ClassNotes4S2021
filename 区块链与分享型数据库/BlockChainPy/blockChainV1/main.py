from block import Block
from blockchain import BlockChain

if __name__=='__main__':
    block_chain=BlockChain()
    block_chain.add_block("第一个交易是转账10块")
    block_chain.add_block("第二个交易是转账20")
    block_chain.print_chain()
