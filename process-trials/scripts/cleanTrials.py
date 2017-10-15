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
        clean(str(i))

def clean(file_num):
    exp_file = "trials/acp-" + file_num + "-trials.csv"
    out_file = "cleantrials/data-" + file_num + ".csv"
    f = open(exp_file, 'rb')
    w = open(out_file, 'wb')
    datum = csv.reader(f)
    data_writer = csv.writer(w)
    datum.next() #ignore header
    datum.next() #ignore 5 trials
    datum.next()
    datum.next()
    datum.next()
    datum.next()
    data_writer.writerow(['pid', 'technique', 'granularity', 'windows', 'trialNo', 'time', 'accuracy', 'steps'])
    trial_no = 1
    for data in datum:
        row = [data[ID], data[TECHNIQUE], data[GRANULARITY], data[WINDOW], trial_no, data[SPEED], data[ACCURACY], data[STEPS]]
        data_writer.writerow(row)
        if trial_no == 3:
            trial_no = 1
        else:
            trial_no = trial_no + 1

    f.close()
    w.close()


main()
