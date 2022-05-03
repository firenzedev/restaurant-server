package dev.firenze.graphql.restaurant.resolver.root;

import dev.firenze.graphql.restaurant.repository.RestaurantRepository;
import dev.firenze.graphql.restaurant.repository.ReviewRepository;
import graphql.kickstart.tools.GraphQLQueryResolver;
import dev.firenze.graphql.restaurant.model.Restaurant;
import dev.firenze.graphql.restaurant.model.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class QueryResolver implements GraphQLQueryResolver {

  @Autowired
  private RestaurantRepository repository;

  @Autowired
  private ReviewRepository reviewRepository;


  public List<Restaurant> getRestaurants(String city) {
    if (city != null) {
      return repository.findByCityLikeIgnoreCase(city);
    }
    return repository.findAll();
  }

  public Optional<Restaurant> getRestaurant(String id) {
    return repository.findById(Integer.parseInt(id));
  }

  public List<Review> getReviews(String restaurantId) {
    return reviewRepository.findByRestaurantId(Integer.parseInt(restaurantId));
  }

}
