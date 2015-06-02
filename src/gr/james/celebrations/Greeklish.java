package gr.james.celebrations;

import java.util.HashMap;

public class Greeklish {
	HashMap<String, String> map = new HashMap<String, String>();

	public Greeklish() {
		map.put("TH", "Θ");
		map.put("KS", "Ξ");
		map.put("PS", "Ψ");

		map.put("A", "Α");
		map.put("B", "Β");
		map.put("C", "");
		map.put("D", "Δ");
		map.put("E", "Ε");
		map.put("F", "Φ");
		map.put("G", "Γ");
		map.put("H", "Η");
		map.put("I", "Ι");
		map.put("J", "");
		map.put("K", "Κ");
		map.put("L", "Λ");
		map.put("M", "Μ");
		map.put("N", "Ν");
		map.put("O", "Ο");
		map.put("P", "Π");
		map.put("Q", "");
		map.put("R", "Ρ");
		map.put("S", "Σ");
		map.put("T", "Τ");
		map.put("U", "Υ");
		map.put("V", "Β");
		map.put("W", "Ω");
		map.put("X", "Χ");
		map.put("Y", "Υ");
		map.put("Z", "Ζ");
	}

	public String ConvertToGreek(String greeklish) {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < greeklish.length(); i++) {
			String val1 = map.get(greeklish.substring(i, i + 1));
			String val2 = ((i < greeklish.length() - 1) ? map.get(greeklish
					.substring(i, i + 2)) : null);
			if (val2 != null) {
				builder.append(val2);
				i++;
			} else if (val1 != null) {
				builder.append(val1);
			} else {
				builder.append(greeklish.charAt(i));
			}
		}

		return builder.toString();
	}
}