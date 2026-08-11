package com.example.badminton_team.dao;

import com.example.badminton_team.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostDao {
    /**
     * 根据ID查询帖子
     */
    Post findByPid(@Param("pid") Integer pid);

    /**
     * 查询帖子列表（支持按状态、关键字筛选，不分页）
     */
    List<Post> findList(@Param("status") Integer status,
                        @Param("keyword") String keyword);

    /**
     * 分页查询帖子列表（支持按状态、关键字筛选）
     */
    List<Post> findPage(@Param("offset") int offset,
                        @Param("size") int size,
                        @Param("status") Integer status,
                        @Param("keyword") String keyword);

    /**
     * 统计符合条件的帖子总数
     */
    long count(@Param("status") Integer status,
               @Param("keyword") String keyword);

    /**
     * 查询用户发布的帖子
     */
    List<Post> findByUserId(@Param("userId") Integer userId);

    /**
     * 插入帖子
     */
    int insert(Post post);

    /**
     * 更新帖子内容
     */
    int update(Post post);

    /**
     * 更新帖子状态（审核通过0/待审核1/已删除2）
     */
    int updateStatus(@Param("pid") Integer pid, @Param("status") Integer status);

    /**
     * 增加浏览数
     */
    int increaseViewCount(@Param("pid") Integer pid);

    /**
     * 增加点赞数
     */
    int increaseLikeCount(@Param("pid") Integer pid);

    /**
     * 减少点赞数
     */
    int decreaseLikeCount(@Param("pid") Integer pid);

    /**
     * 增加评论数
     */
    int increaseCommentCount(@Param("pid") Integer pid);

    /**
     * 减少评论数
     */
    int decreaseCommentCount(@Param("pid") Integer pid);

    /**
     * 统计帖子总数
     */
    long countAll();
    // ========== 点赞记录表操作 ==========
    /**
     * 检查用户是否已点赞
     */
    boolean hasLiked(@Param("postId") Integer postId, @Param("userId") Integer userId);

    /**
     * 添加点赞记录
     */
    int addLike(@Param("postId") Integer postId, @Param("userId") Integer userId);

    /**
     * 删除点赞记录
     */
    int removeLike(@Param("postId") Integer postId, @Param("userId") Integer userId);

    /**
     * 获取帖子的当前点赞数
     */
    int getLikeCount(@Param("pid") Integer pid);
}