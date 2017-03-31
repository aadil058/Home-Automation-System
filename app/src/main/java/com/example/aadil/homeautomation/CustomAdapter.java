package com.example.aadil.homeautomation;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;
import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> Name;
    private ArrayList<Switch> Switch;
    private ArrayList<Integer> Relay;
    private ArrayList<Boolean> State;
    private BluetoothService bluetoothService;
    private SharedPreferences shared;
    private SharedPreferences.Editor editor;
    private String Mode;

    private static LayoutInflater inflater = null;

    @Override
    public int getCount() {
        return Name.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView Name;
        Switch Switch;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder = new Holder();
        View row = inflater.inflate(R.layout.row, null);

        holder.Name = (TextView) row.findViewById(R.id.textview);
        holder.Switch = (Switch) row.findViewById(R.id.Switch);

        holder.Name.setText(this.Name.get(position));

        if(Switch.get(position).isChecked())
            holder.Switch.setChecked(true);

        System.out.println(Switch.get(position).isChecked());

        holder.Switch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!State.get(position)) {
                    State.set(position, true);
                    ((Switch) v.findViewById(R.id.Switch)).setChecked(true);

                    if(Mode.equals("Automatic")) {
                        editor.putBoolean("AState" + position, true);
                        editor.commit();
                    }

                    if(((position + 1) == 1) && (bluetoothService.isConnected()))
                        bluetoothService.write("0");

                    if(((position + 1) == 2) && (bluetoothService.isConnected()))
                        bluetoothService.write("2");

                    if(((position + 1) == 3) && (bluetoothService.isConnected()))
                        bluetoothService.write("4");

                    if(((position + 1) == 4) && (bluetoothService.isConnected()))
                        bluetoothService.write("6");
                }
                else {
                    State.set(position, false);
                    ((Switch) v.findViewById(R.id.Switch)).setChecked(false);

                    if(Mode.equals("Automatic")) {
                        editor.putBoolean("AState" + position, false);
                        editor.commit();
                    }

                    bluetoothService.write("9");

                    if(((position + 1) == 1) && (bluetoothService.isConnected()))
                        bluetoothService.write("1");

                    if(((position + 1) == 2) && (bluetoothService.isConnected()))
                        bluetoothService.write("3");

                    if(((position + 1) == 3) && (bluetoothService.isConnected()))
                        bluetoothService.write("5");

                    if(((position + 1) == 4) && (bluetoothService.isConnected()))
                        bluetoothService.write("7");
                }
            }
        });

        return row;
    }

    public void updateData(ArrayList<String> Name, ArrayList<Switch> Switch, ArrayList<Integer> Relay, ArrayList<Boolean> State) {
        this.Name = Name;
        this.Switch = Switch;
        this.Relay = Relay;
        this.State = State;
    }

    public void update() {
        for(int i = 0; i < State.size(); ++i) {
            if(State.get(i)) bluetoothService.write("1");
            else    bluetoothService.write("0");
        }
    }

    public CustomAdapter(Preconfig context, ArrayList<String> Name, ArrayList<Switch> Switch, ArrayList<Integer> Relay,
                         ArrayList<Boolean> State, BluetoothService bluetoothService, SharedPreferences shared,
                         SharedPreferences.Editor editor, String Mode) {
        this.context = context;
        this.Name = Name;
        this.Switch = Switch;
        this.Relay = Relay;
        this.State = State;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.bluetoothService = bluetoothService;
        this.shared = shared;
        this.editor = editor;
        this.Mode = Mode;
    }
}