package com.example.badminton_team.dao;

import com.example.badminton_team.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentDao {
    /**
     * 根据ID查询评论
     */
    Comment findByCid(@Param("cid") Integer cid);

    /**
     * 查询某个帖子的所有评论（按时间正序）
     */
    List<Comment> findByPostId(@Param("postId") Integer postId);

    /**
     * 查询某个评论的回复（即 parent_id = cid）
     */
    List<Comment> findReplies(@Param("parentId") Integer parentId);

    /**
     * 插入评论
     */
    int insert(Comment comment);

    /**
     * 删除评论（物理删除，如果允许的话）
     */
    int delete(@Param("cid") Integer cid);
}