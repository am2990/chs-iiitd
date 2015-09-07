import serial,time

def processframe(frame):
	if int(frame[1]) < 2:# and int(frame[2]) < 10 and int(frame[3]) < 10:
		sys = (int(frame[1])*100)+(int(frame[2])*10)+(int(frame[3]))
	else:
		sys = 0
	if int(frame[7]):# < 2:# and int(frame[7] < 10 and int(frame[8]) < 10:
		dia = (int(frame[6])*100)+(int(frame[7])*10)+(int(frame[8]))
	else:
		dia = 0
	if int(frame[12]):# < 2:# and int(frame[12]) < 10 and int(frame[13]) < 10:
		hr = (int(frame[11])*100)+(int(frame[12])*10)+(int(frame[13]))
	else:
		hr = 0
	return sys,dia,hr

def bpsensorread(port):
	sensor = serial.Serial(port,baudrate=9600)
	frame=[]
	reading=[]
	fc=15
	while fc > 0:
		fc = fc - 1
		data = sensor.read()
		frame.append(data)
	sys,dia,hr=processframe(frame)
	reading.append([sys,dia,hr])
	return reading
#print bpsensorread()
