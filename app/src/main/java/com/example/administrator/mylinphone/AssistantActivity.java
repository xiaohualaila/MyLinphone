package com.example.administrator.mylinphone;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.mylinphone.util.LinphoneManager;
import com.example.administrator.mylinphone.util.LinphonePreferences;

import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreListenerBase;
import org.linphone.core.LinphoneProxyConfig;

public class AssistantActivity  extends AppCompatActivity {
    private static AssistantActivity instance;
    private LinphonePreferences mPrefs;
    private LinphoneCoreListenerBase mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        mPrefs = LinphonePreferences.instance();
        mListener = new LinphoneCoreListenerBase() {

            @Override
            public void configuringStatus(LinphoneCore lc, final LinphoneCore.RemoteProvisioningState state, String message) {

                if (state == LinphoneCore.RemoteProvisioningState.ConfiguringSuccessful) {
                    Log.i("sss","注册成功！");
                } else if (state == LinphoneCore.RemoteProvisioningState.ConfiguringFailed) {
//                    Toast.makeText(AssistantActivity.instance(), getString(R.string.remote_provisioning_failure), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void registrationState(LinphoneCore lc, LinphoneProxyConfig cfg, LinphoneCore.RegistrationState state, String smessage) {

                    if (state == LinphoneCore.RegistrationState.RegistrationOk) {

                    }



            }
        };
        instance = this;
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


    public static AssistantActivity instance() {
        return instance;
    }
}
