package dev.firenze.graphql.restaurant.repository;

import dev.firenze.graphql.restaurant.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

  @Query("SELECT r FROM Restaurant r WHERE r.city like %:city%")
  List<Restaurant> findByCityLikeIgnoreCase(@Param("city") String city);
}
