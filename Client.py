import socket
import sys

# Creat socket
soc = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Connect the socket to the port were the server is listening
server_address = ('localhost', 10000)
print ('connecting to %s port %s' % server_address)
soc.connect(server_address)

try:
    # Send data
    message = input() #'This is the message. It will be repeated.'
    print('sending "%s"' % message)
    soc.sendto(message.encode(), server_address)

    # Look for the response
    amount_received = 0
    amount_expected = len(message)

    while amount_received < amount_expected:
        data = soc.recv(16)
        amount_received += len(data)
        #data = data.decode()
        print('received "%s"' % data.decode())

finally:
    print('closing socket')
    soc.close()

