package org.study.post.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.study.post.application.interfaces.PostRepository;
import org.study.post.domain.Post;

public class FakePostRepository implements PostRepository {

    private final Map<Long, Post> store = new HashMap<>();


    @Override
    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Post save(Post post) {
        if (post.getId() != null) {
            store.put(post.getId(), post);
            return post;
        }

        long id = store.size() + 1;
        Post newPost = Post.createDefalutPost(id, post.getAuthor(), post.getContent());
        store.put(id, newPost);
        return newPost;
    }

}
