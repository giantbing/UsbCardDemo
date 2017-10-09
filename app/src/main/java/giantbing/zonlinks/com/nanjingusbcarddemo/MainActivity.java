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
    Button readBtn;
    TextView idText;
    UsbHelper usbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readBtn = (Button) findViewById(R.id.readBtn);
        idText = (TextView) findViewById(R.id.CardId);
        initDevice();
        readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usbHelper.startRead(MainActivity.this);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initDevice()  {
        usbHelper = new UsbHelper(new UsbHelper.UsbPermision() {


            @Override
            public void onSucess(String id) {
                idText.append(id);
            }

            @Override
            public void onErro() {

            }
        });

    }








}
