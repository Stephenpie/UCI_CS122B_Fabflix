def non_scaled():
	TS = 0
	TJ = 0
	log = open("LOG FILE PATH", "r")
	count = 0
	for line in log:
		count += 1
		TJ += int(line.split("   ")[0])
		TS += int(line.split("   ")[1][0:-1])

	print (TJ * 1.0 / count) / 10**6
	print (TS * 1.0 / count) / 10**6

def scaled():
	TS = 0
	TJ = 0
	log = open("LOG FILE PATH", "r")
	countMaster = 0
	for line in log:
		countMaster += 1
		TJ += int(line.split("   ")[0])
		TS += int(line.split("   ")[1][0:-1])

	log = open("LOG FILE PATH", "r")
	countSlave = 0
	for line in log:
		countSlave += 1
		TJ += int(line.split("   ")[0])
		TS += int(line.split("   ")[1][0:-1])

	print (TJ * 1.0 / (countSlave + countMaster)) / 10**6
	print (TS * 1.0 / (countSlave + countMaster)) / 10**6
	print countSlave + countMaster

if __name__ == '__main__':
	#scaled()
	non_scaled()
	

