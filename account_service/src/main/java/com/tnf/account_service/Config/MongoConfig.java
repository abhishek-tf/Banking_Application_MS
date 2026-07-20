package com.tnf.account_service.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

// Only enables repository scanning; connection settings live in application.yml.
@Configuration
@EnableMongoRepositories(basePackages = "com.tnf.account_service.Repository")
public class MongoConfig {

}
