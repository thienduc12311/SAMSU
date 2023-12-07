package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.service.EventService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.junit.Assert.*;

@SpringBootTest
public class EventServiceImplTest {


    @Test
    public void evictAllEntries() {

        EventService eventService = new EventServiceImpl();
        eventService.evictAllEntries();

//        String cacheKey = "someKey";
//        Cache eventsCache = cacheManager.getCache("eventsCache");
//        eventsCache.put(cacheKey, "someValue");
//
//        Object valueBeforeEviction = eventsCache.get(cacheKey).get();
//        System.out.println("Value before eviction: " + valueBeforeEviction);

    }
}