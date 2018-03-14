package com.ruanmeng.sort;

import com.ruanmeng.model.CityData;

import java.util.Comparator;

public class PinyinContactComparator implements Comparator<CityData> {

	public int compare(CityData o1, CityData o2) {
		if (o1.getFirstLetter().equals("@")
				|| o2.getLetter().equals("#")) {
			return -1;
		} else if (o1.getLetter().equals("#")
				|| o2.getLetter().equals("@")) {
			return 1;
		} else {
			return o1.getLetter().compareTo(o2.getLetter());
		}
	}

}
