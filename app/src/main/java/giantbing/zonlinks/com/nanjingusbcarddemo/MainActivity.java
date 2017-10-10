package giantbing.zonlinks.com.nanjingusbcarddemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import giantbing.zonlinks.com.usbcardreaderlibrary.usbrfidreader.UsbHelper;

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
        initUsbDevice();
        usbHelper.startRead(MainActivity.this);
        readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usbHelper.stopRead();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        usbHelper.onDestroy();
    }

    private void initUsbDevice()  {
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
