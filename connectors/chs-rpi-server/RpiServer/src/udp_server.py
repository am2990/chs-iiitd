import socket
import sys
import pulseoxy
#import fcntl
import struct

'''
def get_ip_address(ifname):
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    return socket.inet_ntoa(fcntl.ioctl(
        s.fileno(),
        0x8915,  # SIOCGIFADDR
        struct.pack('256s', ifname[:15])
    )[20:24])
'''

# Create a TCP/IP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

# Bind the socket to the port


#TODO find if using a windows machine or linux machine and use appropriate IP address technique
ip = socket.gethostbyname(socket.gethostname())
#ip_lin = get_ip_address('wlan0')

server_address = (ip, 10000)
print >>sys.stderr, 'starting up on %s port %s' % server_address
sock.bind(server_address)

while True:
        print >>sys.stderr, '\nwaiting to receive message'
        data, address = sock.recvfrom(4096)
        
        print >>sys.stderr, 'received %s bytes from %s' % (len(data), address)
        print >>sys.stderr, data
        
        if data:
                recv_sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

                #TODO find if the packet is a discover packet or request packet
                #TODO pick remote ip addresses from json
                #TODO extract port from message
                #TODO populate sensors from a settings file

                if "discover" in data :
                        server_address = (address[0], 10001)
                        message = """{ type: "discover_reply", name:"rpi_board" , sdr:"""+str(ip)+"""  , rcv:"192.168.17.72" , mac:"11:22:33:44:55:66", time:1234 , argv:[{sensorid:1,sensorname:nonin,sensortype:medical}, {sensorid:2,sensorname:temp,sensortype:medical}]}"""

                        try:
                                # Send data
                                print >>sys.stderr, 'sending "%s"' % message
                                sent = recv_sock.sendto(message, server_address)
                        
                        finally:
                                print >>sys.stderr, 'closing socket'
                                recv_sock.close()

                elif "request" in data:
            #readings=pulseoxy.readsensor(10)
                        readings = [[78,32],[32,22],[44,34],[11,54]]
                        server_address = (address[0], 10002)
                        message = """{type:request_reply, sdr: 192.168.48.21, rcv: 192.168.48.21, time: 1222 , argv: [ {"sensorid": 1, "readings":""" + str(readings)+ """ }] }"""

                        try:
                                # Send data
                                print >>sys.stderr, 'sending "%s to %s "' % (message , server_address)
                                sent = recv_sock.sendto(message, server_address)
                        
                        finally:
                                print >>sys.stderr, 'closing socket'
                                recv_sock.close()
                else:
                        print "faulty packet"

 
