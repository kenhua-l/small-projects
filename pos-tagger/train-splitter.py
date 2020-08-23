number_of_data = 38932
parts = 10
line_per_part = number_of_data / 10
print line_per_part

def train_to_test():
    for i in range(1, 11):
        w = open('test'+str(i)+'.test', 'w+')
        with open('train'+str(i)+'.train', 'r') as f:
            for line in f:
                segmented = line.split(" ");
                stitch = []
                for col in segmented:
                    stitch.append(col[:col.rindex('/')])
                stitched = ' '.join(stitch) + '\n'
                w.write(stitched)
        w.close()



def combine_nine():
    for i in range(1, 11):
        with open('train'+str(i)+'long.train', 'w+') as w:
            for j in range(1, 11):
                if j != i:
                    f = open('train' + str(j) + '.train', 'r')
                    for line in f:
                        w.write(line)
                    f.close()


def split_ten():

    with open('sents.train', 'r') as f:
        for i in range(1, 11):
            w = open('train'+str(i)+'.train', 'w+')
            countdown = line_per_part
            if i==10 or i==1:
                countdown = countdown + 1
            while countdown > 0:
                w.write(f.readline())
                countdown = countdown - 1
            w.close()

train_to_test()
