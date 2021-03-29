# -*- encoding: utf-8 -*-
'''
------------------------------------------------------------
-文件        : bccli.py
-说明        : 区块链命令行接口实现
-时间        : 2020/11/18 11:40:37
-作者        : newdao
------------------------------------------------------------
-版本号      ：myBlockchainV5
------------------------------------------------------------
'''
'''
python3 bccli.py printchain
python3 bccli.py createblockchain -a INIT_ACCOUNT
python3 bccli.py getblance -a ACCOUNT
python3 send -f FROM_ACCOUNT -t TO_ACCOUNT -m AMOUNT
'''
import argparse
from blockchain import BlockChain
from transaction import *


def print_chain(args):
    bc = BlockChain()
    bc.print_chain()

def createblockchain(args):
    args_dict = vars(args)
    account = args_dict['account']
    bc = BlockChain(account)
    print('Done!')

def getblance(args):
    args_dict = vars(args)
    account = args_dict['account']

    bc = BlockChain()
    utxos = bc.find_utxo(account)
    blance = 0

    for out in utxos:
        blance += out.amount

    print('Blance of "{}": {}'.format(account, blance))

def send(args):
    args_dict = vars(args)
    from_account = args_dict['from']
    to_account = args_dict['to']
    amount = int(args_dict['amount'])

    bc = BlockChain()
    tx = UTXOTransaction(from_account, to_account, amount, bc)
    cb_tx = CoinbaseTx(from_account)
    tx_list = [cb_tx, tx]
    bc.mine_block(tx_list)
    print('Success!')


class BcCli(object):
    def __init__(self):
        self._parser = argparse.ArgumentParser()
        sub_parser = self._parser.add_subparsers(help='commands')

        # printchain command
        print_parser = sub_parser.add_parser(
                       'printchain', help='Print all the blocks of the blockchain.')
        print_parser.set_defaults(func=print_chain)

        # createblockchain command
        createchain_parser = sub_parser.add_parser('createblockchain', help='Create a blockchain and send genesis block reward to ACCOUNT')
        createchain_parser.add_argument('-a','--account', help='account')
        createchain_parser.set_defaults(func=createblockchain)

        # getblance command
        blance_parser = sub_parser.add_parser('getblance', help='Get balance of account')
        blance_parser.add_argument('-a','--account', required=True, help='account')
        blance_parser.set_defaults(func=getblance)

        # send command
        send_parser = sub_parser.add_parser('send', help='Send AMOUNT of coins from FROM account to TO')
        send_parser.add_argument('-f','--from', required=True, help='from account')
        send_parser.add_argument('-t','--to', required=True, help='to account')
        send_parser.add_argument('-m','--amount', required=True, help='amount')
        send_parser.set_defaults(func=send)


    def run(self):
        args = self._parser.parse_args()
        if hasattr(args, 'func'):
            args.func(args)

if __name__ == '__main__':
    bccli = BcCli()
    bccli.run()
	
