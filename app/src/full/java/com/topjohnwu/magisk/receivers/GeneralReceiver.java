package com.topjohnwu.magisk.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.topjohnwu.magisk.Const;
import com.topjohnwu.magisk.Data;
import com.topjohnwu.magisk.MagiskManager;
import com.topjohnwu.magisk.SuRequestActivity;
import com.topjohnwu.magisk.services.OnBootService;
import com.topjohnwu.magisk.utils.DlInstallManager;
import com.topjohnwu.magisk.utils.SuConnector;
import com.topjohnwu.superuser.Shell;

public class GeneralReceiver extends BroadcastReceiver {
    private final static String TAG = "GeneralReceiver";
    public final static String WX_PKN = "com.tencent.mm";

    private String getPkg(Intent i) {
        return i.getData() == null ? "" : i.getData().getEncodedSchemeSpecificPart();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        MagiskManager mm = Data.MM();
        if (intent == null)
            return;
        String action = intent.getAction();
        if (action == null)
            return;
        switch (action) {
            case Intent.ACTION_BOOT_COMPLETED:

                String bootAction = intent.getStringExtra("action");
                if (bootAction == null)
                    bootAction = "boot";

                switch (bootAction) {
                    case "request":
                        Log.d(TAG,"-----request-----");
                        Intent i = new Intent(mm, Data.classMap.get(SuRequestActivity.class))
                                .putExtra("socket", intent.getStringExtra("socket"))
                                .putExtra("version", 2)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mm.startActivity(i);
                        break;
                    case "log":
                        //SuConnector.handleLogs(intent, 2);
                        break;
                    case "notify":
                        SuConnector.handleNotify(intent);
                        break;
                    case "boot":
                        Log.d(TAG,"-----ACTION_BOOT_COMPLETED-----");
                    default:
                        /* The actual on-boot trigger */
                        OnBootService.enqueueWork(mm);
                        break;
                }
                break;
            case Intent.ACTION_PACKAGE_REPLACED:
                // This will only work pre-O
                if (mm.prefs.getBoolean(Const.Key.SU_REAUTH, false)) {
                    mm.mDB.deletePolicy(getPkg(intent));
                }

                hideAppPkg(getPkg(intent),WX_PKN);

                break;
            case Intent.ACTION_PACKAGE_FULLY_REMOVED:
                String pkg = getPkg(intent);
                mm.mDB.deletePolicy(pkg);
                Shell.su("magiskhide --rm " + pkg).submit();
                break;
            case Const.Key.BROADCAST_MANAGER_UPDATE:
                Data.managerLink = intent.getStringExtra(Const.Key.INTENT_SET_LINK);
                DlInstallManager.upgrade(intent.getStringExtra(Const.Key.INTENT_SET_NAME));
                break;
            case Const.Key.BROADCAST_REBOOT:
                Shell.su("/system/bin/reboot").submit();
                break;
        }
    }

    private void hideAppPkg(String pkg,String hidePkg){
        if(pkg != null){
            Log.d(TAG,"hidePkg = "+ hidePkg + " receive = "+pkg);
            if(hidePkg.equals(pkg)){
                Shell.su("magiskhide --add " + pkg).submit();
            }
        }
    }
}
