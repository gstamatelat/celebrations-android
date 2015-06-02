package gr.james.celebrations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CelebrationDate {
	public int day = 0;
	public int month = 0;

	public static CelebrationDate getToday() {
		CelebrationDate aa = new CelebrationDate();
		aa.day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		aa.month = Calendar.getInstance().get(Calendar.MONTH) + 1;
		return aa;
	}

	public CelebrationDate(Calendar c) {
		day = c.get(Calendar.DAY_OF_MONTH);
		month = c.get(Calendar.MONTH) + 1;
	}

	public CelebrationDate() {
	}

	public CelebrationDate(Date d) {
		// ??????????????????????????????????
		if (d != null) {
			this.day = d.getDate();
			this.month = d.getMonth() + 1;
		}
	}

	public CelebrationDate(int day, int month) {
		this.day = day;
		this.month = month;
	}

	public CelebrationDate(String date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date d = null;
		try {
			d = formatter.parse(date.replace("--", "0000-"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (d != null) {
			this.day = d.getDate();
			this.month = d.getMonth() + 1;
		}
	}

	@Override
	public int hashCode() {
		return 37 * month + day;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CelebrationDate other = (CelebrationDate) obj;
		if (day != other.day)
			return false;
		if (month != other.month)
			return false;
		return true;
	}
}