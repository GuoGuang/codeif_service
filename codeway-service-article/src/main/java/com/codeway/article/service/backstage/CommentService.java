package com.codeway.article.service.backstage;

import com.codeway.article.dao.backstage.CommentDao;
import com.codeway.article.mapper.CommentMapper;
import com.codeway.exception.custom.ResourceNotFoundException;
import com.codeway.model.dto.article.CommentDto;
import com.codeway.model.pojo.article.Comment;
import com.codeway.utils.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {

	private final CommentDao commentDao;
	private final CommentMapper commentMapper;

	public CommentService(CommentDao commentDao, CommentMapper commentMapper) {
		this.commentDao = commentDao;
		this.commentMapper = commentMapper;
	}

	public Page<CommentDto> findCommentByCondition(CommentDto commentDto, Pageable pageable) {
		Specification<Comment> condition = (root, query, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (StringUtils.isNotEmpty(commentDto.getContent())) {
				predicates.add(builder.like(root.get("content"), "%" + commentDto.getContent() + "%"));
			}
			return query.where(predicates.toArray(new Predicate[0])).getRestriction();
		};
		return commentDao.findAll(condition, pageable).map(commentMapper::toDto);
	}

	public CommentDto findCommentByPrimaryKey(String commentId) {
		return commentDao.findById(commentId).map(commentMapper::toDto).orElseThrow(ResourceNotFoundException::new);
	}

	public void saveOrUpdate(CommentDto commentDto) {
		if (StringUtils.isNotBlank(commentDto.getId())) {
			Comment tempComment = commentDao.findById(commentDto.getId()).orElseThrow(ResourceNotFoundException::new);
			BeanUtil.copyProperties(tempComment, commentDto);
		}
		commentDao.save(commentMapper.toEntity(commentDto));
	}

	public void deleteCommentByIds(List<String> commentIds) {
		commentDao.deleteBatch(commentIds);
	}

}
