fileName = []

newFile = 0
for i in range(0, len(fileName), 10000):
    file = open(str(newFile))
    file.write(fileName[i:10000])
