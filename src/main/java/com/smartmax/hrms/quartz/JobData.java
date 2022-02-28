package com.smartmax.hrms.quartz;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class JobData {
	private String jobName;
	private String jobGroup;
	@JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss")
	private LocalDateTime startTime;
	private int counter;
	private int gapDuration;
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getJobGroup() {
		return jobGroup;
	}
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}
	public LocalDateTime getStartTime() {
		return startTime;
	}
	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}
	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}
	public int getGapDuration() {
		return gapDuration;
	}
	public void setGapDuration(int gapDuration) {
		this.gapDuration = gapDuration;
	}
	
}
