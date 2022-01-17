import sys
import random
import os

f = open("alarms.txt", "r")

alarms = []

for x in f:
    alarms.append(x.split("#"))

r = random.randint(0, len(alarms) - 1)

s = "python3 PC_App.py " + alarms[r][1]

os.system(s)