#!/bin/bash

javac tc_train.java
java tc_train stopword-list 5-fold-data/train-list-5 model
java tc_test stopword-list model 5-fold-data/test-list-5 test-class-list
python checker.py 5-fold-data/test-list-out-5 test-class-list 
