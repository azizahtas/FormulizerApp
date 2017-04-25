package com.example.stark.formulizer.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.stark.formulizer.R;

/**
 * Created by Stark on 27-02-2017.
 */

public class PreferenceReader {

    private SharedPreferences sp;
    private Context context;
    private String API_PATH,TOKEN;
    public PreferenceReader(Context context){
        this.context = context;
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getApiPath() {
           this.API_PATH = sp.getString(context.getString(R.string.pref_api_profile_key),context.getString(R.string.pref_api_online_value));
        return API_PATH;
    }

    public String getTOKEN() {
        this.TOKEN = sp.getString(context.getString(R.string.pref_token_key),"");
        return TOKEN;
    }

    public void setTOKEN(String TOKEN) {
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(context.getString(R.string.pref_token_key),TOKEN);
        edit.apply();
        this.TOKEN = TOKEN;
    }
}
