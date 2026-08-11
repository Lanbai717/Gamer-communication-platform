package com.example.badminton_team.service;

import com.example.badminton_team.dto.PageResult;
import com.example.badminton_team.dto.PostDto;
import com.example.badminton_team.entity.Post;
import java.util.List;
import java.util.Map;

public interface PostService {
    void create(PostDto dto, Integer uid);
    List<Post> list(Integer page, Integer size);
    Post getDetail(Integer pid);
    void like(Integer pid, Integer uid);
    List<Post> getByUser(Integer uid);
    void updateStatus(Integer pid, Integer status);
    void deleteByAdmin(Integer pid);
    void banPost(Integer pid);    // 封禁帖子（状态改为2）
    void unbanPost(Integer pid);  // 解封帖子（状态改为0）
    List<Post> findByStatus(Integer status);
    /**
     * 分页查询帖子（支持按状态、关键字筛选）
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @param status 状态（可选）
     * @param keyword 搜索关键字（可选）
     * @return 分页结果
     */
    PageResult<Post> findPage(Integer page, Integer size, Integer status, String keyword);
    long countAll();
    /**
     * 切换点赞状态（已点赞则取消，未点赞则点赞），返回新的点赞数和是否已点赞
     * @return Map containing "liked" (boolean) and "likeCount" (int)
     */
    Map<String, Object> toggleLike(Integer pid, Integer uid);
}