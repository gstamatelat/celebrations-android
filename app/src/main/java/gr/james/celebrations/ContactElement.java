package gr.james.celebrations;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactElement extends LinearLayout implements OnClickListener,
        OnTouchListener {

    private Contact contact = null;

    public ContactElement(Context context) {
        super(context);
        this.setOrientation(VERTICAL);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Button btn1 = (Button) view.findViewById(R.id.button1);
        // btn1.setOnClickListener(this);

        // LayoutParams params = (LayoutParams) this.getLayoutParams();
        // Changes the height and width to the specified *pixels*
        // params.height = 100;
        // params.width = 100;

        // and so on for the rest of the buttons

        // addView(view);

        View view = inflater.inflate(R.layout.list_fragment, null);
        this.addView(view);

        this.invalidate();
        this.setOnTouchListener(this);
        this.setOnClickListener(this);
    }

    public void SetFirst() {
        this.findViewById(R.id.Separator1).setVisibility(View.VISIBLE);
    }

    public void SetContact(Contact c) {
        this.contact = c;
        ((TextView) this.findViewById(R.id.textView11)).setText(contact.displayname);
        String yt = "";
        if (contact.events) {
            yt = "Σ";
        }
        if (contact.giortazei) {
            yt += " Ε";
        }
        ((TextView) this.findViewById(R.id.textView12)).setText(yt);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.setBackgroundColor(Color.rgb(100, 120, 140));
                break;
            case MotionEvent.ACTION_UP:
                v.setBackgroundColor(Color.TRANSPARENT);
                break;
            case MotionEvent.ACTION_CANCEL:
                v.setBackgroundColor(Color.TRANSPARENT);
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (contact != null) {
            Uri contactUri = ContentUris.withAppendedId(
                    ContactsContract.Contacts.CONTENT_URI,
                    Long.valueOf(contact.id));
            Intent intent = new Intent(Intent.ACTION_VIEW, contactUri);
            v.getContext().startActivity(intent);
            // this.startActivity(intent);
        }
    }

}
