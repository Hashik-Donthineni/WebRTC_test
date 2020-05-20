package com.hd.androidwebrtc.rtc_test;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.myhexaville.androidwebrtc.R;
import com.myhexaville.androidwebrtc.databinding.ActivitySampleDataChannelBinding;

import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

/*
 * Shows how to use PeerConnection to connect clients and send text messages and images using DataChannel
 * without any networking
 * */
public class DataChannelActivity extends AppCompatActivity {
    public static final int CHUNK_SIZE = 64000;
    private static final String TAG = "SampleDataChannelAct";
    int incomingFileSize;
    byte[] imageFileBytes;
    boolean receivingFile;
    private ActivitySampleDataChannelBinding binding;
    private PeerConnectionFactory factory;
    private PeerConnection localPeerConnection, remotePeerConnection;
    private PeerConnection mainPeerConnectoin;
    private DataChannel mainDataChannel;
    private DataChannel localDataChannel;

    private static ByteBuffer stringToByteBuffer(String msg, Charset charset) {
        return ByteBuffer.wrap(msg.getBytes(charset));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onCreate(Bundle savedInstanceState) { //Starts here
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sample_data_channel);

        initializePeerConnectionFactory(); //Android Specific, you can Ignore.
        initializeMyPeerConnection(); // Connection Initialization.
        startConnection();
    }

    private void startConnection() {
        Log.d(TAG, "startConnection: Starting Connection...");
        mainPeerConnectoin.createOffer(new SimpleSdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.d(TAG, "onCreateSuccess: " + sessionDescription.description);
                mainPeerConnectoin.setLocalDescription(new SimpleSdpObserver(), sessionDescription);
            }

            @Override
            public void onCreateFailure(String s) {
                Log.e(TAG, "onCreateFailure: FAILED:" + s);
            }
        }, new MediaConstraints());
    }


    private void initializePeerConnectionFactory() {
        PeerConnectionFactory.initializeAndroidGlobals(this, true, true, true);
        factory = new PeerConnectionFactory(null);
    }

    private void initializeMyPeerConnection() {
        Log.d(TAG, "initializeMyPeerConnection: Starting Initialization...");
        mainPeerConnectoin = createPeerConnection(factory, true);

        mainDataChannel = mainPeerConnectoin.createDataChannel("sendDataChannel", new DataChannel.Init()); //Setting the data channel.
        mainDataChannel.registerObserver(new DataChannel.Observer() {
            @Override
            public void onBufferedAmountChange(long l) {

            }

            @Override
            public void onStateChange() {
                //Data channel state change
                Log.d(TAG, "onStateChange: " + localDataChannel.state().toString());
                runOnUiThread(() -> {
                    if (localDataChannel.state() == DataChannel.State.OPEN) {
                        binding.sendButton.setEnabled(true);
                    } else {
                        binding.sendButton.setEnabled(false);
                    }
                });
            }

            @Override
            public void onMessage(DataChannel.Buffer buffer) {
                Toast.makeText(DataChannelActivity.this, "Got the message!", Toast.LENGTH_SHORT).show();
            }
        });
        Log.d(TAG, "initializeMyPeerConnection: Finished Initializing.");
    }

    private PeerConnection createPeerConnection(PeerConnectionFactory factory, boolean isLocal) {
        List<PeerConnection.IceServer> iceServers = new LinkedList<>();
        iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302")); //Setting our custom STUN server.

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
        MediaConstraints pcConstraints = new MediaConstraints();

        PeerConnection.Observer pcObserver = new PeerConnection.Observer() {
            @Override
            public void onSignalingChange(PeerConnection.SignalingState signalingState) {
                Log.d(TAG, "onSignalingChange: ");
            }

            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
                Log.d(TAG, "onIceConnectionChange: ");
            }

            @Override
            public void onIceConnectionReceivingChange(boolean b) {
//                Log.d(TAG, "onIceConnectionReceivingChange: ");
            }

            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
                Log.d(TAG, "onIceGatheringChange: " + iceGatheringState);
                if (iceGatheringState.compareTo(PeerConnection.IceGatheringState.COMPLETE) == 0) {
                    //If ICE gathering is completed, we are logging the SDP. At this point ICE trickling is completed and SDP is ready.
                    Log.d(TAG, "onIceGatheringChange: " + mainPeerConnectoin.getLocalDescription().description);
                    runOnUiThread(() -> {
                        Toast.makeText(DataChannelActivity.this, "ICE Gathering Finished", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                //Fired after each ICE candidate is gathered. AKA ICE trickling.
                Log.d(TAG, "onIceCandidate: SDP:" + iceCandidate.sdp);
                mainPeerConnectoin.addIceCandidate(iceCandidate);
            }

            @Override
            public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
                Log.d(TAG, "onIceCandidatesRemoved: ");
            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                Log.d(TAG, "onAddStream: ");
            }

            @Override
            public void onRemoveStream(MediaStream mediaStream) {
                Log.d(TAG, "onRemoveStream: ");
            }

            @Override
            public void onDataChannel(DataChannel dataChannel) {
                Log.d(TAG, "onDataChannel: State: " + dataChannel.state() + "Registering Observer");
                dataChannel.registerObserver(new DataChannel.Observer() {
                    @Override
                    public void onBufferedAmountChange(long l) {

                    }

                    @Override
                    public void onStateChange() {
                        Log.d(TAG, "onStateChange: remote data channel state: " + dataChannel.state().toString());
                    }

                    @Override
                    public void onMessage(DataChannel.Buffer buffer) {
                        Log.d(TAG, "onMessage: got message");
                        readIncomingMessage(buffer.data);
                    }
                });
            }

            @Override
            public void onRenegotiationNeeded() {
                Log.d(TAG, "onRenegotiationNeeded: ");
            }
        };

        return factory.createPeerConnection(rtcConfig, pcConstraints, pcObserver);
    }

    public void sendMessage(View view) {
        String message = binding.textInput.getText().toString();
        if (message.isEmpty()) {
            return;
        }

        binding.textInput.setText("");

        ByteBuffer data = stringToByteBuffer("-s" + message, Charset.defaultCharset());
        localDataChannel.send(new DataChannel.Buffer(data, false));
    }

    private void readIncomingMessage(ByteBuffer buffer) {
        byte[] bytes;
        if (buffer.hasArray()) {
            bytes = buffer.array();
        } else {
            bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
        }
        if (!receivingFile) {
            String firstMessage = new String(bytes, Charset.defaultCharset());
            String type = firstMessage.substring(0, 2);

            if (type.equals("-i")) {
                incomingFileSize = Integer.parseInt(firstMessage.substring(2));
                imageFileBytes = new byte[incomingFileSize];
                Log.d(TAG, "readIncomingMessage: incoming file size " + incomingFileSize);
                receivingFile = true;
            } else if (type.equals("-s")) {
                runOnUiThread(() -> binding.remoteText.setText(firstMessage.substring(2)));
            }
        }
    }

    private void connectToOtherPeer() {
        MediaConstraints sdpMediaConstraints = new MediaConstraints();

        localPeerConnection.createOffer(new SimpleSdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.d(TAG, "onCreateSuccess: ");
                localPeerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);
                remotePeerConnection.setRemoteDescription(new SimpleSdpObserver(), sessionDescription);

                remotePeerConnection.createAnswer(new SimpleSdpObserver() {
                    @Override
                    public void onCreateSuccess(SessionDescription sessionDescription) {
                        localPeerConnection.setRemoteDescription(new SimpleSdpObserver(), sessionDescription);
                        remotePeerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);
                    }
                }, sdpMediaConstraints);
            }
        }, sdpMediaConstraints);
        Log.d(TAG, "connectToOtherPeer: " + localPeerConnection.getLocalDescription().description);
    }

    private void initializePeerConnections() {
        localPeerConnection = createPeerConnection(factory, true);
        remotePeerConnection = createPeerConnection(factory, false);

        localDataChannel = localPeerConnection.createDataChannel("sendDataChannel", new DataChannel.Init());
        localDataChannel.registerObserver(new DataChannel.Observer() {
            @Override
            public void onBufferedAmountChange(long l) {

            }

            @Override
            public void onStateChange() {
                Log.d(TAG, "onStateChange: " + localDataChannel.state().toString());
                runOnUiThread(() -> {
                    if (localDataChannel.state() == DataChannel.State.OPEN) {
                        binding.sendButton.setEnabled(true);
                    } else {
                        binding.sendButton.setEnabled(false);
                    }
                });
            }

            @Override
            public void onMessage(DataChannel.Buffer buffer) {
                Toast.makeText(DataChannelActivity.this, "Got the message!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}