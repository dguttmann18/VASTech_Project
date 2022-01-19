import socket
import sys

while True:
    # Create socket
    soc = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    # Connect the socket to the port were the server is listening
    server_address = ('localhost', 10000)
    #print ('connecting to %s port %s' % server_address)
    soc.connect(server_address)

    try:
        # Send data
        message = input()
        print('sending "%s"' % message)
        soc.sendto(message.encode(), server_address)

    finally:
        print('closing socket')
        soc.close()