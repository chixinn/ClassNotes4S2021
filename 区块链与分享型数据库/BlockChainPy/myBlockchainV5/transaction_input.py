# -*- encoding: utf-8 -*-
'''
------------------------------------------------------------
-文件        : transaction_input.py
-说明        : 
-时间        : 2020/12/04 10:35:12
-作者        : newdao
------------------------------------------------------------
-版本号      ：myBlockchainV5
------------------------------------------------------------
'''

class TXInput(object):
    def __init__(self, tx_id, vout_idx, account):
        self.tx_id = tx_id # 表示交易标示用于唯一识别一个交易
        self.vout_idx = vout_idx # 表示交易重的输出列表的下标
        self.account = account # 标示引用者的账户

    def used_by_account(self, account):
        return account==self.account 

    def __repr__(self):
        return 'TXInput(tx_id={}, vout_idx={}, account={})'.format(self.tx_id, self.vout_idx, self.account)