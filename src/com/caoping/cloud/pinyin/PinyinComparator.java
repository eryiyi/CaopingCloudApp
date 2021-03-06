package com.caoping.cloud.pinyin;



import com.caoping.cloud.entiy.City;

import java.util.Comparator;

public class PinyinComparator implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {
		 City o11 = (City) o1;
		 City o22 = (City) o2;
		 String str1 = PingYinUtil.getPingYin(o11.getCityName());
	     String str2 = PingYinUtil.getPingYin(o22.getCityName());
	     return str1.compareTo(str2);
	}

}
