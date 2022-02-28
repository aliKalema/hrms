package com.smartmax.hrms.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Random;

import com.smartmax.hrms.entity.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.smartmax.hrms.repository.CategoryRepository;
import com.smartmax.hrms.repository.CenterRepository;
import com.smartmax.hrms.repository.DepartmentRepository;
import com.smartmax.hrms.repository.EmployeeRepository;
import com.smartmax.hrms.repository.GradeRepository;
import com.smartmax.hrms.repository.JobTitleRepository;
import com.smartmax.hrms.repository.SectionRepository;
import com.smartmax.hrms.repository.TeamRepository;


public class SystemUtils {
	public SystemUtils() {
		throw new RuntimeException("YOU CAN NOT INSTANTIATE ME");
	}

	public static LocalDate convertDateFormat(String dateString){
		LocalDate localDate = null;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			localDate = LocalDate.parse(dateString, formatter);
		}catch (Exception e){e.printStackTrace();}
		return localDate;
	}
	public static LocalDate convertDateFormat(String dateString,String format){
		LocalDate localDate =null;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
			 localDate = LocalDate.parse(dateString, formatter);
		}catch(Exception e){e.printStackTrace();}
		return localDate;
	}
	public static String styleName(String name) {
		String firstLetter = name.substring(0 ,1);
		String remLetters = name.substring(1);
		String str = firstLetter.toUpperCase() + remLetters.toLowerCase();
		String[] otherNames = str.split(" ");
		if(otherNames.length<=1) {
			return str;
		}
		String string="";
		for(int i=0;i<otherNames.length;i++) {
			String FL = otherNames[i].substring(0,1);
			String RM = otherNames[i].substring(1);
			string = string + " "+ FL.toUpperCase() + RM.toLowerCase();
		}
		return string;
	}
	public static String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }
	public static void getName(Employee employee) {
		String name = employee.getLastName() + " "+ employee.getFirstName() +" " +employee.getOtherNames();
		employee.setName(name);
	}
	public static boolean isStringNull(String str) {
		boolean isNull = false;
		if(str.equals("null")) {
			isNull = true;
		}
		return isNull;
	}
	public static int getRotationDays(String rotation){
		int days = 0;
		switch(rotation){
			case "weekly":
				days = 7;
				break;
			case "monthly":
				break;
			case "annually":
				days = 365;
				break;
		}
		return days;
	}
	public static boolean isSunday(final LocalDate ld) {
		DayOfWeek day = DayOfWeek.of(ld.get(ChronoField.DAY_OF_WEEK));
		return day == DayOfWeek.SUNDAY;
	}

    public static void setName(Employee employee) {
		String str[] =  employee.getName().split(" ");
		String otherNames = "";
		employee.setFirstName(str[0]);
		employee.setLastName(str[str.length-1]);
		for(int i = 1;i<str.length-1;i++){
				otherNames =  otherNames + str[i]+" ";
		}
		employee.setOtherNames(otherNames);
    }

	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();
		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
}
