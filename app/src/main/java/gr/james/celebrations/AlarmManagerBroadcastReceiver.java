package gr.james.celebrations;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    public static void SetAlarm(Context context, boolean boot) {
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        // intent.putExtra(ONE_TIME, Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        // After after 30 seconds

        Calendar mine = Calendar.getInstance();
        mine.set(Calendar.HOUR_OF_DAY, 12);
        mine.set(Calendar.MINUTE, 0);
        mine.set(Calendar.SECOND, 0);
        mine.set(Calendar.MILLISECOND, 0);
        if (mine.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
            if (!boot) {
                mine.add(Calendar.DATE, 1);
            }
        }
        if (boot) {
            am.set(AlarmManager.RTC_WAKEUP, mine.getTimeInMillis(), pi);
        } else {
            am.setRepeating(AlarmManager.RTC_WAKEUP, mine.getTimeInMillis(),
                    60000 * 24, pi);
        }
    }

    public static ArrayList<Contact> getCelebrationContacts(Context context) {

        int gggggggg = context.getResources().getInteger(R.integer.contacts_read_permission);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    context.getResources().getInteger(R.integer.contacts_read_permission));
            return new ArrayList<>();
        }

        CelebrationDate today = CelebrationDate.getToday();
        if (Store.engine == null) {
            Store.engine = new CelebrationsEngine(context);
        }

        Store.engine.ResetCache();

        // Build adapter with contact entries
        ContentResolver cr = context.getContentResolver();

        Uri uri = ContactsContract.Data.CONTENT_URI;

        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID,
                ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME};

        String where = ContactsContract.Data.MIMETYPE + "= ?";

        String[] selectionArgs = new String[]{ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};

        String[] projectionEvent = new String[]{
                ContactsContract.CommonDataKinds.Event.TYPE,
                ContactsContract.CommonDataKinds.Event.START_DATE};

        String whereEvent = ContactsContract.Data.MIMETYPE + "= ? AND "
                + ContactsContract.CommonDataKinds.Event.CONTACT_ID + "= ?";

        String[] selectionArgsEvent = new String[]{
                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE, ""};

        String sortOrder = null;

        Cursor cursor = cr.query(uri, projection, where, selectionArgs, sortOrder);

        ArrayList<Contact> contacts = new ArrayList<>();
        while (cursor.moveToNext()) {
            String id = cursor
                    .getString(cursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID));
            String firstname = cursor
                    .getString(cursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
            String displayname = cursor
                    .getString(cursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));

            // Use Eortologio
            // HashSet<CelebrationsDate> resutls =
            // ce.GetCelebrationDateByName(firstname, year);

            Contact tmp = new Contact(id, firstname, displayname);
            // if(resutls.contains(today)) tmp.setGiortazei(true);

            selectionArgsEvent[1] = id;
            Cursor pCur = cr.query(uri, projectionEvent, whereEvent,
                    selectionArgsEvent, sortOrder);
            while (pCur.moveToNext()) {
                String date = pCur
                        .getString(pCur
                                .getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
                CelebrationDate cd = new CelebrationDate(date);
                if (cd.equals(today)) {
                    tmp.events = true;
                    break;
                }
            }
            pCur.close();

            // //////////////////////////////////////////////
            // if(tmp.EventsSize() > 0 || tmp.isGiortazei()){
            if (Store.engine.IsNameToday(tmp.firstname)) {
                tmp.giortazei = true;
            }

            if (tmp.giortazei || tmp.events) {
                contacts.add(tmp);
            }
            // break;
            // }
            // /////////////////////////////////////////////
        }
        cursor.close();

        // return contacts;
        return contacts;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // PowerManager pm = (PowerManager) context
        // .getSystemService(Context.POWER_SERVICE);
        // PowerManager.WakeLock wl = pm.newWakeLock(
        // PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        // Acquire the lock
        // wl.acquire();


        ArrayList<Contact> cc = getCelebrationContacts(context);

        if (cc.size() > 0) {
            // ///////////////////////////////////////////////////////////////
            /*Intent notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);*/

            Intent notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            String sss;
            if (cc.size() > 1) {
                sss = String.format(context.getResources().getString(R.string.multiple_celebration), cc.size());
            } else if (cc.size() == 1) {
                sss = context.getResources().getString(R.string.one_celebration);
            } else {
                sss = context.getResources().getString(R.string.no_celebration);
            }

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_launcher_white)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(sss)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setAutoCancel(true).setContentIntent(contentIntent)
                    .setLights(0xFFFFFF, 1000, 10000).setOngoing(true);

            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, mBuilder.build());
            // ////////////////////////////////////////////////////////////////
        }

        SetAlarm(context, false);

        // Release the lock
        // wl.release();
    }

}
