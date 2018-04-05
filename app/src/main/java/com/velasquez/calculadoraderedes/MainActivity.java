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
    TextView text_netmask, text_network, text_broadcast, text_hosts;
    Button btn_calcular;
    String network, broadcast;
    int mask[] = new int[4];
    int wildcard[] = new int[4];
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
        text_hosts = findViewById(R.id.text_hosts);
    }

    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                validateIp();
                getNetMask();
                getNetwork();
                getBroadcast();
                text_hosts.setText("Hosts: " + (int)Math.pow(2, 32-Integer.parseInt(edit_mask.getText().toString())));
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
        else if (Integer.parseInt(temp) < 1 || Integer.parseInt(temp) > 32)
            throw new IllegalArgumentException("Valor no permitido para la máscara: " + temp);
        else {
            int prefix = 0xffffffff << (32 - Integer.parseInt(temp));
            mask[0] = prefix >>> 24;
            mask[1] = prefix >> 16 & 0xff;
            mask[2] = prefix >> 8 & 0xff;
            mask[3] = prefix & 0xff;
            wildcard[0] = (~mask[0] << 24) >>> 24;
            wildcard[1] = (~mask[1] << 24) >>> 24;
            wildcard[2] = (~mask[2] << 24) >>> 24;
            wildcard[3] = (~mask[3] << 24) >>> 24;
            text_netmask.setText("Mascara de Red: "+mask[0]+" . "+mask[1]+" . "+mask[2]+" . "+mask[3]);
        }
    }

    void getNetwork() {
        network = (mask[0] & mip[0]) + "." + (mip[1] & mask[1]) + "." + (mip[2] & mask[2])
                + "." + (mip[3] & mask[3]);
        text_network.setText(getString(R.string.text_network) + " " + network);
    }

    void getBroadcast() {
        broadcast = (wildcard[0] | mip[0]) + "." + (mip[1] | wildcard[1]) + "." + (mip[2] | wildcard[2])
                + "." + (mip[3] | wildcard[3]);
        text_broadcast.setText(getString(R.string.text_broadcast) + " " + broadcast);

        text_hosts.setText("Wildcard : "+(wildcard[0])+" . "+(wildcard[1])+" . "+(wildcard[2])+" . "+(wildcard[3]));
    }
}
