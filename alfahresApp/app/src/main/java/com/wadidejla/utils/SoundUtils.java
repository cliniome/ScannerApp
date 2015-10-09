package com.wadidejla.utils;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

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


    public static void PlayError(Context context)
    {
        Uri ringToneUri = Uri.parse("android.resource://wadidejla.com.alfahresapp/"+ R.raw.error);

        Ringtone ringtone = RingtoneManager.getRingtone(context, ringToneUri);

        if(ringtone != null)
        {
            ringtone.play();
            //vibrateDevice(context);
        }

    }

    public static void vibrateDevice(Context activity) {

        try
        {

            Vibrator vibrator = (Vibrator)activity.getSystemService(Context.VIBRATOR_SERVICE);

            if(vibrator != null &&vibrator.hasVibrator())
                vibrator.vibrate(1000);

        }catch (Exception s)
        {
            Log.e("SoundUtils",s.getMessage());
        }

    }
}
