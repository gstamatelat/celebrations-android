package gr.james.celebrations;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ContactAdapter extends ArrayAdapter<Contact> {

    public ArrayList<Contact> contacts;
    Context context;
    int layoutResourceId;

    public ContactAdapter(Context context, int layoutResourceId,
                          ArrayList<Contact> contacts) {
        super(context, layoutResourceId, contacts);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.contacts = contacts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            if (position == 0) {
                row.findViewById(R.id.Separator1).setVisibility(View.VISIBLE);
            }

            String yt = "";
            if (contacts.get(position).events) {
                yt = "Σ";
            }
            if (contacts.get(position).giortazei) {
                yt += " Ε";
            }
            ((TextView) row.findViewById(R.id.textView12)).setText(yt);
            ((TextView) row.findViewById(R.id.textView11)).setText(contacts
                    .get(position).displayname);

            row.setTag(contacts.get(position));
        }

        // Weather weather = data[position];
        // holder.txtTitle.setText(weather.title);
        // holder.imgIcon.setImageResource(weather.icon);

        return row;
    }
}
