package com.example.badminton_team.service.impl;

import com.example.badminton_team.dao.CommentDao;
import com.example.badminton_team.dao.PostDao;
import com.example.badminton_team.dto.CommentDto;
import com.example.badminton_team.entity.Comment;
import com.example.badminton_team.exception.BusinessException;
import com.example.badminton_team.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private PostDao postDao;

    @Override
    @Transactional
    public void create(CommentDto dto, Integer uid) {
        Comment comment = new Comment();
        comment.setPostId(dto.getPostId());
        comment.setUserId(uid);
        comment.setContent(dto.getContent());
        comment.setParentId(dto.getParentId() == null ? 0 : dto.getParentId());
        comment.setCreateTime(new Date());
        commentDao.insert(comment);
        postDao.increaseCommentCount(dto.getPostId());
    }

    @Override
    public List<Comment> getByPost(Integer postId) {
        return commentDao.findByPostId(postId);
    }

    @Override
    @Transactional
    public void delete(Integer cid, Integer uid) {
        Comment comment = commentDao.findByCid(cid);
        if (comment == null) throw new BusinessException("评论不存在");
        if (!comment.getUserId().equals(uid)) {
            throw new BusinessException("无权限删除此评论");
        }
        commentDao.delete(cid);
        postDao.decreaseCommentCount(comment.getPostId());
    }
}
