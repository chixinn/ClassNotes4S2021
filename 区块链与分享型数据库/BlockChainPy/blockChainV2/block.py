import time
import hashlib
class Block(object):
	def __init__(self,data,prev_block_hash=''):
		self.timestamp=int(time.time())
		self.prev_block_hash=prev_block_hash
		self.data=data
		self.nonce=0
		self.data_hash=hashlib.sha256(data.encode('utf-8')).hexdigest() ##计算区块头和区块体的散列值

	def hash(self):
		data_list=[str(self.nonce),str(self.timestamp),self.prev_block_hash,self.data_hash]
		return hashlib.sha256(''.join(data_list).encode('utf-8')).hexdigest()

	def __repr__(self):
		s='Block(Hash={},TimeStamp={},PrevBlockHash={},Data={},DataHash={})'
		return s.format(self.hash(),self.timestamp,self.prev_block_hash,self.nonce,self.data,self.data_hash)
	