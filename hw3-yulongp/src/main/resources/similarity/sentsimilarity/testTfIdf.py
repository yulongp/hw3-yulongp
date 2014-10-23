#!/usr/bin/python
# -*- coding: utf-8 -*-

'''
Created on 2014-10-20

@author: Yulong Pei
'''

import tfidf

table = tfidf.tfidf()
table.addDocument("foo", ["alpha", "bravo", "charlie", "delta", "echo", "foxtrot", "golf", "hotel"])
table.addDocument("bar", ["alpha", "bravo", "charlie", "india", "juliet", "kilo"])
table.addDocument("baz", ["kilo", "lima", "mike", "november"])

print table.similarities (['alpha', 'bravo', 'charlie'])