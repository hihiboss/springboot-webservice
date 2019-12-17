package com.hihiboss.springbook.springboot.domain.posts;

import com.hihiboss.springbook.springboot.domain.posts.Posts;
import com.hihiboss.springbook.springboot.domain.posts.PostsRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PostsRepositoryTest {

    @Autowired
    PostsRepository postsRepository;

    @After
    public void cleanup() {
        postsRepository.deleteAll();
    }

    @Test
    public void loadSavingPost() {
        // given
        String title = "테스트 게시글";
        String content = "테스트 본문";

        postsRepository.save(Posts.builder()
                .title(title)
                .content(content)
                .author("junhui820@gmail.com")
                .build()
        );

        // when
        List<Posts> postsList = postsRepository.findAll();

        // then
        Posts posts = postsList.get(0);

        assertThat(posts.getTitle()).isEqualTo(title);
        assertThat(posts.getContent()).isEqualTo(content);
    }

    @Test
    public void registerBaseTimeEntity() {
        // given
        LocalDateTime now = LocalDateTime.of(2019, 12, 17, 22, 20, 00);
        postsRepository.save(Posts.builder()
                .title("title")
                .content("content")
                .author("author")
                .build());

        // when
        List<Posts> postsList = postsRepository.findAll();
        Posts posts = postsList.get(0);

        // then
        System.out.println(">>>>>>>> createDate=" + posts.getCreatedDate() + ", modifiedDate=" + posts.getModifiedDate());

        assertThat(posts.getCreatedDate()).isAfter(now);
        assertThat(posts.getModifiedDate()).isAfter(now);
    }

    @Test
    public void queryFindAllDesc() {
        // given
        String[] titleList = {"title1", "title2"};
        String[] contentList = {"content1", "content2"};

        postsRepository.save(Posts.builder()
                .title(titleList[0])
                .content(contentList[0])
                .author("author")
                .build());
        postsRepository.save(Posts.builder()
                .title(titleList[1])
                .content(contentList[1])
                .author("author")
                .build());

        // when
        List<Posts> postsList = postsRepository.findAllDesc();

        // then
        assertThat(postsList.get(0).getTitle()).isEqualTo(titleList[1]);
        assertThat(postsList.get(0).getContent()).isEqualTo(contentList[1]);

        assertThat(postsList.get(1).getTitle()).isEqualTo(titleList[0]);
        assertThat(postsList.get(1).getContent()).isEqualTo(contentList[0]);
    }
}
