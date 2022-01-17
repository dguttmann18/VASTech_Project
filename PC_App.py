import sys

#Get alarm name
alarmName = sys.argv[1]

# Read in alarms from text file
f = open("alarms.txt", "r")

alarms = []

for x in f:
    alarms.append(x)

alarms_extracted = []

#Extract details from each alarm
for x in alarms:
    alarms_extracted.append(x.split("#"))

idx = 0

while alarms_extracted[idx][1] != alarmName:
    idx += 1

print("ALARM NUMBER:", alarms_extracted[idx][0])
print("ALARM NAME:", alarms_extracted[idx][1])
print("SEVERITY LEVEL:", alarms_extracted[idx][2])
print("ALARM DETAILS:", alarms_extracted[idx][3])


