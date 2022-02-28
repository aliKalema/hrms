package com.smartmax.hrms.service;

import com.smartmax.hrms.entity.Employee;
import com.smartmax.hrms.entity.Reminder;
import com.smartmax.hrms.entity.User;
import com.smartmax.hrms.entity.UserImage;
import com.smartmax.hrms.quartz.JobData;
import com.smartmax.hrms.quartz.ReminderJob;
import com.smartmax.hrms.repository.EmployeeRepository;
import com.smartmax.hrms.repository.ReminderRepository;
import com.smartmax.hrms.repository.UserRepository;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class ReminderService {
    @Autowired
    ReminderRepository reminderRepository;

    @Autowired
    Scheduler scheduler;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public ZonedDateTime getZonedDateTime(LocalDateTime localDateTime){
        return ZonedDateTime.of(localDateTime, ZoneId.of("Africa/Addis_Ababa"));
    }
    public void editReminder(Reminder reminder){
        JobKey jobKey  = new JobKey(reminder.getJobName(),"reminders");
        try {
            Trigger oldTrigger = scheduler.getTrigger(new TriggerKey(reminder.getJobName(),"reminders"));
            ZonedDateTime zonedDateTime = this.getZonedDateTime(reminder.getDateTime());
            Trigger newTrigger = TriggerBuilder.newTrigger().withIdentity(reminder.getJobName(), "reminders")
                    .forJob(reminder.getJobName(), "reminders")
                    .startAt(Date.from(zonedDateTime.toInstant()))
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                    .build();
            scheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);
            reminderRepository.save(reminder);
        }
        catch(SchedulerException e){
            e.printStackTrace();
        }
    }
    public void deleteReminder(Reminder reminder){
        String jobName =  reminder.getJobName();
        String jobGroup = "reminders";
        JobKey jobKey  = new JobKey(jobName,jobGroup);
        try{
            scheduler.deleteJob(jobKey);
            reminderRepository.deleteById(reminder.getId());
        }catch(SchedulerException se){
            se.printStackTrace();
        }
    }
    public void addReminder(Reminder reminder){
        JobData jobData = new JobData();
        jobData.setJobName(reminder.getJobName());
        jobData.setJobGroup("reminders");
        jobData.setStartTime(reminder.getDateTime());
        ZonedDateTime zonedDateTime = this.getZonedDateTime(jobData.getStartTime());
        JobDetail jobDetail = JobBuilder.newJob(ReminderJob.class)
                .withIdentity(jobData.getJobName(), jobData.getJobGroup()).usingJobData("reminderJobName",reminder.getJobName())
                .storeDurably(false)
                .build();
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobData.getJobName(), jobData.getJobGroup())
                .forJob(jobData.getJobName(), jobData.getJobGroup())
                .startAt(Date.from(zonedDateTime.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                .build();
        try {
            scheduler.scheduleJob(jobDetail,trigger);
            reminderRepository.save(reminder);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
    public void setUnReceivedReminders(ModelAndView mav, Authentication auth){
        List<Reminder>unreceivedReminders =  new ArrayList<>();
        User user= userRepository.findByUsername(auth.getName()).get();
        Optional<Employee> employee=  employeeRepository.findById(user.getEmployeeId());
        employee.ifPresent(user::setEmployee);
        if(user.getEmployee() == null){
            user.setEmployee(new Employee(new UserImage("default-avatar.png")));
        }
        List<Reminder>allReminders = reminderRepository.findByUserId(user.getId());
        for(Reminder reminder : allReminders){
            if(reminder.isTriggered()){
                if(reminder.isReceived()){
                    reminderRepository.deleteById(reminder.getId());
                }
                else{
                    reminder.setDate(reminder.getDateTime().toLocalDate());
                    reminder.setTime(reminder.getDateTime().toLocalTime());
                    String day  = reminder.getDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                    reminder.setDay(day);
                    unreceivedReminders.add(reminder);
                }
            }
        }
        mav.addObject("user",user);
        mav.addObject("unreceivedReminders",unreceivedReminders);
    }

}
