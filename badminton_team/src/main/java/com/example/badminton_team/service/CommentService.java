package com.example.badminton_team.service;

import com.example.badminton_team.dto.CommentDto;
import com.example.badminton_team.entity.Comment;
import java.util.List;

public interface CommentService {
    void create(CommentDto dto, Integer uid);
    List<Comment> getByPost(Integer postId);
    void delete(Integer cid, Integer uid);
}
