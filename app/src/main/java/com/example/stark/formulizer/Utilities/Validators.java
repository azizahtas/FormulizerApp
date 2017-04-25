package com.example.stark.formulizer.Utilities;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * Created by Stark on 01-03-2017.
 */

public class Validators {

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public final static boolean isConnected()
    {
        boolean b = true;
        //boolean b = false;
      /*  String command = "ping -c 1 google.com";
        try{
           b= Runtime.getRuntime().exec (command).waitFor() == 0;
        }
        catch (IOException | InterruptedException ex){
            ex.printStackTrace();
        }*/
        return b;
    }
}
