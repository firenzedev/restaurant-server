package dev.firenze.graphql.restaurant.resolver.root;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.firenze.graphql.restaurant.exception.ObjectNotFoundException;
import dev.firenze.graphql.restaurant.model.Reply;
import dev.firenze.graphql.restaurant.model.Restaurant;
import dev.firenze.graphql.restaurant.repository.ReplyRepository;
import dev.firenze.graphql.restaurant.repository.RestaurantRepository;
import dev.firenze.graphql.restaurant.repository.ReviewRepository;
import dev.firenze.graphql.restaurant.model.Review;
import dev.firenze.graphql.restaurant.model.ReviewInput;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

@ExtendWith(MockitoExtension.class)
class MutationResolverTest {

  private final Many<Reply> replySink = Sinks.many().multicast().onBackpressureBuffer();
  
  @Mock
  private RestaurantRepository mockRepository;

  @Mock
  private ReviewRepository mockReviewRepository;

  @Mock
  private ReplyRepository mockReplyRepository;
  
  @Captor
  private ArgumentCaptor<Review> reviewCaptor;

  @Captor
  private ArgumentCaptor<Reply> replyCaptor;

  @InjectMocks
  private MutationResolver resolver;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(resolver, "replySink", replySink);
  }
  
  @Test
  void createReview() {
    int restaurantId = 1;
    when(mockRepository.findById(restaurantId)).thenReturn(Optional.of(new Restaurant()));

    Integer rating = 4;

    ReviewInput input = ReviewInput.builder()
      .restaurantId(restaurantId)
      .message("wow")
      .rating(4)
      .build();
    final Review review = resolver.createReview(input);

    verify(mockReviewRepository).save(reviewCaptor.capture());

    assertEquals(rating, reviewCaptor.getValue().getRating());
    assertEquals("wow", reviewCaptor.getValue().getMessage());
  }

  @Test
  void createReview_WhenARestaurantIsNotFound() {
    int restaurantId = 1;
    when(mockRepository.findById(restaurantId)).thenReturn(Optional.empty());

    ReviewInput input = ReviewInput.builder()
        .restaurantId(restaurantId)
        .message("wow")
        .rating(4)
        .build();
    assertThrows(ObjectNotFoundException.class, () -> resolver.createReview(input));

    verify(mockReviewRepository, times(0)).save(any(Review.class));
  }

  @Test
  void addReply() {
    int reviewId = 123;
    Review review = new Review();
    String message = "This is a reply";

    when(mockReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
    when(mockReplyRepository.save(any())).then(args -> args.getArgument(0));

    List<Reply> replies = new ArrayList<>();
    replySink.asFlux().subscribe(replies::add);

    Reply result = resolver.addReply(String.valueOf(reviewId), message);

    assertNotNull(replies);
    assertEquals(1, replies.size());
    assertEquals(result, replies.get(0));

    verify(mockReplyRepository).save(replyCaptor.capture());
    assertEquals(message, replyCaptor.getValue().getMessage());
    assertEquals(review, replyCaptor.getValue().getReview());
    assertEquals(result, replyCaptor.getValue());
  }

  @Test
  void addReply_reviewNotFound() {
    int reviewId = 123;
    String message = "This is a reply";

    when(mockReviewRepository.findById(reviewId)).thenReturn(Optional.empty());

    String reviewIdString = String.valueOf(reviewId);
    assertThrows(ObjectNotFoundException.class, () -> resolver.addReply(reviewIdString, message));
  }
}
