package com.example.badminton_team.service.impl;

import com.example.badminton_team.dao.PostDao;
import com.example.badminton_team.dto.PageResult;
import com.example.badminton_team.dto.PostDto;
import com.example.badminton_team.entity.Post;
import com.example.badminton_team.exception.BusinessException;
import com.example.badminton_team.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostDao postDao;

    @Override
    @Transactional
    public void create(PostDto dto, Integer uid) {
        Post post = new Post();
        post.setUserId(uid);
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setStatus(0); // 正常（如需审核可设为1）
        post.setCreateTime(new Date());
        post.setUpdateTime(new Date());
        postDao.insert(post);
    }

    @Override
    public List<Post> list(Integer page, Integer size) {
        // 原有方法保持不变，返回所有正常帖子（状态0）的前N条
        // 简单起见，仍调用 findList，不进行分页（若需要分页可调整）
        return postDao.findList(0, null);
    }

    @Override
    public Post getDetail(Integer pid) {
        Post post = postDao.findByPid(pid);
        if (post == null) throw new BusinessException("帖子不存在");
        postDao.increaseViewCount(pid);
        return post;
    }

    @Override
    @Transactional
    public void like(Integer pid, Integer uid) {
        if (postDao.hasLiked(pid, uid)) {
            // 已点赞 -> 取消点赞
            postDao.removeLike(pid, uid);
            postDao.decreaseLikeCount(pid);
        } else {
            // 未点赞 -> 点赞
            postDao.addLike(pid, uid);
            postDao.increaseLikeCount(pid);
        }
    }

    @Override
    public List<Post> getByUser(Integer uid) {
        return postDao.findByUserId(uid);
    }

    @Override
    @Transactional
    public void updateStatus(Integer pid, Integer status) {
        postDao.updateStatus(pid, status);
    }

    @Override
    @Transactional
    public void deleteByAdmin(Integer pid) {
        // 将帖子状态改为 2（已删除）
        postDao.updateStatus(pid, 2);
    }

    @Override
    public List<Post> findByStatus(Integer status) {
        return postDao.findList(status, null);
    }

    /**
     * 分页查询帖子（支持按状态、关键字筛选）
     */
    @Override
    public PageResult<Post> findPage(Integer page, Integer size, Integer status, String keyword) {
        int offset = (page - 1) * size;
        List<Post> list = postDao.findPage(offset, size, status, keyword);
        long total = postDao.count(status, keyword);
        return new PageResult<>(list, total);
    }

    @Override
    public long countAll() {
        return postDao.countAll();
    }

    @Override
    @Transactional
    public void banPost(Integer pid) {
        postDao.updateStatus(pid, 2);
    }

    @Override
    @Transactional
    public void unbanPost(Integer pid) {
        postDao.updateStatus(pid, 0);
    }

    @Override
    @Transactional
    public Map<String, Object> toggleLike(Integer pid, Integer uid) {
        boolean liked = postDao.hasLiked(pid, uid);
        if (liked) {
            postDao.removeLike(pid, uid);
            postDao.decreaseLikeCount(pid);
        } else {
            postDao.addLike(pid, uid);
            postDao.increaseLikeCount(pid);
        }
        int newCount = postDao.getLikeCount(pid);
        Map<String, Object> result = new HashMap<>();
        result.put("liked", !liked); // 新状态
        result.put("likeCount", newCount);
        return result;
    }
}