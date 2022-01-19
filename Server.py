import socket
from sqlite3 import connect
import sys

#Create socket
soc = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

#Bind socket to port
server_address = ('localhost', 10000)
print('starting up on ', server_address)
soc.bind(server_address)

#Listen for incoming connections
soc.listen(1)

while True:
    #Wait for a connection
    print('waiting for a connection')
    connection, client_address = soc.accept()

    try:
        print('connection from', client_address)

        #Receive the data in small chucks and retransmit it
        while True:
            data = connection.recv(16)
            print('received "%s"' % data.decode())
            if data:
                #print('sending data back to the client')
                data = str.encode(input())
                connection.sendall(data)
            else:
                #print('no more data from', client_address)
                break
    finally:
        #Clean up connection
        connection.close()
