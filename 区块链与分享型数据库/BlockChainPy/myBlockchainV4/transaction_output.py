class TXOutput(object):
    def __init__(self,amount,account):
        self.amount=amount #输出的额度
        self.account=account #输出的账户
    def can_be_spent_by_account(self,account):
        return self.account==account
    
    def __repr__(self):
        return "TXOutput(account={},amount={})".format(self.account,self.amount)