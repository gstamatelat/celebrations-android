package gr.james.celebrations;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends Activity {
    private static String getCurrentDate() {
        Calendar TimeStop = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMM yyyy",
                new Locale("el", "GR"));
        return sdf.format(TimeStop.getTime());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.setStatusBarColor(this.getResources().getColor(R.color.color2));
        window.setNavigationBarColor(this.getResources().getColor(R.color.color2));

        setContentView(R.layout.activity_main);

        if (Store.engine == null) {
            Store.engine = new CelebrationsEngine(this);
        }

        AlarmManagerBroadcastReceiver.SetAlarm(this, false);
    }

	/*
	 * @Override protected void onNewIntent(Intent intent) {
	 * super.onNewIntent(intent); setIntent(intent); }
	 */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        Store.appmenu = menu;
        return true;
    }

    @Override
    public void onResume() {
        super.onResume(); // Always call the superclass method first

        new updateUI(this).execute(null, null, null);
    }

    private void refreshUI1() {
        // Current day
        ((TextView) findViewById(R.id.textView1)).setText(getCurrentDate());

        // Days off
        ArrayList<String> ggo = Store.engine.GetOff(CelebrationDate.getToday());
        if (ggo != null) {
            String tot = "";
            for (int i = 0; i < ggo.size(); i++) {
                tot += ggo.get(i);
                tot += " • ";
            }
            tot = tot.substring(0, tot.length() - 3);
            ((TextView) findViewById(R.id.textView2)).setText(tot);
            findViewById(R.id.textView2).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.textView2).setVisibility(View.GONE);
        }

        // Day names
        ArrayList<String> ggggg = Store.engine.GetNames(CelebrationDate
                .getToday());
        if (ggggg != null) {
            String tot = "";
            for (int i = 0; i < ggggg.size(); i++) {
                tot += ggggg.get(i);
                tot += ", ";
            }
            tot = tot.substring(0, tot.length() - 2);
            ((TextView) findViewById(R.id.textView4)).setText(tot);
        } else {
            ((TextView) findViewById(R.id.textView4))
                    .setText("Καμία ονομαστική γιορτή");
        }

        // Progress
        if (Store.appmenu != null) {
            MenuItem item = Store.appmenu.findItem(R.id.menu_progress);
            item.setVisible(true);
        }
    }

    private ArrayList<Contact> refreshUI2() {
        return AlarmManagerBroadcastReceiver.getCelebrationContacts(this);
    }

    private void refreshUI3(ArrayList<Contact> con) {
        // Progress
        if (Store.appmenu != null) {
            MenuItem item = Store.appmenu.findItem(R.id.menu_progress);
            item.setVisible(false);
        }

        Store.contacts.clear();
        Store.contacts.addAll(con);

        if (Store.contacts.size() == 0) {
            this.findViewById(R.id.textView3).setVisibility(View.VISIBLE);
        } else {
            this.findViewById(R.id.textView3).setVisibility(View.GONE);
        }

        LinearLayout cel = (LinearLayout) this
                .findViewById(R.id.ContactsLinear);
        cel.removeAllViews();
        for (int i = 0; i < Store.contacts.size(); i++) {
            ContactElement celr = new ContactElement(this);
            celr.SetContact(Store.contacts.get(i));
            if (i == 0) {
                celr.SetFirst();
            }
            cel.addView(celr);
        }
    }

    private class updateUI extends
            AsyncTask<MainActivity, Void, ArrayList<Contact>> {

        MainActivity aa;

        public updateUI(MainActivity a) {
            aa = a;
        }

        @Override
        protected void onPreExecute() {
            aa.refreshUI1();
        }

        @Override
        protected ArrayList<Contact> doInBackground(MainActivity... message) {
            return aa.refreshUI2();
        }

        @Override
        protected void onPostExecute(ArrayList<Contact> b) {
            aa.refreshUI3(b);
        }
    }

}