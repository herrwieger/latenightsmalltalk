Object subclass: #Socket   
   instanceVariableNames: 'socket'
   classVariableNames: ''
   poolDictionaries: '' 
   category: ''! 
Object subclass: #TCP
   instanceVariableNames: 'address port socket'
   classVariableNames: ''
   poolDictionaries: ''
   category: ''!


!Socket class methodsFor: '' !

	new
		| me |
		"Answer a new Socket"
		me := self basicNew.
		^ me
	!
!


!Socket methodsFor: '' !

	accept: ipAddress port: aPort
		^ <149 8 ipAddress socket aPort>
	!
	bind: ipAddress port: aPort
		^ <149 3 ipAddress socket aPort>
	!
	close
		<149 2 socket>.
		self socket: nil
	!
	connectIp: ipAddress port: aPort
		^ <149 4 socket ipAddress aPort>
	!
	create
		socket := <149 1>
	!
	getsockoptLevel: aLevel optname: aName optval: aVal optlen: aLen
		^ <149 10 socket alevel aName aVal aLen>
	!
	listenLog: backlog
		^ <149 7 socket backlog>
	!
	recvMsg: aBuffer size: maxLen
		^ <149 6 socket aBuffer maxLen>.
	!
	recvAll
		| buff1 buff2 rc |
		buff2 := ''.
		[ rc := self recvMsg: buff1 size: 1024. rc>0 ] whileTrue: [
			buff2 := buff2, buff1
			].
		^ buff2
	!
	sendMsg: aString
		^ <149 5 socket aString>
	!
	shutdownHow: how
		^ <149 9 socket how>
	!
	socket
		^ socket
	!
	socket: sock
		socket := sock
	!
!


!TCP class methodsFor: '' !

	getDomain: aURL
		| ix baseURL |
		baseURL := aURL.
		(( ix := baseURL indexOf: $:) > 0) ifTrue: [
				baseURL := baseURL copyFrom: (ix+1) to: baseURL size.
			].
		[(baseURL at:1) = $/]  whileTrue: [ baseURL := baseURL copyFrom: 2 ].
		(( ix := baseURL indexOf: $/) > 0) ifTrue: [
				baseURL := baseURL copyFrom: 1 to: (ix-1).
			].
		^ baseURL	
	!
	getHostByName: anAddress
		^ <150 anAddress>
	!
	getHttp: aURL
		" retrieve a web page from the specified URL "
		| html ip sock |
		sock := Socket new create.
		ip := TCP getHostByName: (TCP getDomain: aURL).
		sock bind: '0.0.0.0' port: 0.
		sock connectIp: ip port: 80.
		sock sendMsg: 'GET ' + aURL + ' HTTP/1.0' + Char newline + Char newline.
		html := sock recvAll.
		sock close.
		^ html asString
	!
!


!TCP methodsFor: '' !
	address
		^ address
	!
	address: anAddress
		address := anAddress
	!
	port
		^ port
	!
	port: aPort
		port := aPort
	!
	socket
		^ socket
	!
	socket: aSocket
		socket := aSocket
	!
	telnetConnect: anAddress port: aPort
		self address: anAddress.
		self port: aPort.
		self socket: <147 1 anAddress aPort>.
		^ self
	!
	telnetDisconnect
		<147 2 (self socket) >.
		self address: nil; port: nil; socket: nil.
		^ nil
	!
	telnetRecvline: buffer size: buffSize for: ticks
		^ <147 8 (self socket) ticks buffer buffSize >
	!
	telnetSend: aString size: aSize
		^ <147 9 (self socket) aString aSize >
	!
	telnetWait: aString for: ticks
		^ <147 6 (self socket) aString ticks >
	!
!

