package dev.firenze.graphql.restaurant.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import dev.firenze.graphql.restaurant.model.Reply;
import dev.firenze.graphql.restaurant.model.Restaurant;
import dev.firenze.graphql.restaurant.repository.ReplyRepository;
import dev.firenze.graphql.restaurant.repository.RestaurantRepository;
import dev.firenze.graphql.restaurant.repository.ReviewRepository;
import dev.firenze.graphql.restaurant.model.Review;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MutationTest {

  @Autowired
  private GraphQLTestTemplate graphQLTestTemplate;

  @MockBean
  private RestaurantRepository mockRestaurantRepository;

  @MockBean
  private ReviewRepository mockReviewRepository;

  @MockBean
  private ReplyRepository mockReplyRepository;

  @Test
  void createReviewSuccess() throws IOException {
    Integer restaurantId = 123;
    Integer reviewId = 111;
    Restaurant restaurant = Restaurant.builder().id(123).build();
    when(mockRestaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
    when(mockReviewRepository.save(any())).then(args -> {
      Review review = args.getArgument(0);
      review.setId(reviewId);
      return review;
    });

    GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/mutation/createReview.graphql");

    assertTrue(response.isOk());
    assertEquals(reviewId, response.get("$.data.createReview.id", Integer.class));
    assertEquals("Content", response.get("$.data.createReview.message"));
    assertEquals("3", response.get("$.data.createReview.rating"));
    assertEquals(restaurantId, response.get("$.data.createReview.restaurant.id", Integer.class));
  }

  @Test
  void createReviewRestaurantNotFound() throws IOException {
    Integer restaurantId = 123;
    when(mockRestaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

    GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/mutation/createReview.graphql");

    assertTrue(response.isOk());
    Object[] errors = response.get("$.errors", Object[].class);
    assertEquals(1, errors.length);
  }

  @Test
  void addReplySuccess() throws IOException {
    Integer reviewId = 123;
    Integer replyId = 111;
    Review review = Review.builder().id(reviewId).build();
    when(mockReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
    when(mockReplyRepository.save(any())).then(args -> {
      Reply reply = args.getArgument(0);
      reply.setId(replyId);
      return reply;
    });

    GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/mutation/addReply.graphql");

    assertTrue(response.isOk());
    assertEquals(replyId, response.get("$.data.addReply.id", Integer.class));
    assertEquals("Content", response.get("$.data.addReply.message"));
    assertEquals(reviewId, response.get("$.data.addReply.review.id", Integer.class));
  }

  @Test
  void addReplyReviewNotFound() throws IOException {
    Integer reviewId = 123;
    when(mockReviewRepository.findById(reviewId)).thenReturn(Optional.empty());

    GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/mutation/addReply.graphql");

    assertTrue(response.isOk());
    Object[] errors = response.get("$.errors", Object[].class);
    assertEquals(1, errors.length);
  }

}
