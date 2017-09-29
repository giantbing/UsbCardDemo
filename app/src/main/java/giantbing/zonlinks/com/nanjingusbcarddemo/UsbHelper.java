package giantbing.zonlinks.com.nanjingusbcarddemo;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;

import giantbing.zonlinks.com.nanjingusbcarddemo.usbrfidreader.ICReaderApi;

/**
 * Created by P on 2017/9/29.
 */

public class UsbHelper {
    public static final String ACTION_DEVICE_PERMISSION = "com.giantbing.USB_PERMISSION";
    private PendingIntent mPermissionIntent;
    private UsbManager mUsbManager;
    private UsbPermision permisionInterFace;

    interface UsbPermision {
        void onSucess(UsbDevice device,UsbManager mUsbManager);

        void onErro();
    }

    public UsbHelper(UsbPermision permisionInterFace) {
        this.permisionInterFace = permisionInterFace;
    }
    //是否连接了
    public boolean isUsbDetach(Context context) {
        boolean isDetach = false;
        mUsbManager = (UsbManager) context.getSystemService(context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceHashMap = mUsbManager.getDeviceList();
        Iterator<UsbDevice> iterator = deviceHashMap.values().iterator();
        while (iterator.hasNext()) {
            UsbDevice device = iterator.next();
            if (device.getProductId() == 53 && device.getVendorId() == 65535) {
                isDetach = true;
                hasPermisimion(context,device);
            }
        }

        return isDetach;
    }
    //是否有权限
    private void hasPermisimion(Context context, UsbDevice device) {
        if (mUsbManager.hasPermission(device)) {
            permisionInterFace.onSucess(device,mUsbManager);
        } else {
            mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_DEVICE_PERMISSION), 0);
            IntentFilter permissionFilter = new IntentFilter(ACTION_DEVICE_PERMISSION);
            context.registerReceiver(mUsbReceiver, permissionFilter);
            mUsbManager.requestPermission(device, mPermissionIntent);

        }
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_DEVICE_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            permisionInterFace.onSucess(device,mUsbManager);
                        }
                    } else {
                        permisionInterFace.onErro();
                    }
                }
            }
        }
    };

    public void onDestroy(Context context) {

    }
}
