package com.example.floorview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

public class RequestsAdapter extends BaseAdapter implements ListAdapter
{
    private List<ClassRequest> list;
    private Context context;
    DBConnector dbConnector;
    ProgressDialog progress;

    public RequestsAdapter(List<ClassRequest> list, Context context)
    {
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
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.requests_layout, null);
        }

        //Handle TextView and display string from the list
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string2);
        listItemText.setText(list.get(position).toString());

        //Handle buttons and add onClickListeners
        Button RejectBtn = (Button)view.findViewById(R.id.RejectBtn);
        Button ApproveBtn = (Button)view.findViewById(R.id.ApproveBtn);
        Button CallStudent = (Button)view.findViewById(R.id.CallToStudent);

        /*
          When user clicks on 'CANCEL' button:
            - The reservation is removed from the db
            - The reservation is removed from the user screen
         */
        RejectBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                progress = new ProgressDialog(v.getContext());
                progress.show();
                progress.setContentView(R.layout.progress_dialog);
                LinearLayout rl = (LinearLayout)v.getParent();
                TextView tv = (TextView)rl.findViewById(R.id.list_item_string2);
                String text = tv.getText().toString();
                int space_index = text.indexOf(' ');
                int linebreak_index = text.indexOf("\n");
                String reservationId = text.substring(space_index+1,linebreak_index);

                String queryString = "DELETE from ClassRequests WHERE ReservationID = "+ reservationId;
                dbConnector.executeUpdate(queryString);
                String queryString2 = "DELETE from ClassReservations WHERE ReservationID = "+ reservationId;
                dbConnector.executeUpdate(queryString2);
                progress.dismiss();
                list.remove(position); //or some other task
                notifyDataSetChanged();
                Toast.makeText(v.getContext(), "Reservation "+reservationId+" has been Rejected", Toast.LENGTH_LONG).show();
            }
        });

        /*
          When user clicks on 'ARRIVED' button:
            - The reservation 'Arrived' column in the db is populated with 'Yes'
            - This button becomes disable
         */
        ApproveBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                ApproveBtn.setEnabled(false);
                notifyDataSetChanged();
                progress = new ProgressDialog(v.getContext());
                progress.show();
                progress.setContentView(R.layout.progress_dialog);
                LinearLayout rl = (LinearLayout)v.getParent();
                TextView tv = (TextView)rl.findViewById(R.id.list_item_string2);
                String text = tv.getText().toString();
                int space_index = text.indexOf(' ');
                int linebreak_index = text.indexOf("\n");
                String reservationId = text.substring(space_index+1,linebreak_index);
                String queryString = "UPDATE ClassReservations SET Status = 'Approved' WHERE ReservationID = "+ reservationId;
                dbConnector.executeUpdate(queryString);
                String queryString2 = "DELETE from ClassRequests WHERE ReservationID = "+ reservationId;
                dbConnector.executeUpdate(queryString2);
                list.remove(position);
                progress.dismiss();
            }
        });
        CallStudent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //CallStudent.setEnabled(false);
                //notifyDataSetChanged();
                progress = new ProgressDialog(v.getContext());
                progress.show();
                progress.setContentView(R.layout.progress_dialog);
                LinearLayout rl = (LinearLayout)v.getParent();
                TextView tv = (TextView)rl.findViewById(R.id.list_item_string2);
                String text = tv.getText().toString();
                int end = text.length();
                int start = text.length()-10;
                String PhoneNumber = text.substring(start,end);
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+PhoneNumber));
                context.startActivity(callIntent);
                progress.dismiss();
            }
        });
        return view;
    }
}
