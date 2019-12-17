package com.hihiboss.springbook.springboot.web;

import com.hihiboss.springbook.springboot.domain.posts.Posts;
import com.hihiboss.springbook.springboot.domain.posts.PostsRepository;
import com.hihiboss.springbook.springboot.web.dto.PostsSaveRequestDto;
import com.hihiboss.springbook.springboot.web.dto.PostsUpdateRequestDto;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    private String domainUrl() {
        return "http://localhost:" + port;
    }

    private String apiUrl(String version) {
        return "/api/" + version + "/posts";
    }

    @After
    public void tearDown() throws Exception {
        postsRepository.deleteAll();
    }

    @Test
    public void registerPosts() throws Exception {
        // given
        String title = "title";
        String content = "content";
        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
                .title(title)
                .content(content)
                .author("junhui820@gmail.com")
                .build();

        String url = domainUrl() + "/api/v1/posts";

        // when
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, requestDto, Long.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = postsRepository.findAll();
        Posts post = all.get(0);
        assertThat(post.getTitle()).isEqualTo(title);
        assertThat(post.getContent()).isEqualTo(content);
    }

    @Test
    public void editPosts() throws Exception {
        // given
        Posts savedPosts = postsRepository.save(Posts.builder()
                .title("title")
                .content("content")
                .author("author")
                .build());
        Long savedPostsId = savedPosts.getId();

        String expectedTitle = "title2";
        String expectedContent = "content2";

        PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
                .title(expectedTitle)
                .content(expectedContent)
                .build();

        String url = domainUrl() + apiUrl("v1") + "/" + savedPostsId;

        HttpEntity<PostsUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);

        // when
        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Long.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = postsRepository.findAll();
        Posts updatedPost = all.get(0);
        assertThat(updatedPost.getTitle()).isEqualTo(expectedTitle);
        assertThat(updatedPost.getContent()).isEqualTo(expectedContent);
    }

    @Test
    public void removePosts() throws Exception {
        // given
        Posts savedPosts = postsRepository.save(Posts.builder()
                .title("title")
                .content("content")
                .author("author")
                .build());
        Long savedPostsId = savedPosts.getId();

        String url = domainUrl() + apiUrl("v1") + "/" + savedPostsId;

        // when
        restTemplate.delete(url);

        //then
        List<Posts> all = postsRepository.findAll();
        assertThat(all.size()).isEqualTo(0);
    }
}
