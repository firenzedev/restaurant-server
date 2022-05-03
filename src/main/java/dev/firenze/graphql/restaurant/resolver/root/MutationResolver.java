package dev.firenze.graphql.restaurant.resolver.root;

import dev.firenze.graphql.restaurant.repository.RestaurantRepository;
import dev.firenze.graphql.restaurant.repository.ReviewRepository;
import graphql.kickstart.tools.GraphQLMutationResolver;
import dev.firenze.graphql.restaurant.exception.ObjectNotFoundException;
import dev.firenze.graphql.restaurant.model.Reply;
import dev.firenze.graphql.restaurant.model.Restaurant;
import dev.firenze.graphql.restaurant.model.Review;
import dev.firenze.graphql.restaurant.model.ReviewInput;
import dev.firenze.graphql.restaurant.repository.ReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Sinks.Many;

@Component
public class MutationResolver implements GraphQLMutationResolver {

  @Autowired
  private ReviewRepository reviewRepository;

  @Autowired
  private RestaurantRepository restaurantRepository;

  @Autowired
  private ReplyRepository replyRepository;

  @Autowired
  private Many<Reply> replySink;

  @Transactional
  public Review createReview(ReviewInput input) {
    Restaurant restaurant = restaurantRepository
      .findById(input.getRestaurantId())
      .orElseThrow(() -> new ObjectNotFoundException("resturant non found", input.getRestaurantId()));

    Review review = Review.builder()
      .message(input.getMessage())
      .rating(input.getRating())
      .restaurant(restaurant)
      .build();
    return reviewRepository.save(review);
  }

  @Transactional
  public Reply addReply(String reviewId, String message) {

    Review review = reviewRepository
        .findById(Integer.parseInt(reviewId))
        .orElseThrow(() -> new ObjectNotFoundException("review non found", reviewId));

    Reply reply = Reply.builder()
        .message(message)
        .review(review)
        .build();

    Reply saved = replyRepository.save(reply);

    replySink.tryEmitNext(saved);

    return saved;
  }

}
