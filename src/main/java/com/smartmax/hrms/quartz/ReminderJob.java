package com.smartmax.hrms.quartz;

import java.util.List;
import java.util.stream.Collectors;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.smartmax.hrms.entity.Reminder;
import com.smartmax.hrms.entity.User;
import com.smartmax.hrms.repository.ReminderRepository;
import com.smartmax.hrms.repository.UserRepository;
import com.smartmax.hrms.websocket.WebsocketService;

@Component
public class ReminderJob extends QuartzJobBean{
	@Autowired
	ReminderRepository reminderRepository;
	
	@Autowired
	SessionRegistry sessionRegistry;
	
	@Autowired
	UserRepository userRepository;
	

	@Autowired
	WebsocketService websocketService;
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		JobDataMap jobDataMap = context.getMergedJobDataMap();
		String reminderJobName = jobDataMap.getString("reminderJobName");
		Reminder reminder = reminderRepository.findByJobName(reminderJobName).get();
		reminder.setTriggered(true);
		boolean online = false;
		List<UserDetails> usersDetails= sessionRegistry.getAllPrincipals()
		            		 			.stream()
		            		 			.filter(principal -> principal instanceof UserDetails)
		            		 			.map(UserDetails.class::cast)
		            		 			.collect(Collectors.toList());
		User user = userRepository.findByReminderId(reminder.getId()).get();
		for(int  i =0;i<usersDetails.size();i++) {
			if(usersDetails.get(i).getUsername().equals(user.getUsername())) {
				online = true;
				reminder.setReceived(true);
				reminderRepository.save(reminder);
				websocketService.notifyFrontend(user,reminder);
			}
		}
		if(!online) {
			reminder.setReceived(false);
			reminderRepository.save(reminder);
		}
	}

}
  