package com.example.badminton_team.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class CommentDto {
    private Integer cid;
    @NotNull
    private Integer postId;
    @NotBlank
    private String content;
    private Integer parentId; // 0 表示根评论
}