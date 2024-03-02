package edu.java.configuration;

import edu.java.clients.GitHubClient;
import edu.java.clients.StackOverflowClient;
import edu.java.dao.LinksToUpdateDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientsConfig {

    @Autowired
    LinksToUpdateDao linksToUpdateDao;

    @Bean GitHubClient gitHubClient(ApplicationConfig config) {
        return new GitHubClient(config.githubLink(), linksToUpdateDao);
    }

    @Bean StackOverflowClient stackOverflowClient(ApplicationConfig config) {
        return new StackOverflowClient(config.stackOverflowLink(), linksToUpdateDao);
    }
}
