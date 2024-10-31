package ai.herofactoryservice.create_game_resource_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RabbitMQConfig {
    public static final String PAYMENT_QUEUE = "payment-queue";
    public static final String PAYMENT_EXCHANGE = "payment-exchange";
    public static final String PAYMENT_DLQ = "payment-dlq";
    public static final String PAYMENT_DLX = "payment-dlx";
    public static final String PROMPT_QUEUE = "prompt-queue";
    public static final String PROMPT_EXCHANGE = "prompt-exchange";
    public static final String PROMPT_DLQ = "prompt-dlq";
    public static final String PROMPT_DLX = "prompt-dlx";


    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue paymentQueue() {
        return QueueBuilder.durable(PAYMENT_QUEUE)
                .withArgument("x-dead-letter-exchange", PAYMENT_DLX)
                .withArgument("x-dead-letter-routing-key", PAYMENT_DLQ)
                .withArgument("x-message-ttl", 300000) // 5분
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(PAYMENT_DLQ).build();
    }

    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(PAYMENT_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(PAYMENT_DLX, true, false);
    }

    @Bean
    public Binding paymentBinding() {
        return BindingBuilder.bind(paymentQueue())
                .to(paymentExchange())
                .with(PAYMENT_QUEUE);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(PAYMENT_DLQ);
    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000);
        backOffPolicy.setMultiplier(2.0);
        backOffPolicy.setMaxInterval(10000);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        return retryTemplate;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter messageConverter,
                                         RetryTemplate retryTemplate) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setRetryTemplate(retryTemplate);
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                // 메시지 전송 실패 처리
                // correlationData를 통해 실패한 메시지 식별 가능
            }
        });
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setPrefetchCount(1);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("rabbitmq");  // 도커 네트워크 사용시 "rabbitmq"
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }
    @Bean
    public Queue promptQueue() {
        return QueueBuilder.durable(PROMPT_QUEUE)
                .withArgument("x-dead-letter-exchange", PROMPT_DLX)
                .withArgument("x-dead-letter-routing-key", PROMPT_DLQ)
                .withArgument("x-message-ttl", 300000)
                .build();
    }

    @Bean
    public Queue promptDeadLetterQueue() {
        return QueueBuilder.durable(PROMPT_DLQ).build();
    }

    @Bean
    public DirectExchange promptExchange() {
        return new DirectExchange(PROMPT_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange promptDeadLetterExchange() {
        return new DirectExchange(PROMPT_DLX, true, false);
    }

    @Bean
    public Binding promptBinding() {
        return BindingBuilder.bind(promptQueue())
                .to(promptExchange())
                .with(PROMPT_QUEUE);
    }

    @Bean
    public Binding promptDeadLetterBinding() {
        return BindingBuilder.bind(promptDeadLetterQueue())
                .to(promptDeadLetterExchange())
                .with(PROMPT_DLQ);
    }
}
