package com.smartmax.hrms.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.smartmax.hrms.service.EmployeeService;
import com.smartmax.hrms.service.ReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.smartmax.hrms.entity.Employee;
import com.smartmax.hrms.entity.Reminder;
import com.smartmax.hrms.entity.User;
import com.smartmax.hrms.quartz.JobData;
import com.smartmax.hrms.repository.EmployeeRepository;
import com.smartmax.hrms.repository.ReminderRepository;
import com.smartmax.hrms.repository.UserRepository;
import com.smartmax.hrms.utils.SystemUtils;

@RestController
public class ReminderController {
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	EmployeeRepository employeeRepository;

	@Autowired
	ReminderRepository reminderRepository;
	
	@Autowired
	ReminderService reminderService;

	@Autowired
	EmployeeService employeeService;
	
	private DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

	@PostMapping("/api/admin/reminders")
	public ResponseEntity<Reminder>addReminder(@RequestParam("description")String message,
											   @RequestParam("reminder-date")String date,
											   @RequestParam("reminder-time")String time,
											   Authentication authentication){
		String datetime = date + " " + time;
		LocalDateTime localDateTime = LocalDateTime.parse(datetime,format);
		User user = userRepository.findByUsername(authentication.getName()).get();
		Reminder  reminder = new Reminder();
		String jobName = SystemUtils.getSaltString() + new Date().getTime() + localDateTime.format(format);
		reminder.setJobName(jobName);
		reminder.setMessage(message);
		reminder.setDateTime(localDateTime);
		reminder.setUser(user);
		reminder.setDate(reminder.getDateTime().toLocalDate());
		reminder.setTime(reminder.getDateTime().toLocalTime());
		String day  = reminder.getDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
		reminder.setDay(day);
		reminderService.addReminder(reminder);
		return new ResponseEntity<>(reminder,HttpStatus.OK);
	}
	@GetMapping("/api/admin/reminders")
	public ResponseEntity<List<Reminder>>getReminders(Authentication authentication){
		return null;
	}

	@GetMapping("/api/admin/reminders/delete/{id}")
	public ResponseEntity<?>deleteReminder(@PathVariable("id")String id){
		Reminder reminder =  reminderRepository.findById(Integer.parseInt(id)).get();
		reminderService.deleteReminder(reminder);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/api/admin/reminders/edit/{id}")
	public ResponseEntity<?>addReminder(@RequestParam("description")String message,
										@RequestParam("reminder-date")String date,
										@RequestParam("reminder-time")String time,
										@PathVariable("id")String id,
										Authentication authentication){
		User user=  userRepository.findByUsername(authentication.getName()).get();
		String datetime = date + " " + time;
		System.out.println(datetime);
		Reminder reminder = reminderRepository.findById(Integer.parseInt(id)).get();
		LocalDateTime localDateTime = LocalDateTime.parse(datetime,DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		reminder.setDateTime(localDateTime);
		reminder.setMessage(message);
		reminder.setUser(user);
		reminder.setDate(reminder.getDateTime().toLocalDate());
		reminder.setTime(reminder.getDateTime().toLocalTime());
		String day  = reminder.getDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
		reminder.setDay(day);
		reminderService.editReminder(reminder);
		return new ResponseEntity<>(reminder,HttpStatus.OK);
	}

	@GetMapping("/admin/reinder/receceived/{id}")
	public void setReminderReceived(@PathVariable("id")String id){
		 reminderRepository.deleteById(Integer.parseInt(id));
	}

}
