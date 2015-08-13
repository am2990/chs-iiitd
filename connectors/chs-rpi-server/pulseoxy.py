import serial,time,sys

#returns non zero value if the bit is set
def testbit(value,bit):
	return (value & 1 << bit)

#converts raw string to hexadecimal
def stringAsHex(s):
        return ":".join("{:02x}".format(ord(c)) for c in s)

#extract the information from a packet
def processpacket(p):
	msb=p[19][3]
	lsb=p[20][3]
	hr= ((msb<<7)|(lsb)) & 0x1ff
	emsb=p[21][3]
        elsb=p[22][3]
        ehr= ((emsb<<7)|(elsb)) & 0x1ff
        oxy=p[8][3]
        ol= oxy & 0x7f
        eoxy=p[16][3]
        eol= eoxy & 0x7f
	#if hr!= 511:
		#print "Pulse Rate:", hr
	        #print "Extended Pulse Rate:",ehr
		#print "Oxygen Level:", ol
		#print "Extended Oxygen Level:",eol
	#else:
		#print "Place your finger"
	return hr,ol

def readsensor(n):
	sd=[]
	val=[]
        sensor = serial.Serial('/dev/ttyUSB0',timeout=1,baudrate=9600)
        frame=[]
        fc=0
        packet=[]
        while True:
                data = sensor.read()
                d=stringAsHex(data)
                intdata=int(d,16)
                #print intdata 
                if intdata == 1:
                        data1 = sensor.read()
                        d1=stringAsHex(data1)
                        intdata1=int(d1,16)
                        #print intdata1
                        tb = testbit(intdata1,0)
                        #print "test bit:", tb
                        if tb > 0 :
                                frame.append(intdata)
                                frame.append(intdata1)
                                fc = 2
                                while ( fc <= 125 ):
                                        data = sensor.read()
                                	d=stringAsHex(data)
                                        intdata=int(d,16) 
                        		frame.append(intdata)
                                	fc = fc + 1
                                        if fc%5 == 0:
                                                #print frame
                                                if frame[0] == 1:
                                                        packet.append(frame)
                                                        frame=[]
                                                else:
                                                        break
                                        if fc == 126:
                                                n = n - 1
                                                if n >= 0:
                                                        h,o= processpacket(packet)
							sd.append([h,o])
							#print sd
                                                        packet=[]
                                                        #print "***********Packet received***********"
                                                        fc = 1
                                                else:
                                                       	return sd



#readsensor(15)
				  
	
