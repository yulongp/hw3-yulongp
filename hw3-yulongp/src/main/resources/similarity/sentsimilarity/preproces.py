#!/usr/bin/python
# -*- coding: utf-8 -*-

'''
Created on 2014-10-20

@author: Yulong Pei
'''

import nltk
import string

def preprocessWithStop():
    stop = set()
    fin = open('../stopwords', 'r')
    for line in fin.readlines():
        stop.add(line.strip())
    fin.close()
    
    fin = open('../input', 'r')
    outf = '../data_stop'
    fout = open(outf, 'w')
    for line in fin.readlines():
        tmp = line.strip().split('\t')
        if tmp[1] == 'rel=99':
            fout.write('Q ')
        sent = tmp[2]
        tokens = nltk.word_tokenize(sent)
        for item in tokens:
            #if item.lower() in stop or item in string.punctuation:
            if item in string.punctuation:
                print item
            else:
                fout.write(item.lower() + ' ')
        fout.write('\n')
    fin.close()
    fout.close()
    return outf
    
def preprocessNoStop():
    stop = set()
    fin = open('../stopwords', 'r')
    for line in fin.readlines():
        stop.add(line.strip())
    fin.close()
    
    fin = open('../input', 'r')
    outf = '../data_stop'
    fout = open(outf, 'w')
    for line in fin.readlines():
        tmp = line.strip().split('\t')
        if tmp[1] == 'rel=99':
            fout.write('Q ')
        sent = tmp[2]
        tokens = nltk.word_tokenize(sent)
        for item in tokens:
            if item.lower() in stop or item in string.punctuation:
            #if item in string.punctuation:
                print item
            else:
                fout.write(item.lower() + ' ')
        fout.write('\n')
    fin.close()
    fout.close()
    return outf