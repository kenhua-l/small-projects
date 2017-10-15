import csv
w = open("cleantrials/ANOVAdataRunningT.csv", "w+")
data_writer = csv.writer(w)
data_writer.writerow(['pid', 'technique', 'granularity', 'windows', 'trialNo', 'time', 'accuracy', 'steps'])
with open("cleantrials/ANOVAdata.csv", "r") as f:
    reader = csv.reader(f)
    f.readline()
    i = 1
    for line in reader:
        if i>108:
            i = 1
        line[4] = str(i)
        data_writer.writerow(line)
        i = i+1

w.close()
