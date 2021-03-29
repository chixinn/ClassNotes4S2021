# -*- encoding: utf-8 -*-
'''
------------------------------------------------------------
-文件        : bccli.py
-说明        : 区块链命令行接口实现
-时间        : 2020/11/18 11:40:37
-作者        : newdao
------------------------------------------------------------
-版本号      ：
------------------------------------------------------------
'''
'''
python3 bccli.py mineblock -d "data string"
python3 bccli.py printchain
'''
import argparse
from blockchain import BlockChain

def mine_block(args):
    args_dict = vars(args)
    data = args_dict['data']

    bc = BlockChain()
    bc.mine_block(data)

def print_chain(args):
    bc = BlockChain()
    bc.print_chain()

class BcCli(object):
    def __init__(self):
        self._parser = argparse.ArgumentParser()
        sub_parser = self._parser.add_subparsers(help='commands')

        # printchain command
        print_parser = sub_parser.add_parser(
                       'printchain', help='Print all the blocks of the blockchain.')
        print_parser.set_defaults(func=print_chain)

        # mineblock command
        mine_parser = sub_parser.add_parser(
                      'mineblock', help='Mine a new block for the blockchain.')
        mine_parser.add_argument('-d', '--data', required=True, help='block data')
        mine_parser.set_defaults(func=mine_block)

    def run(self):
        args = self._parser.parse_args()
        if hasattr(args, 'func'):
            args.func(args)

if __name__ == '__main__':
    bccli = BcCli()
    bccli.run()
	
