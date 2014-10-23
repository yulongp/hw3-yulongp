#!/usr/bin/python
# -*- coding: utf-8 -*-

'''
Created on 2014-10-20

@author: Yulong Pei
'''

import preproces
import similarity

if __name__ == '__main__':
    fn1 = preproces.preprocessWithStop()
    fn2 = preproces.preprocessNoStop()
    similarity.mmr(fn1)
    similarity.mmr(fn2)