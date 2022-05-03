package dev.firenze.graphql.restaurant.config;

import dev.firenze.graphql.restaurant.model.Reply;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;
import reactor.util.concurrent.Queues;

@Configuration
public class SubscriptionConfiguration {

  @Bean
  public Many<Reply> replySink() {
    return Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);
  }

  @Bean
  public Flux<Reply> replies(Many<Reply> replySink) {
    return replySink.asFlux();
  }
}
