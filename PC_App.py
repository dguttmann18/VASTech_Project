import sys
import socket
from sqlite3 import connect

#Get alarm name


# Read in alarms from text file and store it in list alarms_extracted
f = open("alarms.txt", "r")

alarms = []

for x in f:
    alarms.append(x)

f.close()

alarms_extracted = []

#Extract details from each alarm
for x in alarms:
    alarms_extracted.append(x.split("#"))
######################################################################

'''*******************************************************************************************************************'''

#Create socket
soc = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

#Bind socket to port
server_address = ('localhost', 10000)
#print('starting up on ', server_address)
soc.bind(server_address)

#Listen for incoming connections
soc.listen(1)

while True:
    #Wait for a connection
    print('waiting for a connection...')
    connection, client_address = soc.accept()

    try:
        print('connection from', client_address)

        #Receive the data in small chucks and retransmit it
        while True:
            data = connection.recv(100)
            print('received "%s"' % data.decode())
            
            alarmName = data.decode()
            '''*******************************************************************************************************************'''
            #find alarm details and print to output console
            idx = 0

            while alarms_extracted[idx][1] != alarmName:
                idx += 1

            print("ALARM NUMBER:", alarms_extracted[idx][0])
            print("ALARM NAME:", alarms_extracted[idx][1])
            print("SEVERITY LEVEL:", alarms_extracted[idx][2])
            print("ALARM DETAILS:", alarms_extracted[idx][3])

            ###############################################

            break

    finally:
        #Clean up connection
        connection.close()