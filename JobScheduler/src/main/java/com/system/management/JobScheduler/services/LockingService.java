package com.system.management.JobScheduler.services;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

@Service
public class LockingService {
	
	private ConcurrentHashMap<UUID, ReentrantLock> jobInstanceLockMap ;
	
	@PostConstruct
	public void init() {
		jobInstanceLockMap = new ConcurrentHashMap<>();
	}
	
	public void getLockForLuanchId(UUID jobLaunchId) {
		
		ReentrantLock lock = jobInstanceLockMap.putIfAbsent(jobLaunchId, new ReentrantLock(true));
		lock.lock();
		
	}
	
	public void releaseLockForLaunchId(UUID jobLaunchId ) {
		ReentrantLock lock = jobInstanceLockMap.get(jobLaunchId);
		lock.unlock();
	}
	
	public void removeLockForLaunchId(UUID jobLaunchId ) {
		ReentrantLock lock = jobInstanceLockMap.get(jobLaunchId);
		
		if(lock != null && !lock.isLocked()) {
			jobInstanceLockMap.remove(jobLaunchId);
		}
		
		
	}
	

}
