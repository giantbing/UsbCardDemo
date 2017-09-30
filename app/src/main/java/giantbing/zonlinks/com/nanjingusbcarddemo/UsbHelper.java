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
import java.util.concurrent.TimeUnit;

import giantbing.zonlinks.com.nanjingusbcarddemo.usbrfidreader.ICReaderApi;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by P on 2017/9/29.
 */

public class UsbHelper {
    public static final String ACTION_DEVICE_PERMISSION = "com.giantbing.USB_PERMISSION";
    private PendingIntent mPermissionIntent;
    private UsbManager mUsbManager;
    private UsbPermision permisionInterFace;
    ICReaderApi api;

    interface UsbPermision {
        void onSucess(UsbDevice device, UsbManager mUsbManager);

        void onErro();
    }

    public UsbHelper(UsbPermision permisionInterFace) {
        this.permisionInterFace = permisionInterFace;
    }

    public UsbManager getManager(Context context) {
        if (mUsbManager == null)
            mUsbManager = (UsbManager) context.getSystemService(context.USB_SERVICE);

        return mUsbManager;
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
                hasPermisimion(context, device);
            }
        }

        return isDetach;
    }

    //是否有权限
    private void hasPermisimion(Context context, UsbDevice device) {
        if (mUsbManager.hasPermission(device)) {
            permisionInterFace.onSucess(device, mUsbManager);
            api = new ICReaderApi(device, mUsbManager);
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
                            permisionInterFace.onSucess(device, mUsbManager);
                            api = new ICReaderApi(device, mUsbManager);
                        }
                    } else {
                        permisionInterFace.onErro();
                    }
                }
            }
        }
    };


    public void HasUsb(final Context context) {
        Observable.interval(1000, TimeUnit.MICROSECONDS, Schedulers.io())
                .flatMap(new Function<Long, ObservableSource<UsbDevice>>() {
                    @Override
                    public ObservableSource<UsbDevice> apply(Long aLong) throws Exception {
                        ObservableSource obs = new Observable() {
                            @Override
                            protected void subscribeActual(Observer observer) {
                                getManager(context);
                                UsbDevice device = null;
                                HashMap<String, UsbDevice> deviceHashMap = mUsbManager.getDeviceList();
                                Iterator<UsbDevice> iterator = deviceHashMap.values().iterator();
                                while (iterator.hasNext()) {
                                    device = iterator.next();
                                    if (device.getProductId() == 53 && device.getVendorId() == 65535) {
                                        observer.onNext(device);
                                    }

                                }
                            }
                        };

                        return obs;

                    }
                });

    }


    public void onDestroy(Context context) {

    }

    private void readCardId() {
        String text = new String();
        byte mode = 0x52;

        byte halt = (byte) 0;
        byte[] snr = new byte[1];
        byte[] value = new byte[5]; // card number
        int result = api.GET_SNR(mode, halt, snr, value);
        text = showStatue(text, result);
        if (result == 0) {
            if (snr[0] == 0x00)
                text += ("Only one card.....\n");
            else
                text += ("More than one card......\n");
            text = showData(text, value, "The card number:\n", 0, 4);
            Bee(10, 1);
        } else
            text = showStatue(text, snr[0]);
    }

    private String showStatue(String text, int Code) {
        String msg = null;
        switch (Code) {
            case 0x00:
                msg = "命令执行成功 .....";
                break;
            case 0x01:
                msg = "命令操作失败 .....";
                break;
            case 0x02:
                msg = "地址校验错误 .....";
                break;
            case 0x03:
                msg = "没有选择COM口 .....";
                break;
            case 0x04:
                msg = "读写器返回超时 .....";
                break;
            case 0x05:
                msg = "数据包流水号不正确 .....";
                break;
            case 0x07:
                msg = "接收异常 .....";
                break;
            case 0x0A:
                msg = "参数值超出范围 .....";
                break;
            case 0x80:
                msg = "参数设置成功 .....";
                break;
            case 0x81:
                msg = "参数设置失败 .....";
                break;
            case 0x82:
                msg = "通讯超时.....";
                break;
            case 0x83:
                msg = "卡不存在.....";
                break;
            case 0x84:
                msg = "接收卡数据出错.....";
                break;
            case 0x85:
                msg = "未知的错误.....";
                break;
            case 0x87:
                msg = "输入参数或者输入命令格式错误.....";
                break;
            case 0x89:
                msg = "输入的指令代码不存在.....";
                break;
            case 0x8A:
                msg = "在对于卡块初始化命令中出现错误.....";
                break;
            case 0x8B:
                msg = "在防冲突过程中得到错误的序列号.....";
                break;
            case 0x8C:
                msg = "密码认证没通过.....";
                break;
            case 0x8F:
                msg = "输入的指令代码不存在.....";
                break;
            case 0x90:
                msg = "卡不支持这个命令.....";
                break;
            case 0x91:
                msg = "命令格式有错误.....";
                break;
            case 0x92:
                msg = "在命令的FLAG参数中，不支持OPTION 模式.....";
                break;
            case 0x93:
                msg = "要操作的BLOCK不存在.....";
                break;
            case 0x94:
                msg = "要操作的对象已经别锁定，不能进行修改.....";
                break;
            case 0x95:
                msg = "锁定操作不成功.....";
                break;
            case 0x96:
                msg = "写操作不成功.....";
                break;
        }
        msg += '\n';
        text += msg;
        return text;
    }

    private String showData(String text, byte[] data, String str, int pos,
                            int len) {
        String dStr = "";
        for (int i = 0; i < len; i++) {
            dStr += String.format("%02x ", data[i + pos]);
        }
        text += (str + dStr.toUpperCase() + '\n');
        return text;
    }

    private void Bee(int freqInt, int durationInt) {
        String text = new String();
        byte freq = (byte) freqInt;
        byte duration = (byte) durationInt;
        byte[] buffer = new byte[1];
        int result = api.API_ControlBuzzer(freq, duration, buffer);
    }
}
