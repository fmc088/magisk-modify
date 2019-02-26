package com.topjohnwu.magisk.services;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.topjohnwu.magisk.Const;
import com.topjohnwu.magisk.Data;
import com.topjohnwu.magisk.components.Notifications;
import com.topjohnwu.magisk.receivers.GeneralReceiver;
import com.topjohnwu.superuser.Shell;
import com.topjohnwu.superuser.ShellUtils;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

public class OnBootService extends JobIntentService {
    private final static String TAG = "OnBootService";


    public static void enqueueWork(Context context) {
        enqueueWork(context, Data.classMap.get(OnBootService.class), Const.ID.ONBOOT_SERVICE_ID, new Intent());
    }
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d(TAG,"---onHandleWork---");
        /* Devices with DTBO might want to patch dtbo.img.
         * However, that is not possible if Magisk is installed by
         * patching boot image with Magisk Manager and flashed via
         * fastboot, since at that time we do not have root.
         * Check for dtbo status every boot time, and prompt user
         * to reboot if dtbo wasn't patched and patched by Magisk Manager.
         * */
        if (Shell.rootAccess() && ShellUtils.fastCmdResult("mm_patch_dtbo")) {
            Notifications.dtboPatched();
        }
        hideWxPkg();
    }
    public void hideWxPkg(){
        try {
                ApplicationInfo ai;
                try {
                    ai = OnBootService.this.getPackageManager().getApplicationInfo(GeneralReceiver.WX_PKN, 0);
                    Shell.su("magiskhide --add " + GeneralReceiver.WX_PKN).submit();
                    Log.d(TAG," ----magiskhide----- ");
                } catch (PackageManager.NameNotFoundException e) {
                    Log.d(TAG," NameNotFoundException = "+e.getMessage());
                }
        } catch (Exception e) {
            Log.w(TAG, "hideWxPkg: " + e.getMessage());
        }
    }
}
