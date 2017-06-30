package com.realizer.salladodriver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import com.realizer.salladodriver.utils.Singleton;

/**
 * Created by Bhagyashri on 2/17/2016.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


        if (wifi.getState().equals(NetworkInfo.State.CONNECTED) || mobile.getState().equals(NetworkInfo.State.CONNECTED)) {

            SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String getValueBack = sharedpreferences.getString("IsLogin", "");
            if(getValueBack.length()==0)
                getValueBack="false";
            // Do something
            try {
                if (!getValueBack.equalsIgnoreCase("false")) {
                    Intent autoservice = new Intent(context, ServiceLocationChange.class);
                    Singleton.getInstance().setAutoserviceIntent(autoservice);
                    context.startService(autoservice);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            if (Singleton.getInstance().getAutoserviceIntent() != null) {
                context.stopService(Singleton.getInstance().getAutoserviceIntent());
            }
        }
    }
}