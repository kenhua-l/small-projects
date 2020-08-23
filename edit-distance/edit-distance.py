def insert(ch):
    return 1

def delete(ch):
    return 1

def sub(src, tgt):
    if src == tgt:
        return 0
    else:
        return 2

def pretty_print(mat, target, source):
    print '-', '-', ' '.join([ch for ch in target]);
    source = ''.join(["-", source])
    for sc, line in zip(source, mat):
        print sc, ' '.join(str(i) for i in line)


def edit_distance(target, source):
    n = len(target)
    m = len(source)
    mat = [ [ None for x in range(n+1) ] for y in range(m+1) ]
    mat[0][0] = 0
    for i in range(1, n+1):
        mat[0][i] = mat[0][i-1] + 1
    for j in range(1, m+1):
        mat[j][0] = mat[j-1][0] + 1
    for i in range(1, n+1):
        for j in range(1, m+1):
            mat[j][i] = min([mat[j-1][i-1] + sub(source[j-1], target[i-1]),
                             mat[j][i-1] + 1,
                             mat[j-1][i] + 1])

    pretty_print(mat, target, source)
    return mat[m][n]

def main():
    source = raw_input("Enter source: ")
    target = raw_input("Enter target: ")
    print edit_distance(target, source)
    print source, target

main()
