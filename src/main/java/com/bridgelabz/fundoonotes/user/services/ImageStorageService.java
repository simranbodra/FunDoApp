package com.bridgelabz.fundoonotes.user.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.bridgelabz.fundoonotes.user.exceptions.FileConversionException;

public interface ImageStorageService {

	public void createBucket(String bucketName);
	
	public List<String> getBuckets();
	
	public void deleteBucket(String bucketName);
	
	public void createFolder(String folderName);
	
	public void deleteFolder(String folderName);
	
	public void uploadFile(String folderName, MultipartFile fileLocation) throws FileConversionException;
	
	public void deleteFile(String folderName, String deleteFileName);
	
	public String getFile(String folderName, String fileName);
	
	public List<String> getFiles(String folderName);
}
