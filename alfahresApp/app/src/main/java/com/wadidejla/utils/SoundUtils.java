package com.wadidejla.utils;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import wadidejla.com.alfahresapp.R;

/**
 * Created by snouto on 07/06/15.
 */
public class SoundUtils {


    public static void playSound(Context context)
    {
        Uri ringToneUri = Uri.parse("android.resource://wadidejla.com.alfahresapp/"+ R.raw.marked);

        Ringtone ringtone = RingtoneManager.getRingtone(context, ringToneUri);

        if(ringtone != null)
            ringtone.play();
    }
}
