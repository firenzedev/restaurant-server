package dev.firenze.graphql.restaurant.resolver.root;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import dev.firenze.graphql.restaurant.model.Restaurant;
import dev.firenze.graphql.restaurant.repository.RestaurantRepository;
import dev.firenze.graphql.restaurant.repository.ReviewRepository;
import dev.firenze.graphql.restaurant.model.Review;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QueryResolverTest {

  @Mock
  private RestaurantRepository mockRepository;

  @Mock
  private ReviewRepository mockReviewRepository;

  @InjectMocks
  private QueryResolver resolver;

  @Test
  void getRestaurants() {
    List<Restaurant> expected = List.of(new Restaurant(), new Restaurant());
    when(mockRepository.findAll()).thenReturn(expected);

    List<Restaurant> result = resolver.getRestaurants(null);

    assertEquals(expected, result);
  }

  @Test
  void getRestaurantFound() {
    Restaurant expected = new Restaurant();
    when(mockRepository.findById(123)).thenReturn(Optional.of(expected));

    Optional<Restaurant> result = resolver.getRestaurant("123");

    assertTrue(result.isPresent());
    assertEquals(expected, result.get());
  }

  @Test
  void getRestaurantNotFound() {
    when(mockRepository.findById(123)).thenReturn(Optional.empty());

    Optional<Restaurant> result = resolver.getRestaurant("123");

    assertFalse(result.isPresent());
  }

  @Test
  void getReviews() {
    List<Review> expected = List.of(new Review(), new Review());
    int restaurantId = 1;
    when(mockReviewRepository.findByRestaurantId(restaurantId)).thenReturn(expected);

    List<Review> result = resolver.getReviews(String.valueOf(restaurantId));

    assertEquals(expected, result);
  }
  
}
