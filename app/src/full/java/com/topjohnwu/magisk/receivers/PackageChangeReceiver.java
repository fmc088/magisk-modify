package com.topjohnwu.magisk.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import android.util.Log;

import com.topjohnwu.magisk.utils.SysPropUtils;
import com.topjohnwu.superuser.Shell;


public class PackageChangeReceiver extends BroadcastReceiver {

    private final static String TAG = "PackageChangeReceiver";
    public final static String SYS_PROP_ASST_PLUS_ENABLED = "persist.sys.scrm.asstplus.enabled";
    public final static String HOOK_PKN = "com.scrm.robot.plus";

    private static String getPackageName(Intent intent) {
        Uri uri = intent.getData();
        return (uri != null) ? uri.getSchemeSpecificPart() : null;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        try {
            Log.d(TAG," action  ="+intent.getAction());
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED) && intent.getBooleanExtra(Intent.EXTRA_REPLACING, false))
                // Ignore existing packages being removed in order to be updated
                return;
            String packageName = getPackageName(intent);
            if (packageName == null)
                return;
            Log.d(TAG,"  packageName  ="+packageName);
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_CHANGED)) {
                // make sure that the change is for the complete package, not only a
                // component
                String[] components = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_COMPONENT_NAME_LIST);
                if (components != null) {
                    boolean isForPackage = false;
                    for (String component : components) {
                        if (packageName.equals(component)) {
                            isForPackage = true;
                            break;
                        }
                    }
                    if (!isForPackage)
                        return;
                }
            }
            if(packageName.equals(HOOK_PKN)){
                boolean asstPlusEnabled = Boolean.valueOf(SysPropUtils.getSysPro(SYS_PROP_ASST_PLUS_ENABLED));
                Log.d(TAG,"  asstPlusEnabled  ="+asstPlusEnabled);
                if(asstPlusEnabled){
                    Log.d(TAG,"  asstPlusEnabled  ="+asstPlusEnabled);
                    Shell.su("/system/bin/reboot").submit();
                }
            }
        }catch (Exception e){
            Log.e(TAG,"Exception = "+e.getMessage());
            e.printStackTrace();
        }

    }
}
