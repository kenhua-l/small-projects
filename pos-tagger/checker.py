import sys

def main():
    a=open(sys.argv[1])
    b=open(sys.argv[2])
    tagged = 0
    wrong = 0
    for line in a:
        lineb = b.readline().split()
        linea = line.split()
        print "---"
        for worda, wordb in zip(linea, lineb):
            tagged = tagged + 1
            if worda != wordb:
                wrong = wrong + 1
                # print "(",worda,",",wordb,")","correct"
            # else:
                print "(",worda,",",wordb,")","Wrong"
    a.close()
    b.close()
    print "wrong is", wrong, "/", tagged
    print "correct is", tagged-wrong, "/", tagged

main()
