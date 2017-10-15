# -*- coding: utf-8 -*-
import json
import csv

ID = 0
TECHNIQUE = 1
GRANULARITY = 2
WINDOW = 3
TRIAL_NUM = 4
STIMULI = 5

RESPONSE = 6
SPEED = 9
ACCURACY = 10
STEPS = 11
CLICK = 12
DELS = 13
CPS = 14
SELS = 15
TYPE = 16
ARROW = 17

def main():
    for i in range(1, 13):
        check(str(i))

def check_empty(row):
    pass

def check_with_json(data, row):
    check_empty(row)
    if row[TECHNIQUE] != data["technique"]:
        print "technique not same at ", row[TRIAL_NUM]
        return False
    if row[GRANULARITY] != data["granularity"]:
        print "granularity not same at ", row[TRIAL_NUM]
        return False
    if row[WINDOW] != data["windows"]:
        print "windows not same at ", row[TRIAL_NUM]
        return False
    preProcess = row[STIMULI].replace('%u2019', u'’')
    if preProcess != data["stimuli"]:
        print "stimuli not same at ", row[TRIAL_NUM]
        print preProcess
        print data["stimuli"]
        return False
    return True

def check(file_num):
    in_file = "experiment/experiment" + file_num + ".json"
    exp_file = "trials/acp-" + file_num + "-trials.csv"
    datum = ""
    datum_collected = ""
    clear = True
    with open(in_file, 'r') as f:
        datum = json.load(f)

    with open(exp_file, 'r') as g:
        datum_collected = csv.reader(g, delimiter=',')
        next(datum_collected, None)
        running_trials = 1
        for data,row in zip(datum["experiments"], datum_collected):
            if str(row[ID]) != file_num:
                print "ID not same for ", file_num
                clear = False
            if row[TRIAL_NUM] != str(running_trials):
                print "trials number not running at ", running_trials, row[TRIAL_NUM]
                clear = False
            clear = check_with_json(data, row)
            running_trials = running_trials + 1
            num_steps = int(row[CLICK]) + int(row[DELS]) + int(row[CPS]) + int(row[SELS]) + int(row[TYPE]) + int(row[ARROW])
            if int(row[STEPS]) != num_steps:
                print num_steps, row[STEPS]
                print "step count not correct at ", file_num, row[TRIAL_NUM]
            if row[RESPONSE] == "" or row[STEPS] == 0:
                print "empty response at ", file_num, row[TRIAL_NUM]
            # if row[ACCURACY] == str(0):
            #     print "not accurate at ", file_num, row[TRIAL_NUM]
    print clear

main()
