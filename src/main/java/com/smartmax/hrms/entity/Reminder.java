package com.smartmax.hrms.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;

import javax.persistence.*;

@Entity
public class Reminder {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private String jobName;
	private LocalDateTime dateTime;
	private String message;
	private boolean triggered;
	private boolean received;
	@OneToOne(cascade= CascadeType.MERGE,fetch= FetchType.EAGER)
	@JoinColumn(name="user_id")
	private User user;
	@Transient
	private LocalDate date;
	@Transient
	private LocalTime time;
	@Transient
	String day;
	public Reminder(int id, String jobName, LocalDateTime dateTime, String message, boolean triggered, boolean received, User user) {
		this.id = id;
		this.jobName = jobName;
		this.dateTime = dateTime;
		this.message = message;
		this.triggered = triggered;
		this.received = received;
		this.user = user;
	}
	public Reminder(){super();}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public LocalDateTime getDateTime() {
		return dateTime;
	}
	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isTriggered() {
		return triggered;
	}
	public void setTriggered(boolean triggered) {
		this.triggered = triggered;
	}
	public boolean isReceived() {
		return received;
	}
	public void setReceived(boolean received) {
		this.received = received;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public LocalTime getTime() {
		return time;
	}
	public void setTime(LocalTime time) {
		this.time = time;
	}
	public void setDay(String day){
		this.day =day;
	}
	public String getDay(){
		return day;
	}

	@Override
	public String toString() {
		return "Reminder{" +
				"id=" + id +
				", jobName='" + jobName + '\'' +
				", dateTime=" + dateTime +
				", message='" + message + '\'' +
				", triggered=" + triggered +
				", received='" + received + '\'' +
				", user=" + user +
				'}';
	}

}
