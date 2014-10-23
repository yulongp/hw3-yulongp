#!/usr/bin/python
# -*- coding: utf-8 -*-

'''
Created on 2014-10-20

@author: Yulong Pei
'''

import tfidf

def tfidfSim(infile):
    table = tfidf.tfidf()
    res = []
    fin = open(infile, 'r')
    content = []
    index = 1
    query = []
    for line in fin.readlines():
        #table = tfidf.tfidf()
        if line[0] == 'Q':
            index = 1
            if len(content) == 0:
                query = line[2:].strip().split()
                #print query
            else:
                res.append(table.similarities(query))
                query = line[2:].strip().split()
                table.clear()
                content = []
        else:
            tmp = line.strip().split()
            table.addDocument(str(index), tmp)
            #print str(index), tmp
            content.append(line)
            index += 1
    res.append(table.similarities(query))
    fin.close()
    return res

def mmr(infile):
    mmr = 0
    res = tfidfSim(infile)
    for item in res:
        rank = 1
        value = 0.0
	tmp = []
        for it in item:
	    tmp.append(it[1])
            #if value < it[1]:
            #    value = it[1]
            #    rank = int(it[0])
	for i in range(1, len(tmp)):
	    if tmp[0] < tmp[i]:
		rank += 1        
	print rank    
        mmr += 1.0/rank
    print mmr/20
    return mmr/20

mmr('../data_stop')
print '------------'
mmr('../data_nostop')
