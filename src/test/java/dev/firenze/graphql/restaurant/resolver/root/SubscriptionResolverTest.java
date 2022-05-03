package dev.firenze.graphql.restaurant.resolver.root;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import dev.firenze.graphql.restaurant.model.Reply;
import dev.firenze.graphql.restaurant.model.Review;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;

@ExtendWith(MockitoExtension.class)
class SubscriptionResolverTest {

  @InjectMocks
  private SubscriptionResolver resolver;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(resolver, "replies", Flux.just(
        Reply.builder().id(987).review(Review.builder().id(123).build()).build(),
        Reply.builder().id(876).review(Review.builder().id(234).build()).build(),
        Reply.builder().id(765).review(Review.builder().id(123).build()).build()
    ));
  }

  @Test
  void replyAdded() {
    Flux<Reply> result = (Flux<Reply>) resolver.replyAdded("123");
    List<Reply> list = result.collectList().block();
    assertNotNull(list);
    assertEquals(2, list.size());
    assertEquals(987, list.get(0).getId().intValue());
    assertEquals(765, list.get(1).getId().intValue());
  }
}
