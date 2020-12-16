package io.agora.openduo.activities

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import io.agora.openduo.Constants
import io.agora.openduo.R
import io.agora.rtm.LocalInvitation
import io.agora.rtm.RemoteInvitation

class CallActivity : BaseCallActivity(), View.OnClickListener {
    private var mRole = 0
    private var mPeer: String? = null
    private var mChannel: String? = null
    private var mAcceptBtn: AppCompatImageView? = null
    private var mHangupBtn: AppCompatImageView? = null
    private var mPlayer: MediaPlayer? = null
    private var mAnimator: PortraitAnimator? = null
    private var mInvitationSending = false
    private var mInvitationReceiving = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        initUI()
        if (isCaller) {
            sendInvitation()
            mInvitationSending = true
        } else if (isCallee) {
            mInvitationReceiving = true
        }
        startRinging()
    }

    private fun initUI() {
        val portrait = findViewById<ImageView>(R.id.peer_image)
        portrait.setImageResource(R.drawable.portrait)
        val mPeerUidText = arrayOfNulls<AppCompatTextView>(PEER_ID_RES.size)
        for (i in mPeerUidText.indices) {
            mPeerUidText[i] = findViewById(PEER_ID_RES[i])
        }
        val intent = intent
        mChannel = intent.getStringExtra(Constants.KEY_CALLING_CHANNEL)
        mPeer = intent.getStringExtra(Constants.KEY_CALLING_PEER)
        if (mPeer != null) {
            try {
                var peer = Integer.valueOf(mPeer!!)
                for (i in mPeerUidText.indices.reversed()) {
                    val digit = peer % 10
                    peer /= 10
                    mPeerUidText[i]!!.text = digit.toString()
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
        }
        mHangupBtn = findViewById(R.id.hang_up_btn)
        mHangupBtn?.setVisibility(View.VISIBLE)
        mHangupBtn?.setOnClickListener(this)
        val roleText = findViewById<TextView>(R.id.call_role)
        mAcceptBtn = findViewById(R.id.accept_call_btn)
        mRole = intent.getIntExtra(Constants.KEY_CALLING_ROLE, Constants.ROLE_CALLEE)
        if (isCallee) {
            roleText.setText(R.string.receiving_call)
            mAcceptBtn?.setVisibility(View.VISIBLE)
            mAcceptBtn?.setOnClickListener(this)
        } else if (isCaller) {
            roleText.setText(R.string.calling_out)
            mAcceptBtn?.setVisibility(View.GONE)
        }
        mAnimator = PortraitAnimator(
                findViewById(R.id.anim_layer_1),
                findViewById(R.id.anim_layer_2),
                findViewById(R.id.anim_layer_3))
    }

    private val isCaller: Boolean
        private get() = mRole == Constants.ROLE_CALLER

    private val isCallee: Boolean
        private get() = mRole == Constants.ROLE_CALLEE

    override fun onGlobalLayoutCompleted() {
        var params: RelativeLayout.LayoutParams
        if (isCallee) {
            val actionLayout = findViewById<RelativeLayout>(R.id.action_button_layout)
            params = actionLayout.layoutParams as RelativeLayout.LayoutParams
            val buttonMargin = displayMetrics.widthPixels / 6
            params.leftMargin = buttonMargin
            params.rightMargin = buttonMargin
            actionLayout.layoutParams = params
            params = mAcceptBtn!!.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
            mAcceptBtn!!.layoutParams = params
            params = mHangupBtn!!.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE)
            mHangupBtn!!.layoutParams = params
        } else if (isCaller) {
            params = mHangupBtn!!.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
            mHangupBtn!!.layoutParams = params
        }
        val content = findViewById<RelativeLayout>(R.id.content_layout)
        params = content.layoutParams as RelativeLayout.LayoutParams
        val marginTop = (displayMetrics.heightPixels -
                statusBarHeight - content.height) / 2
        params.topMargin = marginTop + statusBarHeight
        content.layoutParams = params
    }

    private fun sendInvitation() {
        inviteCall(mPeer, mChannel)
    }

    private fun startRinging() {
        if (isCallee) {
            mPlayer = playCalleeRing()
        } else if (isCaller) {
            mPlayer = playCallerRing()
        }
    }

    private fun playCallerRing(): MediaPlayer {
        return startRinging(R.raw.basic_ring)
    }

    private fun playCalleeRing(): MediaPlayer {
        return startRinging(R.raw.basic_tones)
    }

    private fun startRinging(resource: Int): MediaPlayer {
        val player = MediaPlayer.create(this, resource)
        player.isLooping = true
        player.start()
        return player
    }

    private fun stopRinging() {
        if (mPlayer != null && mPlayer!!.isPlaying) {
            mPlayer!!.stop()
            mPlayer!!.release()
            mPlayer = null
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.accept_call_btn -> answerCall(global()?.remoteInvitation)
            R.id.hang_up_btn -> if (isCaller) {
                cancelLocalInvitation()
            } else if (isCallee) {
                refuseRemoteInvitation(global().remoteInvitation!!)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mAnimator!!.start()
    }

    override fun onStop() {
        super.onStop()
        mAnimator!!.stop()
    }

    override fun onLocalInvitationReceived(localInvitation: LocalInvitation?) {
        super.onLocalInvitationReceived(localInvitation)
    }

    override fun onLocalInvitationAccepted(localInvitation: LocalInvitation?, response: String?) {
        super.onLocalInvitationAccepted(localInvitation, response)
        mInvitationSending = false
        stopRinging()
    }

    override fun onLocalInvitationCanceled(localInvitation: LocalInvitation?) {
        Log.i(TAG, "onLocalInvitationCanceled:" + localInvitation?.content)
        mInvitationSending = false
        finish()
    }

    override fun onLocalInvitationRefused(localInvitation: LocalInvitation?, response: String?) {
        Log.i(TAG, "onLocalInvitationRefused:$response")
        mInvitationSending = false
        finish()
    }

    override fun onLocalInvitationFailure(localInvitation: LocalInvitation?, errorCode: Int) {
        super.onLocalInvitationFailure(localInvitation, errorCode)
        mInvitationSending = false
        stopRinging()
    }

    override fun onRemoteInvitationReceived(remoteInvitation: RemoteInvitation?) {
        // Do not respond to any other calls
        Log.i(TAG, "Ignore remote invitation from " +
                remoteInvitation?.callerId + " while in calling")
    }

    override fun onRemoteInvitationAccepted(remoteInvitation: RemoteInvitation?) {
        super.onRemoteInvitationAccepted(remoteInvitation)
        mInvitationReceiving = false
        stopRinging()
    }

    override fun onRemoteInvitationRefused(remoteInvitation: RemoteInvitation?) {
        Log.i(TAG, "onRemoteInvitationRefused:" + remoteInvitation?.content)
        mInvitationReceiving = false
        finish()
    }

    override fun onRemoteInvitationCanceled(remoteInvitation: RemoteInvitation?) {
        Log.i(TAG, "onRemoteInvitationCanceled:" + remoteInvitation?.content)
        mInvitationReceiving = false
        finish()
    }

    override fun onRemoteInvitationFailure(remoteInvitation: RemoteInvitation?, errorCode: Int) {
        super.onRemoteInvitationFailure(remoteInvitation, errorCode)
        mInvitationReceiving = false
        stopRinging()
    }

    override fun finish() {
        stopRinging()
        if (isCallee && mInvitationReceiving && global().remoteInvitation != null) {
            refuseRemoteInvitation(global().remoteInvitation!!)
        } else if (isCaller && mInvitationSending && global().localInvitation != null) {
            cancelLocalInvitation()
        }
        super.finish()
    }

    private inner class PortraitAnimator internal constructor(private val mLayer1: View, private val mLayer2: View, private val mLayer3: View) {
        private val mAnim1: Animation
        private val mAnim2: Animation
        private val mAnim3: Animation
        private var mIsRunning = false
        private fun buildAnimation(startOffset: Int): AnimationSet {
            val set = AnimationSet(true)
            val alphaAnimation = AlphaAnimation(1.0f, 0.0f)
            alphaAnimation.duration = ANIM_DURATION.toLong()
            alphaAnimation.startOffset = startOffset.toLong()
            alphaAnimation.repeatCount = Animation.INFINITE
            alphaAnimation.repeatMode = Animation.RESTART
            alphaAnimation.fillAfter = true
            val scaleAnimation = ScaleAnimation(
                    1.0f, 1.3f, 1.0f, 1.3f,
                    ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                    ScaleAnimation.RELATIVE_TO_SELF, 0.5f)
            scaleAnimation.duration = ANIM_DURATION.toLong()
            scaleAnimation.startOffset = startOffset.toLong()
            scaleAnimation.repeatCount = Animation.INFINITE
            scaleAnimation.repeatMode = Animation.RESTART
            scaleAnimation.fillAfter = true
            set.addAnimation(alphaAnimation)
            set.addAnimation(scaleAnimation)
            return set
        }

        fun start() {
            if (!mIsRunning) {
                mIsRunning = true
                mLayer1.visibility = View.VISIBLE
                mLayer2.visibility = View.VISIBLE
                mLayer3.visibility = View.VISIBLE
                mLayer1.startAnimation(mAnim1)
                mLayer2.startAnimation(mAnim2)
                mLayer3.startAnimation(mAnim3)
            }
        }

        fun stop() {
            mLayer1.clearAnimation()
            mLayer2.clearAnimation()
            mLayer3.clearAnimation()
            mLayer1.visibility = View.GONE
            mLayer2.visibility = View.GONE
            mLayer3.visibility = View.GONE
        }


        val ANIM_DURATION = 3000

        init {
            mAnim1 = buildAnimation(0)
            mAnim2 = buildAnimation(1000)
            mAnim3 = buildAnimation(2000)
        }
    }

    companion object {
        private val TAG = CallActivity::class.java.simpleName
        private val PEER_ID_RES = intArrayOf(
                R.id.peer_id_digit_1,
                R.id.peer_id_digit_2,
                R.id.peer_id_digit_3,
                R.id.peer_id_digit_4
        )
    }
}