package com.moveup.config;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class StripeConfig {
    
    @Value("${stripe.secret.key:sk_test_dummy}")
    private String secretKey;
    
    @Value("${stripe.publishable.key:pk_test_dummy}")
    private String publishableKey;
    
    @Value("${stripe.webhook.secret:whsec_dummy}")
    private String webhookSecret;
    
    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
        System.out.println("Stripe initialized with API version: " + Stripe.API_VERSION);
    }
    
    public String getPublishableKey() {
        return publishableKey;
    }
    
    public String getWebhookSecret() {
        return webhookSecret;
    }
}
