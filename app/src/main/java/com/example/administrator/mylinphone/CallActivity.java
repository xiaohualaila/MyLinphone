package com.example.administrator.mylinphone;
/*
CallActivity.java
Copyright (C) 2015  Belledonne Communications, Grenoble, Fran
*/


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.TextView;
import com.example.administrator.mylinphone.util.LinphoneManager;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreListenerBase;

/**
 * @author Sylvain Berfini
 */
public class CallActivity extends Activity implements View.OnClickListener {

	private boolean isVideoCallPaused = false;
	private LinphoneCoreListenerBase mListener;
	private CallVideoFragment videoCallFragment;

	private TextView hangUp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call);
		hangUp = findViewById(R.id.hangUp);
		hangUp.setOnClickListener(this);

		mListener = new LinphoneCoreListenerBase() {
			@Override
			public void messageReceived(LinphoneCore lc, LinphoneChatRoom cr, LinphoneChatMessage message) {
			}
			
			@Override
			public void callState(LinphoneCore lc, final LinphoneCall call, LinphoneCall.State state, String message) {
				if (LinphoneManager.getLc().getCallsNb() == 0) {//挂断电话
					finish();
					return;
				}
			}

			@Override
			public void callEncryptionChanged(LinphoneCore lc, final LinphoneCall call, boolean encrypted, String authenticationToken) {

			}

		};
	     	disableVideo();

	        Fragment callFragment = new CallVideoFragment();
			LinphoneManager.getInstance().routeAudioToSpeaker();
			callFragment.setArguments(getIntent().getExtras());
			getFragmentManager().beginTransaction().add(R.id.fragmentContainer, callFragment).commitAllowingStateLoss();

	}

	public void bindVideoFragment(CallVideoFragment fragment) {
		videoCallFragment = fragment;
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("Speaker", LinphoneManager.getLc().isSpeakerEnabled());
		outState.putBoolean("Mic", LinphoneManager.getLc().isMicMuted());
		outState.putBoolean("VideoCallPaused", isVideoCallPaused);

		super.onSaveInstanceState(outState);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
       switch (id){
		   case R.id.hangUp:
			   hangUp();
		   	break;
	   }

	}

    //挂断
	private void hangUp() {
		LinphoneCore lc = LinphoneManager.getLc();
		LinphoneCall currentCall = lc.getCurrentCall();

		if (currentCall != null) {
			lc.terminateCall(currentCall);
		} else if (lc.isInConference()) {
			lc.terminateConference();
		} else {
			lc.terminateAllCalls();
		}
	}

	private void disableVideo() {
		final LinphoneCall call = LinphoneManager.getLc().getCurrentCall();
		if (call == null) {
			return;
		}

		if (!call.getRemoteParams().isLowBandwidthEnabled()) {
			LinphoneManager.getInstance().addVideo();
		}
	}


	@Override
	protected void onResume() {
		super.onResume();

		LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
		if (lc != null) {
			lc.addListener(mListener);
		}
	}


	@Override
	protected void onPause() {
		LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
		if (lc != null) {
			lc.removeListener(mListener);
		}

		super.onPause();
	}

	@Override
	protected void onDestroy() {
		LinphoneManager.getInstance().changeStatusToOnline();
		super.onDestroy();
		System.gc();
	}

}
