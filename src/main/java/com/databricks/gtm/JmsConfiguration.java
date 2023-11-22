package com.databricks.gtm;

import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

import javax.jms.ConnectionFactory;

@Configuration
public class JmsConfiguration {

    public static final String SLACK_INTAKE_QUEUE = "urn:dbx:slack:intake";
    public static final String SLACK_FEEDBACK_QUEUE = "urn:dbx:slack:update";

    @Bean("queueFactory")
    public JmsListenerContainerFactory<?> slackJmsFactory(ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();

        // This provides all autoconfigured defaults to this im-memory factory, including the message converter
        configurer.configure(factory, connectionFactory);

        // You could still override some settings if necessary.
        return factory;
    }
}
