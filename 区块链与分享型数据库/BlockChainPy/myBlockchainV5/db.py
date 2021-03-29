# -*- encoding: utf-8 -*-
'''
------------------------------------------------------------
-文件        : db.py
-说明        : 区块链数据库接口
-时间        : 2020/11/10 15:18:19
-作者        : newdao
------------------------------------------------------------
-版本号      ：myBlockchainV5
------------------------------------------------------------
'''
import pickle

class DB():
    def __init__(self, db_file):
        self._db_file = db_file
        try:
            with open(self._db_file, 'rb') as f:
                self.kv = pickle.load(f)
        except FileNotFoundError:
            self.kv = {}

    def commit(self):
        with open(self._db_file, 'wb') as f:
            pickle.dump(self.kv, f)

    def get(self, key):
        return self.kv[key]

    def put(self, key, value):
        self.kv[key] = value

    def delete(self,  key):
        del self.kv[key]

    def reset(self):
        self.kv = {}