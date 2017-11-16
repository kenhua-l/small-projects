import sys

def main():
    file1 = open(sys.argv[1])
    file2 = open(sys.argv[2])
    classified = 0
    wrong = 0
    for line in file1:
        lineb = file2.readline().split()
        linea = line.split()
        classified = classified + 1
        if lineb[0] != linea[0]:
            print "wrong texts classified"
            break
        if lineb[1] != linea[1]:
            wrong = wrong + 1
            print linea[0], "tagged as", lineb[1], "but should be", linea[1]

    file1.close()
    file2.close()
    print "wrong is", wrong, "/", classified
    print "correct is", classified-wrong, "/", classified

main()
