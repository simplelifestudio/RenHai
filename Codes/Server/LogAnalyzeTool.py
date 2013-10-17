import sys

def containsAny(keys, line):
    """ Check whether sequence seq contains ANY of the items in aset. """
    for c in keys:
        if c in line: return True
    return False

def main(argv):
    inputfile = argv[1]
    output = argv[2]
    keys = argv[3].split(',')

    outputfile = open(output, 'w')
    with open(inputfile) as file:
        for line in file:
            if (containsAny(keys, line)):
                outputfile.write(line)
    outputfile.close()

if __name__ == '__main__':
    if (len(sys.argv) == 1):
        print("Parameters missed, usage of this tool:\n  LogAnalyzeTool.py inputfile outputfile keys")
        sys.exit(1)
    main(sys.argv)
