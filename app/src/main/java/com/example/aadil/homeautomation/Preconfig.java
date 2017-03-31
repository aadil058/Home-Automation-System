package com.example.aadil.homeautomation;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Preconfig extends AppCompatActivity {

    // Automatic Controls
    ArrayList<Switch> ASwitch;
    ArrayList<String> AName;
    ArrayList<Integer> ARelay;
    ArrayList<Boolean> AState;

    // Manual Controls
    ArrayList<Switch> MSwitch;
    ArrayList<String> MName;
    ArrayList<Integer> MRelay;
    ArrayList<Boolean> MState;

    private ListView listView;

    private BluetoothService bluetoothService;

    // Select the mode
    private String Mode;

    private SharedPreferences shared;
    private SharedPreferences.Editor editor;

    Button indicator;
    Button add;
    Button mode;
    TextView title;

    private int PSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        shared = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        editor = shared.edit();

        ASwitch = new ArrayList<>();
        AName = new ArrayList<>();
        ARelay = new ArrayList<>();
        AState = new ArrayList<>();

        MSwitch = new ArrayList<>();
        MName = new ArrayList<>();
        MRelay = new ArrayList<>();
        MState = new ArrayList<>();

        if(shared.getInt("count", 0) == 0) {
            editor.putInt("count", 0);
            PSize = 0;
        }
        else {
            PSize = shared.getInt("count", 0);
            for(int i = 0; i < 4; ++i) {
                for(int j = 0; j < PSize; ++j) {
                    if(i == 0) {
                        AName.add(shared.getString("AName" + j, null));
                        MName.add(shared.getString("AName" + j, null));
                    }
                    if(i == 1) {
                        ASwitch.add(new Switch(this));
                        MSwitch.add(new Switch(this));
                    }
                    if(i == 2) {
                        ARelay.add(shared.getInt("ARelay" + j, -1));
                        MRelay.add(shared.getInt("ARelay" + j, -1));
                    }
                    if(i == 3) {
                        AState.add(shared.getBoolean("AState" + j, false));
                        MState.add(shared.getBoolean("AState" + j, false));
                        if(shared.getBoolean("AState" + j, false)) {
                            ASwitch.get(j).setChecked(true);
                            MSwitch.get(j).setChecked(true);
                        }
                    }
                }
            }
        }

        mode = (Button) findViewById(R.id.mode);
        add = (Button) findViewById(R.id.add);
        indicator = (Button) findViewById(R.id.indicator);
        title = (TextView) findViewById(R.id.title);

        bluetoothService = new BluetoothService();
        bluetoothService.init();

        showAutomatic();

        final Timer t = new Timer();
        final Timer tt = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!bluetoothService.isConnected()) {
                    bluetoothService.init();
                    System.out.println("Not Connected");
                }
                else {
                    System.out.println("Connected");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            indicator.setBackgroundColor(Color.GREEN);
                            ProcessPreviousData();
                        }
                    });

                    tt.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(!bluetoothService.isConnected()) {
                                        indicator.setBackgroundColor(Color.RED);
                                    }
                                }
                            });
                        }

                    }, 0, 3000);
                }
            }

        }, 0, 1000);

        mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchMode();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialog();
            }
        });
    }

    void ProcessPreviousData() {
        ((CustomAdapter) listView.getAdapter()).update();
    }

    void ShowDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_menu);
        dialog.setTitle("Add Device Dialog");

        Button button = (Button) dialog.findViewById(R.id.add2);
        final EditText name = (EditText) dialog.findViewById(R.id.deviceName);
        final EditText relay = (EditText) dialog.findViewById(R.id.relay);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                updateList(name.getText().toString(), Integer.parseInt(relay.getText().toString()));
            }
        });

        dialog.show();
    }

    public void updateList(String Name, Integer Relay) {
        if(Mode.equals("Automatic")) {
            this.AName.add(Name);
            this.ARelay.add(Relay);
            this.AState.add(false);
            this.ASwitch.add(new Switch(this));
            this.PersistAutomatic(Name, Relay, false);
        }
        else {
            this.MName.add(Name);
            this.MRelay.add(Relay);
            this.MState.add(false);
            this.MSwitch.add(new Switch(this));
        }
    }

    public void SwitchMode() {
        if(this.Mode.equals("Automatic"))
            showManual();
        else
            showAutomatic();
    }

    public void PersistAutomatic(String Name, Integer Relay, Boolean State) {
        int index = shared.getInt("count", 0);
        editor.putInt("count", index + 1);
        editor.putString("AName" + index, Name);
        editor.putInt("ARelay" + index, Relay);
        editor.putBoolean("AState" + index, State);
        editor.commit();
    }

    public void showManual() {
        mode.setText("Switch to Automatic Mode");
        title.setText("Manual Configuration");
        this.Mode = "Manual";

        if(listView == null) {
            listView = (ListView) findViewById(R.id.ListView);
            listView.setAdapter(new CustomAdapter(this, MName, MSwitch, MRelay, MState, bluetoothService, shared, editor, "Manual"));
            ((CustomAdapter) listView.getAdapter()).notifyDataSetChanged();
        }
        else {
            ((CustomAdapter) listView.getAdapter()).updateData(MName, MSwitch, MRelay, MState);
            ((CustomAdapter) listView.getAdapter()).notifyDataSetChanged();
        }
    }

    public void showAutomatic() {
        this.mode.setText("Switch to Manual Mode");
        this.title.setText("Automatic Configuration");
        this.Mode = "Automatic";

        if(listView == null) {
            listView = (ListView) findViewById(R.id.ListView);
            listView.setAdapter(new CustomAdapter(this, AName, ASwitch, ARelay, AState, bluetoothService, shared, editor, "Automatic"));
            ((CustomAdapter) listView.getAdapter()).notifyDataSetChanged();
        }
        else {
            ((CustomAdapter) listView.getAdapter()).updateData(AName, ASwitch, ARelay, AState);
            ((CustomAdapter) listView.getAdapter()).notifyDataSetChanged();
        }
    }
}