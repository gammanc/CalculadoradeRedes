package com.velasquez.calculadoraderedes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText edit_ip, edit_mask;
    TextView text_netmask, text_network, text_broadcast, text_test;
    Button btn_calcular;
    String network, broadcast;
    int mask[] = new int[4];
    int wmask[] = new int[4];
    int mip[] = new int[4];

    private final String IP_ADDRESS_PATTERN =
            "^(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\." +
            "(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\." +
            "(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\." +
            "(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edit_ip = findViewById(R.id.edit_ip);
        edit_mask = findViewById(R.id.edit_mask);
        btn_calcular = findViewById(R.id.btn_calcular);
        btn_calcular.setOnClickListener(onClick);
        text_netmask = findViewById(R.id.text_netmask);
        text_network = findViewById(R.id.text_network);
        text_broadcast = findViewById(R.id.text_broadcast);
        text_test = findViewById(R.id.text_test);
    }

    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                validateIp();
                getNetMask();
                getNetwork();
                getBroadcast();
            }
            catch (IllegalArgumentException e) {
                Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                toast.show();
            }
        }
    };

    void validateIp() {
        String temp = edit_ip.getText().toString().trim();
        if (temp.isEmpty())
            throw new IllegalArgumentException("Campo de IP vacío");
        else if (!temp.matches(IP_ADDRESS_PATTERN))
            throw new IllegalArgumentException("Formato de IP no válido");
        else {
            mip[0] = Integer.parseInt(temp.split("\\.")[0]);
            mip[1] = Integer.parseInt(temp.split("\\.")[1]);
            mip[2] = Integer.parseInt(temp.split("\\.")[2]);
            mip[3] = Integer.parseInt(temp.split("\\.")[3]);
        }
    }

    void getNetMask() {
        String temp = edit_mask.getText().toString().trim();
        if (temp.isEmpty())
            throw new IllegalArgumentException("Campo de máscara vacío");
        else if (Integer.parseInt(temp) > 32)
            throw new IllegalArgumentException("Valor no permitido para la máscara: " + temp);
        else {
            String octeto = "";
            String octetow = "";
            for (int i = 0; i < 32; i++) {
                if (i < Integer.parseInt(temp)){
                    octeto += "1";
                    octetow += "0";
                }
                else {
                    octeto += "0";
                    octetow += "1";
                }
                if ((i+1) % 8 == 0) {
                    mask[(i/8)] = Integer.parseInt(octeto, 2);
                    wmask[(i/8)] = Integer.parseInt(octetow, 2);
                    octeto = octetow = "";
                }
            }
            text_netmask.setText(getString(R.string.text_netmask) + " "
                    + mask[0] + "." + mask[1] + "." + mask[2] + "." + mask[3]);
        }
    }

    void getNetwork() {
        network = (mask[0] & mip[0]) + "." + (mip[1] & mask[1]) + "." + (mip[2] & mask[2])
                + "." + (mip[3] & mask[3]);
        text_network.setText(getString(R.string.text_network) + " " + network);
    }

    void getBroadcast() {
        broadcast = (wmask[0] | mip[0]) + "." + (mip[1] | wmask[1]) + "." + (mip[2] | wmask[2])
                + "." + (mip[3] | wmask[3]);
        text_broadcast.setText(getString(R.string.text_broadcast) + " " + broadcast);

        text_test.setText("Wildcard : "+(wmask[0])+" . "+(wmask[1])+" . "+(wmask[2])+" . "+(wmask[3]));
    }
}
