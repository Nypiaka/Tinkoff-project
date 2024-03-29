package edu.java.configuration;

import edu.java.clients.GitHubClient;
import edu.java.clients.StackOverflowClient;
import edu.java.dao.LinksDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientsConfig {

    @Autowired
    LinksDao linksToUpdateDao;

    @Bean GitHubClient gitHubClient(ApplicationConfig config) {
        return new GitHubClient(config.githubLink(), linksToUpdateDao);
    }

    @Bean StackOverflowClient stackOverflowClient(ApplicationConfig config) {
        return new StackOverflowClient(config.stackOverflowLink(), linksToUpdateDao);
    }
}
