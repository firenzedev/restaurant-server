package dev.firenze.graphql.restaurant.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import dev.firenze.graphql.restaurant.model.Restaurant;
import dev.firenze.graphql.restaurant.repository.RestaurantRepository;
import dev.firenze.graphql.restaurant.repository.ReviewRepository;
import dev.firenze.graphql.restaurant.model.Review;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class QueryTest {

  @Autowired
  private GraphQLTestTemplate graphQLTestTemplate;

  @MockBean
  private RestaurantRepository mockRestaurantRepository;

  @MockBean
  private ReviewRepository mockReviewRepository;

  @Test
  void getRestaurantsEmpty() throws IOException {
    when(mockRestaurantRepository.findAll()).thenReturn(Collections.emptyList());

    GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/query/getRestaurants.graphql");

    assertTrue(response.isOk());
    Restaurant[] restaurants = response.get("$.data.restaurants", Restaurant[].class);
    assertEquals(0, restaurants.length);
  }

  @Test
  void getRestaurantsNotEmpty() throws IOException {
    Restaurant restaurant1 = Restaurant.builder().id(123).name("Restaurant one").address("address one").city("city one").build();
    Restaurant restaurant2 = Restaurant.builder().id(234).name("Restaurant two").address("address two").city("city two").build();
    when(mockRestaurantRepository.findAll()).thenReturn(List.of(restaurant1, restaurant2));

    GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/query/getRestaurants.graphql");

    assertTrue(response.isOk());
    Restaurant[] restaurants = response.get("$.data.restaurants", Restaurant[].class);
    assertEquals(2, restaurants.length);
    assertEquals(restaurant1, restaurants[0]);
    assertEquals(restaurant2, restaurants[1]);
  }

  @Test
  void getRestaurantFound() throws IOException {
    Restaurant restaurant = Restaurant.builder().id(123).name("Restaurant one").address("address one").city("city one").build();
    when(mockRestaurantRepository.findById(123)).thenReturn(Optional.of(restaurant));

    GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/query/getRestaurant.graphql");

    assertTrue(response.isOk());
    Restaurant result = response.get("$.data.restaurant", Restaurant.class);
    assertEquals(restaurant, result);
  }

  @Test
  void getRestaurantNotFound() throws IOException {
    when(mockRestaurantRepository.findById(123)).thenReturn(Optional.empty());

    GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/query/getRestaurant.graphql");

    assertTrue(response.isOk());
    assertNull(response.get("$.data.restaurant"));
  }

  @Test
  void getReviewsEmpty() throws IOException {
    when(mockReviewRepository.findByRestaurantId(123)).thenReturn(Collections.emptyList());

    GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/query/getReviews.graphql");

    assertTrue(response.isOk());
    Review[] reviews = response.get("$.data.reviews", Review[].class);
    assertEquals(0, reviews.length);
  }

  @Test
  void getReviewsNotEmpty() throws IOException {
    Restaurant restaurant = Restaurant.builder().id(123).build();
    Review review1 = Review.builder().id(111).message("Message one").rating(1).restaurant(restaurant).build();
    Review review2 = Review.builder().id(222).message("Message two").rating(2).restaurant(restaurant).build();
    when(mockReviewRepository.findByRestaurantId(123)).thenReturn(List.of(review1, review2));

    GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/query/getReviews.graphql");

    assertTrue(response.isOk());
    Review[] reviews = response.get("$.data.reviews", Review[].class);
    assertEquals(2, reviews.length);
    assertEquals(review1, reviews[0]);
    assertEquals(review2, reviews[1]);
  }
}
