package me.axon.webrtc.wrapper

import android.content.Context
import me.axon.webrtc.rtcCallback.RtcListener
import org.webrtc.*
import org.webrtc.CameraEnumerationAndroid.getNameOfFrontFacingDevice
import org.webrtc.PeerConnection.*
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.*

public open class WebRTCWrapper(
    private val serverUrl : String,
    val eglContext: EglBase.Context?,
    var receiveMsg: ((String) -> Unit)?
) : SdpObserver, PeerConnection.Observer {

    private lateinit var mPeer: PeerConnection
    private lateinit var mPeerFactory: PeerConnectionFactory
    private lateinit var mLocalMedia: MediaStream
    private lateinit var mVideoSource: VideoSource
    private lateinit var videoTracker: VideoTrack
    private lateinit var audioTracker: AudioTrack
    private lateinit var localDataChannel: DataChannel
    private val mIceServers = LinkedList<IceServer>()
    private val mMediaConstraints = MediaConstraints()
    private lateinit var mListener: RtcListener

    fun init(context: Context,mListener: RtcListener) {

        this.mListener = mListener

        PeerConnectionFactory.initializeAndroidGlobals(context, true, true, true)

        mPeerFactory = PeerConnectionFactory()

        eglContext?.let {
            mPeerFactory.setVideoHwAccelerationOptions(
                eglContext,
                eglContext
            )
            mMediaConstraints.mandatory.add(
                MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true")
            )

            mMediaConstraints.mandatory.add(
                MediaConstraints.KeyValuePair(
                    "OfferToReceiveVideo", "true"
                )
            )

            createLocalMedia()

        }

        setStunServer()

        mMediaConstraints.optional.add(
            MediaConstraints.KeyValuePair(
                "DtlsSrtpKeyAgreement", "true"
            )
        )

        createPeerConnection()
    }

    private fun createChannel() {
        try {
            localDataChannel = mPeer.createDataChannel("dataChannel", DataChannel.Init())
            localDataChannel.registerObserver(dataChannelObserver)
        } catch (e: Exception) {
//            Timber.e(e.message)
        }
    }

    //region chat

    private fun readIncomingMessage(buffer: ByteBuffer) {
        val bytes: ByteArray
        if (buffer.hasArray()) {
            bytes = buffer.array()
        } else {
            bytes = ByteArray(buffer.remaining())
            buffer[bytes]
        }
        val firstMessage = String(bytes, Charset.defaultCharset())
        receiveMsg!!(firstMessage)
    }

    fun sendMessage(message: String): Boolean {
        if (message.isEmpty()) {
            return false
        }
        val data = stringToByteBuffer(message, Charset.defaultCharset())
        val status = localDataChannel.send(DataChannel.Buffer(data, false))
        return status
    }

    private fun stringToByteBuffer(msg: String, charset: Charset): ByteBuffer? {
        return ByteBuffer.wrap(msg.toByteArray(charset))
    }

    private var dataChannelObserver = object : DataChannel.Observer {

        override fun onBufferedAmountChange(p0: Long) {}

        override fun onStateChange() {
            if (localDataChannel.state() === DataChannel.State.OPEN) {

            } else {

            }
        }

        override fun onMessage(buffer: DataChannel.Buffer) {
            readIncomingMessage(buffer = buffer.data)
        }

    }

    //endregion

    fun createOffer() {
        createChannel()
        mPeer.createOffer(this, mMediaConstraints)
    }

    fun createAnswer() {
        mPeer.createAnswer(this, mMediaConstraints)
    }

    fun setRemoteSdp(type: String?, sdp: String?) {
        val sessionDescription =
            SessionDescription(SessionDescription.Type.fromCanonicalForm(type), sdp)
        mPeer.setRemoteDescription(this, sessionDescription)
    }

    fun setCandidate(label: Int, mid: String?, candidate: String?) {
        if (mPeer.remoteDescription != null) {
            val iceCandidate = IceCandidate(mid, label, candidate)
            mPeer.addIceCandidate(iceCandidate)
        } else {
//            Timber.d("WebRTCWrapper remote sdp is null when set candidate")
        }
    }

    fun exitSession() {
        if (::mPeer.isInitialized) {
            mPeer.close()
            mPeer.dispose()
        }
        if (::mVideoSource.isInitialized) {
            mVideoSource.dispose()
        }
        mPeerFactory.dispose()
    }

    private fun createPeerConnection() {
        mPeer = mPeerFactory.createPeerConnection(mIceServers, mMediaConstraints, this)
        if (::mLocalMedia.isInitialized)
            mPeer.addStream(mLocalMedia)
    }

    private fun createLocalMedia() {
        mLocalMedia = mPeerFactory.createLocalMediaStream("ARDAMS")
        val videoConstraints = MediaConstraints()
        videoConstraints.mandatory.add(
            MediaConstraints.KeyValuePair("maxHeight", 1280.toString())
        )
        videoConstraints.mandatory.add(
            MediaConstraints.KeyValuePair("maxWidth", 720.toString())
        )
        videoConstraints.mandatory.add(
            MediaConstraints.KeyValuePair("maxFrameRate", 25.toString())
        )
        videoConstraints.mandatory.add(
            MediaConstraints.KeyValuePair("minFrameRate", 25.toString())
        )

        val frontCameraDeviceName = getNameOfFrontFacingDevice()
        val videoCapture = VideoCapturerAndroid.create(frontCameraDeviceName)

        mVideoSource = mPeerFactory.createVideoSource(videoCapture, videoConstraints)
        videoTracker = mPeerFactory.createVideoTrack("ARDAMSv0", mVideoSource)
        mLocalMedia.addTrack(videoTracker)

        val audioSource = mPeerFactory.createAudioSource(MediaConstraints())
        audioTracker = mPeerFactory.createAudioTrack("ARDAMSa0", audioSource)
        mLocalMedia.addTrack(audioTracker)
        mListener.onLocalStream(mLocalMedia)

    }

    override fun onCreateSuccess(sessionDescription: SessionDescription) {
        mPeer.setLocalDescription(this, sessionDescription)
        mListener.onCreateOfferOrAnswer(
            sessionDescription.type.canonicalForm(),
            sessionDescription.description
        )
    }

    override fun onIceCandidate(iceCandidate: IceCandidate) {
        mListener.onIceCandidate(iceCandidate.sdpMLineIndex, iceCandidate.sdpMid, iceCandidate.sdp)
    }

    override fun onAddStream(mediaStream: MediaStream) {
        mListener.onAddRemoteStream(mediaStream)
    }

    override fun onRemoveStream(mediaStream: MediaStream) {
        mListener.onRemoveRemoteStream(mediaStream)
    }

    override fun onSetSuccess() {}

    override fun onCreateFailure(s: String) {}

    override fun onSetFailure(s: String) {}

    override fun onSignalingChange(signalingState: SignalingState) {}

    override fun onIceConnectionChange(iceConnectionState: IceConnectionState) {}

    override fun onIceConnectionReceivingChange(p0: Boolean) {}

    override fun onIceGatheringChange(iceGatheringState: IceGatheringState) {}

    override fun onDataChannel(dataChannel: DataChannel) {
        localDataChannel = dataChannel
        dataChannel.registerObserver(dataChannelObserver)
    }

    override fun onRenegotiationNeeded() {}

    //region setCameraTools
    fun enableVideo() {
        videoTracker.setEnabled(!videoTracker.enabled())
    }

    fun enableAudio() {
        audioTracker.setEnabled(!audioTracker.enabled())
    }
    //endregion

    private fun setStunServer() {
        mIceServers.add(IceServer("stun:${serverUrl}:3478"))
        mIceServers.add(IceServer("stun:${serverUrl}:49160"))
        mIceServers.add(IceServer("stun:${serverUrl}:49161"))
        mIceServers.add(IceServer("stun:${serverUrl}:49162"))
        mIceServers.add(IceServer("stun:${serverUrl}:49163"))
        mIceServers.add(IceServer("stun:${serverUrl}:49164"))
        mIceServers.add(IceServer("stun:${serverUrl}:49165"))
        mIceServers.add(IceServer("stun:${serverUrl}:49166"))
        mIceServers.add(IceServer("stun:${serverUrl}:49167"))
        mIceServers.add(IceServer("stun:${serverUrl}:49168"))
        mIceServers.add(IceServer("stun:${serverUrl}:49169"))
        mIceServers.add(IceServer("stun:${serverUrl}:49170"))
        mIceServers.add(IceServer("stun:${serverUrl}:49171"))
        mIceServers.add(IceServer("stun:${serverUrl}:49172"))
        mIceServers.add(IceServer("stun:${serverUrl}:49173"))
        mIceServers.add(IceServer("stun:${serverUrl}:49174"))
        mIceServers.add(IceServer("stun:${serverUrl}:49175"))
        mIceServers.add(IceServer("stun:${serverUrl}:49176"))
        mIceServers.add(IceServer("stun:${serverUrl}:49177"))
        mIceServers.add(IceServer("stun:${serverUrl}:49178"))
        mIceServers.add(IceServer("stun:${serverUrl}:49179"))
        mIceServers.add(IceServer("stun:${serverUrl}:49180"))
        mIceServers.add(IceServer("stun:${serverUrl}:49181"))
        mIceServers.add(IceServer("stun:${serverUrl}:49182"))
        mIceServers.add(IceServer("stun:${serverUrl}:49183"))
        mIceServers.add(IceServer("stun:${serverUrl}:49184"))
        mIceServers.add(IceServer("stun:${serverUrl}:49185"))
        mIceServers.add(IceServer("stun:${serverUrl}:49186"))
        mIceServers.add(IceServer("stun:${serverUrl}:49187"))
        mIceServers.add(IceServer("stun:${serverUrl}:49188"))
        mIceServers.add(IceServer("stun:${serverUrl}:49189"))
        mIceServers.add(IceServer("stun:${serverUrl}:49190"))
        mIceServers.add(IceServer("stun:${serverUrl}:49191"))
        mIceServers.add(IceServer("stun:${serverUrl}:49192"))
        mIceServers.add(IceServer("stun:${serverUrl}:49193"))
        mIceServers.add(IceServer("stun:${serverUrl}:49194"))
        mIceServers.add(IceServer("stun:${serverUrl}:49195"))
        mIceServers.add(IceServer("stun:${serverUrl}:49196"))
        mIceServers.add(IceServer("stun:${serverUrl}:49197"))
        mIceServers.add(IceServer("stun:${serverUrl}:49198"))
        mIceServers.add(IceServer("stun:${serverUrl}:49199"))
        mIceServers.add(IceServer("stun:${serverUrl}:49200"))
    }

}