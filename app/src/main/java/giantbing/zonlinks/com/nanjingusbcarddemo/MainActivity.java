package giantbing.zonlinks.com.nanjingusbcarddemo;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Iterator;

import giantbing.zonlinks.com.nanjingusbcarddemo.usbrfidreader.ICReaderApi;

public class MainActivity extends AppCompatActivity {
    public static final String ACTION_DEVICE_PERMISSION = "com.giantbing.USB_PERMISSION";

    Button readBtn;
    TextView idText;
    PendingIntent mPermissionIntent;
    UsbManager mUsbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readBtn = (Button) findViewById(R.id.readBtn);
        idText = (TextView) findViewById(R.id.CardId);

        readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDevice();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initDevice()  {
        UsbHelper usbHelper = new UsbHelper(new UsbHelper.UsbPermision() {
            @Override
            public void onSucess(UsbDevice device, UsbManager mUsbManager) {
                //api = new ICReaderApi(device,mUsbManager);
            }

            @Override
            public void onErro() {

            }
        });
        if (usbHelper.isUsbDetach(this)){
            //readCardId();
        }
    }








}
