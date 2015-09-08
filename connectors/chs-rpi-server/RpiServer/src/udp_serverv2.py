import socket
import sys
import fcntl
import struct
import platform
import pulse
import time
import commands
import bp
import json
#function that creates a sensor object
def sensorobject(sid,sname,stype):
	ob=dict()
	ob["sensorid"]=sid
	ob["sensorname"]=sname
	ob["sensortype"]=stype
	return ob
#function that determines which sensors are connected in which port(BP & Pulseoxy)
def sensorconnected():
	p={}
	b={}
	sensors=[]
	port1 = '/dev/ttyUSB0'
	port2 = '/dev/ttyUSB1'
	bp=''
	po=''
	#command that lists the connected sensors
	portlist=commands.getoutput("ls /dev/ttyUSB*")
	#case if both ports are occupied by sensors
	if 'USB0' in portlist and 'USB1' in portlist:
		#case for identifying the corresponding port for each of the sensors
		try:
			pup.readsensor(1,port1)
			po=port1
			bp=port2
			p=sensorobject(1,'nonin','medical')
			b=sensorobject(2,'bp','medical')
			sensors.append(p)
			sensors.append(b)
		except ValueError:
			po=port2
			bp=port1
			b=sensorobject(1,'bp','medical')
			p=sensorobject(2,'nonin','medical')
			sensors.append(b)
			sensors.append(p)
		return po,bp,sensors
	#case if only one sensor is connected and identifying which sensor is connected	
	elif 'USB0' in portlist:
		try:
			pup.readsensor(1,port1)
			po=port1
			p=sensorobject(1,'nonin','medical')
			sensors.append(p)
		except ValueError:
			print "only BP sensor connected in PORT1"
			bp=port1
			b=sensorobject(1,'bp','medical')
			sensors.append(b)
		return po,bp,sensors
	elif 'USB1' in portlist:
		try:
			pup.readsensor(1,port2)
			po=port2
			p=sensorobject(1,'nonin','medical')
			sensors.append(p)
		except ValueError:
			print "only BP sensor connected in PORT2"
			bp=port2
			b=sensorobject(1,'bp','medical')
			sensors.append(b)
		return po,bp,sensors
	#case when no sensor is connected(the list of sensors is empty)
	else:
		print "Sensor not connected"
		return po,bp,sensors
#function to get the mac address of the hardware
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
#case to determine the OS platform
if platform.system()=='Windows':
	ip = socket.gethostbyname(socket.gethostname())
else:
	ip = get_ip_address('wlan0',sock)
	mac= getmac('wlan0')
# Bind the socket to the port
server_address = (ip, 10000)
sock.bind(server_address)
print >>sys.stderr, 'starting up on %s port %s' , server_address
#function to get the current time 
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
		message = dict()
                #TODO find if the packet is a discover packet or request packet
                #TODO pick self ip addresses automatically
                #TODO pick remote ip addresses from json
                #TODO extract port from message
                #TODO populate sensors from a settings file
	        if "discover" in data :
                        t = gettime()
                        server_address = (address[0], 10001)
                        #message to be sent created as a dictionary
                        message["type"]="discover_reply"
                        message["name"]="rpi_board"
                        message["sdr"]=ip
                        message["rcv"]=address[0]
                        message["mac"]=mac
                        message["time"]=t
                        message["argv"]=sensors
                        #dictionary converted to json
                        jsonmessage=json.dumps(message)
                        try:
                    	# Send data
                                print >>sys.stderr, 'sending "%s"' , jsonmessage
                                sent = recv_sock.sendto(jsonmessage, server_address)
			finally:
                                print >>sys.stderr, 'closing socket'
                                recv_sock.close()
                elif "request" in data:
                	value=dict()
			t = gettime()
			readings=[]
			#case to get the readings from the sensor
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
			#message to be sent created as a dictionary
			message["type"]="request_reply"
                        message["sdr"]=ip
                        message["rcv"]=address[0]
                        message["time"]=t
                        value["sensorname"]=sensorname
                        value["readings"]=readings
                        message["argv"]=value
                        #dictionary converted to json
                        jsonmessage=json.dumps(message)
                        try:
                                # Send data
                                print >>sys.stderr, 'sending "%s to %s "' , (jsonmessage , server_address)
                                sent = recv_sock.sendto(jsonmessage, server_address)
		                        
                        finally:
                                print >>sys.stderr, 'closing socket'
                                recv_sock.close()
                else:
                        print "faulty packet"
