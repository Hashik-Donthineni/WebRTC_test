### Refactored [AppRTC](https://github.com/njovy/AppRTCDemo) complete app 
### Tutorials
  * How to render camera with WebRTC SDK
  * How to use PeerConnection to connect clients and stream video using MediaStream
  * How to use PeerConnection to connect clients and send text messages and images using DataChannel
  * How to use PeerConnection to connect clients using signalling server and send video


### SDP Offer Structure:

```
v=0
o=- 8188095726609322358 2 IN IP4 127.0.0.1
s=-
t=0 0
a=group:BUNDLE data
a=msid-semantic: WMS
m=application 9 DTLS/SCTP 5000
c=IN IP4 0.0.0.0
a=ice-ufrag:ET30
a=ice-pwd:IJpE4j/YWMeRc1Ggub96loCW
a=ice-options:renomination
a=fingerprint:sha-256 29:40:AD:49:77:4B:71:9D:8E:B0:D6:D2:04:60:87:F6:9C:33:B9:E2:29:EA:0A:51:A7:8F:4D:CE:FD:C5:22:B9
a=setup:actpass
a=mid:data
a=sctpmap:5000 webrtc-datachannel 1024
```
