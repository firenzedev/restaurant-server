package dev.firenze.graphql.restaurant.repository;

import dev.firenze.graphql.restaurant.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

  List<Restaurant> findByCityLikeIgnoreCase(String city);
}
