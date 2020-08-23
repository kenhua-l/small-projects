
NUMBER_OF_FOLD = 5

def read_train_list(file_name):
    file_list_and_class = dict();
    with open(file_name, "r") as f:
        for line in f:
            segments = tuple(line.split())
            if segments[1] not in file_list_and_class:
                file_list_and_class[segments[1]] = [segments[0]]
            else:
                file_list_and_class[segments[1]].append(segments[0])
    return  file_list_and_class

def main():
    file_list_and_class = read_train_list("train-class-list")
    for i in range(1,NUMBER_OF_FOLD+1):
        wt = open("5-fold-data/test-list-"+str(i), "w+")
        wta = open("5-fold-data/test-list-out-"+str(i), "w+")
        with open("5-fold-data/train-list-"+str(i), "w+") as w:
            for key in file_list_and_class:
                interval = len(file_list_and_class[key]) / NUMBER_OF_FOLD
                start = 0
                for j in range(5):
                    if (i-1)*interval == start:
                        for k in range(start, start+interval):
                            line = file_list_and_class[key][k] + '\n'
                            wt.write(line)
                            line = file_list_and_class[key][k] + " " + key +'\n'
                            wta.write(line)
                    else:
                        for k in range(start, start+interval):
                            line = file_list_and_class[key][k] + " " + key +'\n'
                            w.write(line)
                    start = start + interval
            wt.close()
            wta.close()


main()
