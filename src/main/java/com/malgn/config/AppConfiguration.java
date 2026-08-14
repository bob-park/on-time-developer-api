package com.malgn.config;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@RequiredArgsConstructor
@EnableScheduling
@EnableTransactionManagement
@Configuration
public class AppConfiguration {
}
