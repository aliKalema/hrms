package com.smartmax.hrms.service;

import java.security.NoSuchAlgorithmException;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
	public String addFile(MultipartFile multipartfile) throws NoSuchAlgorithmException;

}
