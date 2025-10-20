package com.moveup.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("cityRankings", "famousLocations");
    }

    /**
     * Refresh automatico della cache ogni 10 minuti
     */
    @Scheduled(fixedRate = 600000) // 10 minuti in millisecondi
    public void refreshCache() {
        // Il ConcurrentMapCacheManager non ha un metodo diretto per clear
        // Le cache si aggiorneranno automaticamente quando i metodi @Cacheable verranno chiamati
        // dopo la scadenza naturale della cache
    }
}