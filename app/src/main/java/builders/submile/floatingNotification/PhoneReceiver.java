package builders.submile.floatingNotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import builders.submile.messaging.MApp;

/**
 * Created by Alfie on 2016/04/18.
 */
public class PhoneReceiver extends BroadcastReceiver
{
    final static String tag = PhoneReceiver.class.getSimpleName();

    private String phoneNumber;

static boolean wasRinging = false;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        String extraState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        Log.d(tag,"Phone Number = "+ phoneNumber);

        try
        {
            if (extraState != null)
            {

                if (TelephonyManager.EXTRA_STATE_RINGING.equals(extraState))
                {
                    wasRinging = true;
                    if (phoneNumber == null)
                    phoneNumber = intent
                            .getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                    MApp.ctx.startService(new Intent(MApp.ctx, FloatingPilotService.class));

                } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(extraState))
                {
                    if(!wasRinging)
                    {
                        if (phoneNumber == null)
                            phoneNumber = intent
                                    .getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                        MApp.ctx.startService(new Intent(MApp.ctx, FloatingPilotService.class));

                        wasRinging = false;
                    }
                } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(extraState))
                {
                           MApp.ctx.startService(new Intent(MApp.ctx, FloatingPilotService.class));

                    wasRinging = false;
                }

            }
        } catch (Exception e)

        {
            Log.e(tag, "Exception");
            e.printStackTrace();
        }
    }
}
    
/*
            if (extraState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
            {
                Intent recorderIntent = new Intent(context,
                        RecorderService.class);
                recorderIntent.putExtra("commandType",
                        Constants.STATE_CALL_START);
                context.startService(recorderIntent);
            } else if (extraState
                    .equals(TelephonyManager.EXTRA_STATE_IDLE))
            {
                Intent recorderIntent = new Intent(context,
                        RecorderService.class);
                recorderIntent.putExtra("commandType",
                        Constants.STATE_CALL_END);
                context.startService(recorderIntent);
            } else if (extraState
                    .equals(TelephonyManager.EXTRA_STATE_RINGING))
            {
                if (phoneNumber == null)
                    phoneNumber = intent
                            .getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Intent recorderIntent = new Intent(context,
                        RecorderService.class);
                recorderIntent.putExtra("commandType",
                        Constants.STATE_INCOMING_NUMBER);
                recorderIntent.putExtra("phoneNumber", phoneNumber);
                recorderIntent.putExtra("silentMode", silent);
                context.startService(recorderIntent);
            }
        }else if (phoneNumber != null)
    {
        Intent recorderIntent = new Intent(context, RecorderService.class);
        recorderIntent.putExtra("commandType",
                Constants.STATE_INCOMING_NUMBER);
        recorderIntent.putExtra("phoneNumber", phoneNumber);
        recorderIntent.putExtra("silentMode", silent);
        context.startService(recorderIntent);
    }
    }
}
}

        }
*/
