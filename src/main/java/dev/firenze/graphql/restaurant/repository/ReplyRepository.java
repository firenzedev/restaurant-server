package dev.firenze.graphql.restaurant.repository;

import dev.firenze.graphql.restaurant.model.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Integer> {

  List<Reply> findByReviewId(Integer id);
}
