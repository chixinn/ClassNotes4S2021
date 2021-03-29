class TXInput(object):
    def __init__(self,tx_id,vout_idx,account) :
        self.tx_id=tx_id
        self.vout_idx=vout_idx
        self.account=account
    def used_by_account(self,account):
        return account==self.account
    def __repr__(self):
        return "TXInput(tx_id={},vout_idx={},account={})".format(self.tx_id,self.vout_idx,self.account)