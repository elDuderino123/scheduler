package com.system.management.JobScheduler.services;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.uuid.Generators;
import com.system.management.JobScheduler.commons.Constants;

import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;

@Service
public class CacheService {

	private static  Logger LOGGER = LoggerFactory.getLogger(CacheService.class);
	
	private ChronicleMap<UUID, String> localJobCache;
	
	@PostConstruct
	public void init() {
		try {
			localJobCache = ChronicleMapBuilder.of(UUID.class,String.class).constantKeySizeBySample(Generators.timeBasedGenerator().generate()).averageValueSize(1024).name("JOB_SCHEDULER_CACHE").entries(10000).createPersistedTo(new File(Constants.CACHE_PERSISTENCE_FILE));
		} catch (IOException e) {
			LOGGER.error("Exception while initializing the local cache",e);
		}
	}
	
	
	public String getForUUID(UUID key) {
		return localJobCache.get(key);
	}
	
	public void setForUUID(UUID key , String value) {
		localJobCache.put(key, value);
	}
	
	public void removeFromCache(UUID key) {
		localJobCache.remove(key);
	}
	
}
