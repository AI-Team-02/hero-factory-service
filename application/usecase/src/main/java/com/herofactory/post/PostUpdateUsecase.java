package com.herofactory.post;

import lombok.Data;

public interface PostUpdateUsecase {

    Post update(Request request);

    @Data
    class Request {
        private final Long postId;
        private final String title;
        private final String content;
        private final Long categoryId;
    }
}
