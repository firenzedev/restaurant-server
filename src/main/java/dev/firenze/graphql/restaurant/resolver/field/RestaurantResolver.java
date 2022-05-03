package dev.firenze.graphql.restaurant.resolver.field;

import dev.firenze.graphql.restaurant.model.Restaurant;
import dev.firenze.graphql.restaurant.model.Review;
import dev.firenze.graphql.restaurant.repository.ReviewRepository;
import graphql.kickstart.tools.GraphQLResolver;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RestaurantResolver implements GraphQLResolver<Restaurant> {

  @Autowired
  private ReviewRepository reviewRepository;

  public Double getRating(Restaurant restaurant) {
    final List<Review> reviews = reviewRepository.findByRestaurantId(restaurant.getId());
    return reviews
      .stream()
      .mapToDouble(Review::getRating)
      .average()
      .orElse(0);
  }

  public List<Review> getReviews(Restaurant restaurant, Integer rating) {
    final int requiredRating = rating == null ? 0 : rating;
    return reviewRepository.findByRestaurantId(restaurant.getId())
      .stream()
      .filter(r -> r.getRating() >= requiredRating)
      .toList();
  }

  public int getNumberOfReviews(Restaurant restaurant) {
    return reviewRepository.countByRestaurantId(restaurant.getId());
  }

}
