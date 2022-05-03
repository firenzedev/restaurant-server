package dev.firenze.graphql.restaurant.resolver.field;

import dev.firenze.graphql.restaurant.repository.ReplyRepository;
import graphql.kickstart.tools.GraphQLResolver;
import dev.firenze.graphql.restaurant.model.Reply;
import dev.firenze.graphql.restaurant.model.Review;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReviewResolver implements GraphQLResolver<Review> {

  @Autowired
  private ReplyRepository replyRepository;

  public List<Reply> getReplies(Review review) {
    return replyRepository.findByReviewId(review.getId());
  }


}
