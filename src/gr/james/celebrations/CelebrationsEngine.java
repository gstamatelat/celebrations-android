package gr.james.celebrations;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

public class CelebrationsEngine {
	public static class CelebrationsDatabase {
		public HashMap<CelebrationDate, ArrayList<String>> all = new HashMap<CelebrationDate, ArrayList<String>>();
		public HashMap<CelebrationDate, ArrayList<String>> off = new HashMap<CelebrationDate, ArrayList<String>>();
	}

	private Context context = null;
	private CelebrationsDatabase db = null;
	private Greeklish greeklish = new Greeklish();
	private int currentYear = 0;

	private CelebrationDate cacheDay = null;
	private HashMap<String, Boolean> cache = new HashMap<String, Boolean>();

	public CelebrationsEngine(Context context) {
		this.context = context;
		ResetCache();
	}

	public void ResetCache() {
		if (!CelebrationDate.getToday().equals(cacheDay)) {
			cacheDay = CelebrationDate.getToday();
			cache.clear();
		}
		rebuildDatabase();
	}

	public static Calendar GetOrthodoxEaster(int myear) {
		Calendar dof = Calendar.getInstance();

		int r1 = myear % 4;
		int r2 = myear % 7;
		int r3 = myear % 19;
		int r4 = (19 * r3 + 15) % 30;
		int r5 = (2 * r1 + 4 * r2 + 6 * r4 + 6) % 7;
		int mdays = r5 + r4 + 13;

		if (mdays > 39) {
			mdays = mdays - 39;
			dof.set(myear, 4, mdays);
		} else if (mdays > 9) {
			mdays = mdays - 9;
			dof.set(myear, 3, mdays);
		} else {
			mdays = mdays + 22;
			dof.set(myear, 2, mdays);
		}
		return dof;
	}

	private void rebuildDatabase() {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		if (currentYear == year) {
			return;
		}

		currentYear = year;
		db = new CelebrationsDatabase();
		BufferedReader bufferedReader = null;
		try {
			InputStream inStream = context.getAssets().open("celeb.json");

			BufferedInputStream bufferedStream = new BufferedInputStream(
					inStream);
			InputStreamReader reader = new InputStreamReader(bufferedStream);
			bufferedReader = new BufferedReader(reader);
			StringBuilder builder = new StringBuilder();
			String line = bufferedReader.readLine();
			while (line != null) {
				builder.append(line);
				line = bufferedReader.readLine();
			}

			JSONObject jso = new JSONObject(builder.toString());

			// Static
			JSONArray jsonArray = (JSONArray) jso.get("names");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject current = (JSONObject) (jsonArray.get(i));
				JSONArray currentNames = (JSONArray) current.get("names");
				CelebrationDate dbi = new CelebrationDate();
				dbi.day = Integer.parseInt(current.get("day").toString());
				dbi.month = Integer.parseInt(current.get("month").toString());
				ArrayList<String> dateNames = new ArrayList<String>();
				for (int j = 0; j < currentNames.length(); j++) {
					dateNames.add(currentNames.get(j).toString());
				}
				db.all.put(dbi, dateNames);
			}

			// Moving
			jsonArray = (JSONArray) jso.get("moving");
			Calendar orthodoxEaster = GetOrthodoxEaster(currentYear);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject current = (JSONObject) (jsonArray.get(i));
				JSONArray currentNames = (JSONArray) current.get("names");
				int currentDays = Integer.parseInt(current.get("days")
						.toString());
				Calendar tmpCal = ((Calendar) orthodoxEaster.clone());
				tmpCal.add(Calendar.DATE, currentDays);
				CelebrationDate dbi = new CelebrationDate(tmpCal);
				ArrayList<String> dateNames = new ArrayList<String>();
				for (int j = 0; j < currentNames.length(); j++) {
					dateNames.add(currentNames.get(j).toString());
				}
				addToMap(dbi, dateNames);
			}

			// Moving Special
			ArrayList<String> ggggg1 = new ArrayList<String>();
			ggggg1.add("ΧΛΟΗ");
			Calendar curCal = Calendar.getInstance();
			curCal.set(Calendar.DAY_OF_MONTH, 13);
			curCal.set(Calendar.MONTH, 1);
			int cdow = curCal.get(Calendar.DAY_OF_WEEK);
			if (cdow != Calendar.SUNDAY) {
				curCal.add(Calendar.DAY_OF_MONTH, 7 - cdow + Calendar.SUNDAY);
			}
			addToMap(new CelebrationDate(curCal), ggggg1);

			// Moving Special
			ArrayList<String> ggggg2 = new ArrayList<String>(
					Arrays.asList("ΑΑΡΩΝ,ΑΒΡΑΑΜ,ΑΒΡΑΜΙΑ,ΑΔΑΜ,ΑΔΑΜΑΝΤΙΟΣ,ΑΔΑΜΟΣ,ΑΔΑΜΗΣ,ΑΔΑΜΑΣ,ΔΙΑΜΑΝΤΗΣ,ΑΔΑΜΑΝΤΙΑ,ΑΜΑΝΤΑ,ΑΝΤΑ,ΔΙΑΜΑΝΤΟΥΛΑ,ΔΙΑΜΑΝΤΩ,ΕΥΑ,ΔΑΒΙΔ,ΔΑΥΙΔ,ΔΑΝΑΗ,ΔΑΝ,ΔΕΒΟΡΑ,ΔΕΒΩΡΑ,ΝΤΕΜΠΟΡΑ,ΝΤΕΠΥ,ΕΣΘΗΡ,ΜΕΛΧΙΣΕΔΕΧ,ΜΕΛΧΗΣ,ΝΩΕ,ΡΑΧΗΛ,ΡΕΒΕΚΚΑ,ΜΠΕΚΥ,ΡΟΥΜΠΙΝΙ,ΡΟΥΜΠΙΝΗ,ΡΟΥΜΠΕΝ,ΡΟΥΜΠΙΝΑ,ΣΑΡΑ,ΣΑΡΡΑ,ΙΣΑΑΚ,ΙΩΒ,ΙΩΒΙΑ,ΙΩΒΗ"
							.split(",")));
			curCal = Calendar.getInstance();
			curCal.set(Calendar.DAY_OF_MONTH, 11);
			curCal.set(Calendar.MONTH, 11);
			int cdow2 = curCal.get(Calendar.DAY_OF_WEEK);
			if (cdow2 != Calendar.SUNDAY) {
				curCal.add(Calendar.DAY_OF_MONTH, 7 - cdow2 + Calendar.SUNDAY);
			}
			addToMap(new CelebrationDate(curCal), ggggg2);

			// Moving Special
			ArrayList<String> ggggg3 = new ArrayList<String>(
					Arrays.asList("ΓΕΩΡΓΙΟΣ,ΓΕΩΡΓΗΣ,ΓΙΩΡΓΟΣ,ΓΕΩΡΓΙΑ,ΓΙΩΡΓΙΑ,ΓΕΩΡΓΟΥΛΑ,ΓΩΓΩ"
							.split(",")));
			Calendar normalDay = Calendar.getInstance();
			normalDay.set(Calendar.DAY_OF_MONTH, 23);
			normalDay.set(Calendar.MONTH, 3);
			if (orthodoxEaster.get(Calendar.DAY_OF_YEAR) >= normalDay
					.get(Calendar.DAY_OF_YEAR)) {
				normalDay = (Calendar) orthodoxEaster.clone();
				normalDay.add(Calendar.DATE, 1);
			}
			addToMap(new CelebrationDate(normalDay), ggggg3);

			// Moving Special
			ArrayList<String> ggggg4 = new ArrayList<String>(
					Arrays.asList("ΜΑΡΚΟΣ,ΜΑΡΚΙΑ,ΜΑΡΚΟΥΛΗΣ,ΜΑΡΚΟΥΛΑ".split(",")));
			normalDay = Calendar.getInstance();
			normalDay.set(Calendar.DAY_OF_MONTH, 25);
			normalDay.set(Calendar.MONTH, 3);
			if (orthodoxEaster.get(Calendar.DAY_OF_YEAR) > normalDay
					.get(Calendar.DAY_OF_YEAR) - 2) {
				normalDay = (Calendar) orthodoxEaster.clone();
				normalDay.add(Calendar.DATE, 2);
			}
			addToMap(new CelebrationDate(normalDay), ggggg4);

			// Days Off
			addToOff(new CelebrationDate(1, 1), "Πρωτοχρονιά");
			addToOff(new CelebrationDate(25, 12), "Χριστούγεννα");
			addToOff(new CelebrationDate(6, 1), "Θεοφάνεια");
			addToOff(new CelebrationDate(1, 5), "Πρωτομαγιά");
			addToOff(new CelebrationDate(25, 3), "25η Μαρτίου");
			addToOff(new CelebrationDate(15, 8), "Κοίμηση της Θεοτόκου");
			addToOff(new CelebrationDate(28, 10), "28η Οκτωβρίου");

			Calendar allCelebrate = (Calendar) orthodoxEaster.clone();
			allCelebrate.add(Calendar.DATE, -59);
			addToOff(new CelebrationDate(allCelebrate), "Τσικνοπέμπτη");

			allCelebrate = (Calendar) orthodoxEaster.clone();
			allCelebrate.add(Calendar.DATE, -48);
			addToOff(new CelebrationDate(allCelebrate), "Καθαρά Δευτέρα");

			allCelebrate = (Calendar) orthodoxEaster.clone();
			allCelebrate.add(Calendar.DATE, -49);
			addToOff(new CelebrationDate(allCelebrate),
					"Κυριακή της Αποκριάς");

			allCelebrate = (Calendar) orthodoxEaster.clone();
			allCelebrate.add(Calendar.DATE, -6);
			addToOff(new CelebrationDate(allCelebrate), "Μεγάλη Δευτέρα");

			allCelebrate = (Calendar) orthodoxEaster.clone();
			allCelebrate.add(Calendar.DATE, -5);
			addToOff(new CelebrationDate(allCelebrate), "Μεγάλη Τρίτη");

			allCelebrate = (Calendar) orthodoxEaster.clone();
			allCelebrate.add(Calendar.DATE, -4);
			addToOff(new CelebrationDate(allCelebrate), "Μεγάλη Τετάρτη");

			allCelebrate = (Calendar) orthodoxEaster.clone();
			allCelebrate.add(Calendar.DATE, -3);
			addToOff(new CelebrationDate(allCelebrate), "Μεγάλη Πέμπτη");

			allCelebrate = (Calendar) orthodoxEaster.clone();
			allCelebrate.add(Calendar.DATE, -2);
			addToOff(new CelebrationDate(allCelebrate), "Μεγάλη Παρασκευή");

			allCelebrate = (Calendar) orthodoxEaster.clone();
			allCelebrate.add(Calendar.DATE, -1);
			addToOff(new CelebrationDate(allCelebrate), "Μεγάλο Σάββατο");
			
			addToOff(new CelebrationDate(orthodoxEaster), "Πάσχα");

			allCelebrate = (Calendar) orthodoxEaster.clone();
			allCelebrate.add(Calendar.DATE, 49);
			addToOff(new CelebrationDate(allCelebrate), "Πεντηκοστή");

			allCelebrate = (Calendar) orthodoxEaster.clone();
			allCelebrate.add(Calendar.DATE, 50);
			addToOff(new CelebrationDate(allCelebrate), "Του Αγίου Πνεύματος");
			
			allCelebrate = (Calendar) orthodoxEaster.clone();
			allCelebrate.add(Calendar.DATE, 56);
			addToOff(new CelebrationDate(allCelebrate), "Αγίων Πάντων");

			//addToOff(CelebrationDate.getToday(), "Some test day");
			//addToOff(CelebrationDate.getToday(), "Some more test day");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void addToMap(CelebrationDate d, ArrayList<String> names) {
		if (!db.all.containsKey(d)) {
			db.all.put(d, names);
		} else {
			db.all.get(d).addAll(names);
		}
	}
	
	private void addToOff(CelebrationDate d, String name){
		if (!db.off.containsKey(d)) {
			ArrayList<String> rere = new ArrayList<String>();
			rere.add(name);
			db.off.put(d, rere);
		} else {
			db.off.get(d).add(name);
		}
	}

	public ArrayList<String> GetNames(CelebrationDate d) {
		return db.all.get(d);
	}

	public ArrayList<String> GetOff(CelebrationDate d) {
		return db.off.get(d);
	}

	public static String ToUpper(String str) {
		str = str.toUpperCase(new Locale("el", "GR"));
		String nfdNormalizedString = Normalizer.normalize(str,
				Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(nfdNormalizedString).replaceAll("");
	}

	public static double GetDistance(CharSequence cSeq1, CharSequence cSeq2) {
		JaroWinklerDistance g = new JaroWinklerDistance();
		return g.distance(cSeq1, cSeq2);
	}

	private double getMinimumDistance(String name, ArrayList<String> names) {
		double minDistance = Double.POSITIVE_INFINITY;

		for (Iterator<String> i = names.iterator(); i.hasNext();) {
			String current = i.next();
			double currentDistance = GetDistance(name, current);
			if (currentDistance < minDistance) {
				minDistance = currentDistance;
			}
		}

		return minDistance;
	}

	public boolean IsNameToday(String name) {
		if (name == null) {
			return false;
		}
		
		if (name.equals("Netdirectbargains")) {
			System.out.println();
		}

		Boolean tmpB = cache.get(name);
		if (tmpB != null) {
			return tmpB.booleanValue();
		}

		String name0 = ToUpper(name);
		name0 = greeklish.ConvertToGreek(name0);

		ArrayList<String> todayNames = db.all.get(CelebrationDate.getToday());
		if (todayNames == null) {
			cache.put(name, Boolean.valueOf(false));
			return false;
		}

		double todayDistance = getMinimumDistance(name0, todayNames);

		if (todayDistance > 0.25) {
			cache.put(name, Boolean.valueOf(false));
			return false;
		}

		for (ArrayList<String> value : db.all.values()) {
			if (getMinimumDistance(name0, value) < todayDistance) {
				cache.put(name, Boolean.valueOf(false));
				return false;
			}
		}

		cache.put(name, Boolean.valueOf(true));
		return true;
	}

	public ArrayList<Contact> GetCelebrations(ArrayList<Contact> contacts) {
		ResetCache();

		ArrayList<Contact> fulllist = new ArrayList<Contact>();
		for (Iterator<Contact> j = contacts.iterator(); j.hasNext();) {
			Contact contact = j.next();
			if (IsNameToday(contact.firstname)) {
				contact.giortazei = true;
			}
			if (contact.giortazei || contact.events) {
				fulllist.add(contact);
			}
		}

		return fulllist;
	}

}