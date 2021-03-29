# -*- encoding: utf-8 -*-
'''
------------------------------------------------------------
-文件        : transaction_output.py
-说明        : 
-时间        : 2020/12/04 10:09:32
-作者        : newdao
------------------------------------------------------------
-版本号      ：myBlockchainV5
------------------------------------------------------------
'''

class TXOutput(object):
    def __init__(self, account, amount):
        self.amount = amount
        self.account = account

    def can_be_spent_by_account(self, account):
        return self.account == account

    def __repr__(self):
        return "TXOutput(acount={}, amount={})".format(self.account, self.amount)