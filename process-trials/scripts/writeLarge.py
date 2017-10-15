import csv

ID = 0
TECHNIQUE = 1
GRANULARITY = 2
WINDOW = 3
TRIAL_NUM = 4

SPEED = 9
ACCURACY = 10
STEPS = 11

def main():
    large_file = "cleantrials/ANOVAdata.csv"
    w = open(large_file, 'wb')
    data_writer = csv.writer(w)
    data_writer.writerow(['pid', 'technique', 'granularity', 'windows', 'trialNo', 'time', 'accuracy', 'steps'])

    for i in range(1, 13):
        out_file = "cleantrials/data-" + str(i) + ".csv"
        f = open(out_file, 'rb')
        datum = csv.reader(f)
        datum.next()
        for data in datum:
            data_writer.writerow(data)
        f.close()
    w.close()


main()
