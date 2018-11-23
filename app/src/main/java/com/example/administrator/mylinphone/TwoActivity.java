package com.example.administrator.mylinphone;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.mylinphone.util.ContactsManager;
import com.example.administrator.mylinphone.util.LinphoneManager;
import com.example.administrator.mylinphone.util.LinphonePreferences;
import com.example.administrator.mylinphone.util.LinphoneService;

import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneCoreListenerBase;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.core.Reason;


public class TwoActivity  extends AppCompatActivity implements View.OnClickListener {

    private TextView net_state;
    private LinphoneCoreListenerBase mListener;
    private LinphoneCall mCall;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);
        net_state = findViewById(R.id.net_state);
        net_state.setOnClickListener(this);
        ContactsManager.getInstance().initializeSyncAccount(getApplicationContext(), getContentResolver());

        mListener = new LinphoneCoreListenerBase(){
            @Override
            public void messageReceived(LinphoneCore lc, LinphoneChatRoom cr, LinphoneChatMessage message) {

            }

            @Override
            public void registrationState(LinphoneCore lc, LinphoneProxyConfig proxy, LinphoneCore.RegistrationState state, String smessage) {
            }



            @Override
            public void callState(LinphoneCore lc, LinphoneCall call, LinphoneCall.State state, String message) {//打电话回调
                if (state == LinphoneCall.State.IncomingReceived) {
                    Log.i("sss","打进");
                } else if (state == LinphoneCall.State.OutgoingInit || state == LinphoneCall.State.OutgoingProgress) {
                    Log.i("sss","打出");
                } else if (state == LinphoneCall.State.CallEnd || state == LinphoneCall.State.Error || state == LinphoneCall.State.CallReleased) {
                    Log.i("sss","end");
                }else if(LinphoneCall.State.Connected == state){
                           startActivity(new Intent(TwoActivity.this,CallActivity.class));
                }else if(state == LinphoneCall.State.Error){
                    Log.i("sss","Error");
                }

                int missedCalls = LinphoneManager.getLc().getMissedCallsCount();
                Log.i("sss","missedCalls " + missedCalls);

            }
        };
        if (LinphonePreferences.instance().getAccountCount() > 0) {

        }else {
            saveCreatedAccount("7003", "sipto1234", null,"192.168.11.1:5060");
        }

        LinphoneManager.getLc().setDeviceRotation(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.net_state:
                try {
                    if (!LinphoneManager.getInstance().acceptCallIfIncomingPending()) {
                        LinphoneManager.getInstance().newOutgoingCall("7004");
                    }
                } catch (LinphoneCoreException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!LinphoneService.isReady()) {
            startService(new Intent(Intent.ACTION_MAIN).setClass(this, LinphoneService.class));
        }

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

    public void saveCreatedAccount(String username, String password, String ha1, String domain) {
        LinphoneAddress.TransportType transport = LinphoneAddress.TransportType.LinphoneTransportTcp;
        String identity = "sip:" + username + "@" + domain;
        try {
             LinphoneCoreFactory.instance().createLinphoneAddress(identity);
        } catch (LinphoneCoreException e) {
//            Log.e("ss",e);
        }
        LinphonePreferences.AccountBuilder builder = new LinphonePreferences.AccountBuilder(LinphoneManager.getLc())
                .setUsername(username)
                .setDomain(domain)
                .setHa1(ha1)
                .setPassword(password);

        if(transport != null) {
            builder.setTransport(transport);
        }

        try {
            builder.saveNewAccount();
        } catch (LinphoneCoreException e) {
//            Log.e(e);
        }
    }


}
