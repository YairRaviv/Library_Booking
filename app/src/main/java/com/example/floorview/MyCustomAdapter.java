package com.example.floorview;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MyCustomAdapter extends BaseAdapter implements ListAdapter {
    private List<Reservation> list;
    private Context context;
    DBConnector dbConnector;
    ProgressDialog progress;

    public MyCustomAdapter(List<Reservation> list, Context context) {
        this.dbConnector = DBConnector.getInstance();
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.my_custom_list_layout, null);
        }
        ReservedObjectType type = list.get(0).reservedObjectType;

        //Handle TextView and display string from the list
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(list.get(position).toString());

        //Handle buttons and add onClickListeners
        Button cancelBtn = (Button)view.findViewById(R.id.cancel_btn);
        Button arrivedBtn = (Button)view.findViewById(R.id.arrived_btn);

        /*
          When user clicks on 'CANCEL' button:
            - The reservation is removed from the db
            - The reservation is removed from the user screen
         */
        cancelBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                progress = new ProgressDialog(v.getContext());
                progress.show();
                progress.setContentView(R.layout.progress_dialog);
                RelativeLayout rl = (RelativeLayout)v.getParent();
                TextView tv = (TextView)rl.findViewById(R.id.list_item_string);
                String text = tv.getText().toString();
                int space_index = text.indexOf(' ');
                int linebreak_index = text.indexOf("\n");
                String reservationId = text.substring(space_index+1,linebreak_index);
                String queryString = "";
                if (type == ReservedObjectType.table) {
                    queryString = "DELETE from Reservations WHERE reservationId = " + reservationId;
                }
                else{
                    queryString = "DELETE from ClassReservations WHERE reservationId = " + reservationId;
                }
                dbConnector.executeUpdate(queryString);
                progress.dismiss();
                list.remove(position); //or some other task
                notifyDataSetChanged();
                Toast.makeText(v.getContext(), "Reservation "+reservationId+" has been canceled", Toast.LENGTH_LONG).show();
            }
        });

        /*
          When user clicks on 'ARRIVED' button:
            - The reservation 'Arrived' column in the db is populated with 'Yes'
            - This button becomes disable
         */
        arrivedBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                arrivedBtn.setEnabled(false);
                notifyDataSetChanged();
                progress = new ProgressDialog(v.getContext());
                progress.show();
                progress.setContentView(R.layout.progress_dialog);
                RelativeLayout rl = (RelativeLayout)v.getParent();
                TextView tv = (TextView)rl.findViewById(R.id.list_item_string);
                String text = tv.getText().toString();
                int space_index = text.indexOf(' ');
                int linebreak_index = text.indexOf("\n");
                String reservationId = text.substring(space_index+1,linebreak_index);
                String queryString = "";
                if (type == ReservedObjectType.table) {
                    queryString = "UPDATE Reservations SET Arrived = 'Yes' WHERE reservationId = " + reservationId;
                }
                else{
                    queryString = "UPDATE ClassReservations SET Arrived = 'Yes' WHERE reservationId = " + reservationId;
                }
                dbConnector.executeUpdate(queryString);
                progress.dismiss();
            }
        });
        return view;
    }
}