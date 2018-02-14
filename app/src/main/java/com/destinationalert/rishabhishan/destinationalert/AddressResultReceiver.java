package com.destinationalert.rishabhishan.destinationalert;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by rishu on 3/3/2016.
 */
public class AddressResultReceiver extends ResultReceiver {


    public AddressResultReceiver(Handler handler) {
        super(handler);
        if (m == null)
            m = new MapsActivity();
    }

    MapsActivity m;

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        System.out.println("**** Address is " + resultData.getString(Constants.RESULT_DATA_KEY));
        Constants.ADDRESS = resultData.getString(Constants.RESULT_DATA_KEY);
        m.updateUIWidgets();

    }
}

