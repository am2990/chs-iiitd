import socket
import sys
import fcntl
import struct
import platform
import pulse
import time
import commands
import bp


def sensorconnected():
	p={}
	b={}
	sensors=[]
	port1 = '/dev/ttyUSB0'
	port2 = '/dev/ttyUSB1'
	bp=''
	po=''
	portlist=commands.getoutput("ls /dev/ttyUSB*")
	if 'USB0' in portlist and 'USB1' in portlist:
		try:
			pup.readsensor(1,port1)
			po=port1
			bp=port2
			p['sensorid']=1
			p['sensorname']='nonin'
			p['sensortype']='medical'
			b['sensorid']=2
			b['sensorname']='bp'
			b['sensortype']='medical'
			sensors.append(p)
			sensors.append(b)
		except ValueError:
			po=port2
			bp=port1
			b['sensorid']=1
			b['sensorname']='bp'
			b['sensortype']='medical'
			p['sensorid']=2
			p['sensorname']='nonin'
			p['sensortype']='medical'
			sensors.append(b)
			sensors.append(p)
		return po,bp,sensors
	elif 'USB0' in portlist:
		try:
			pup.readsensor(1,port1)
			po=port1
			p['sensorid']=1
			p['sensorname']='nonin'
			p['sensortype']='medical'
			sensors.append(p)
		except ValueError:
			print "only BP sensor connected in PORT1"
			bp=port1
			b['sensorid']=1
			b['sensorname']='bp'
			b['sensortype']='medical'
			sensors.append(b)
		return po,bp,sensors
	elif 'USB1' in portlist:
		try:
			pup.readsensor(1,port2)
			po=port2
			p['sensorid']=1
			p['sensorname']='nonin'
			p['sensortype']='medical'
			sensors.append(p)
		except ValueError:
			print "only BP sensor connected in PORT2"
			bp=port2
			b['sensorid']=1
			b['sensorname']='bp'
			b['sensortype']='medical'
			sensors.append(b)
		return po,bp,sensors
	else:
		print "Sensor not connected"
		return po,bp,sensors



#sensors=[{sensorid:1,sensorname:nonin,sensortype:medical}, {sensorid:2,sensorname:bp,sensortype:medical}]
def getmac(iface):
	mac=commands.getoutput("ifconfig "+iface+" | grep HWaddr | awk '{ print $5 }'")
	if len(mac)==17:
		return mac


# Create a TCP/IP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

#Function to get the IP Address
def get_ip_address(ifname,sock):
	#s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
	return socket.inet_ntoa(fcntl.ioctl(sock.fileno(),0x8915,struct.pack('256s', ifname[:15]))[20:24])

#TODO get ip from program
if platform.system()=='Windows':
	ip = socket.gethostbyname(socket.gethostname())
else:
	ip = get_ip_address('wlan0',sock)
	mac= getmac('wlan0')

# Bind the socket to the port
server_address = (ip, 10000)
sock.bind(server_address)

print >>sys.stderr, 'starting up on %s port %s' % server_address

def gettime():
	t = time.strftime("%x") + time.strftime("%X")
	return t

while True:
	po,bp,sensors=sensorconnected()
        print >>sys.stderr, '\nwaiting to receive message'
        data, address = sock.recvfrom(4096)
        
        print >>sys.stderr, 'received %s bytes from %s' % (len(data), address)
        print >>sys.stderr, data
        
        if data:
                recv_sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

                #TODO find if the packet is a discover packet or request packet
                #TODO pick self ip addresses automatically
                #TODO pick remote ip addresses from json
                #TODO extract port from message
                #TODO populate sensors from a settings file
		
                if "discover" in data :
                        t = gettime()
                        server_address = (address[0], 10001)
			message = """{ type: "discover_reply", name:"rpi_board" , sdr:"""+str(ip)+"""  , rcv:"""+str(address[0])+""" , mac:"""+str(mac)+""", time:"""+t+""" , argv:"""+sensors+"""}"""
                        try:
                    # Send data
                                print >>sys.stderr, 'sending "%s"' % message
                                sent = recv_sock.sendto(message, server_address)
			finally:
                                print >>sys.stderr, 'closing socket'
                                recv_sock.close()
                elif "request" in data:
			t = gettime()
			readings=[]
			if "nonin" in data:
				if po=='':
					readings=[[-1,-1]]
				else:
					readings=pulse.readsensor(1,po)
					sensorname='nonin'
			elif "bp" in data:
				if bp=='':
					readings=[[-1,-1]]
				else:
					readings=bp.bpsensorread(bp)
					sensorname='bp'
			else:
				readings=[[-1,-1]]
                        #readings=pulseoxy.readsensor(1)
                        #readings = [[78,35]]
			server_address = (address[0], 10002)
                        message = """{type:request_reply, sdr:"""+str(ip)+"""  , rcv:"""+str(address[0])+""", time:"""+t+""" , argv: [ {"sensorid":"""+str(sensorname)+""", "readings":""" + str(readings)+ """ }] }"""

                        try:
                                # Send data
                                print >>sys.stderr, 'sending "%s to %s "' % (message , server_address)
                                sent = recv_sock.sendto(message, server_address)
		                        
                        finally:
                                print >>sys.stderr, 'closing socket'
                                recv_sock.close()
                else:
                        print "faulty packet"
