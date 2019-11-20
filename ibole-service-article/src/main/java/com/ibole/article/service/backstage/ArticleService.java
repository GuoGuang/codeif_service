package com.ibole.article.service.backstage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.CaseFormat;
import com.ibole.api.user.UserServiceRpc;
import com.ibole.article.dao.backstage.ArticleDao;
import com.ibole.cache.constant.RedisConstant;
import com.ibole.cache.redis.RedisService;
import com.ibole.constant.CommonConst;
import com.ibole.pojo.QueryVO;
import com.ibole.pojo.article.Article;
import com.ibole.pojo.user.User;
import com.ibole.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 文章板块:文章服务
 **/
@Service
public class ArticleService {

	private final ArticleDao articleDao;

	private final RedisService redisService;

	private final UserServiceRpc userServiceRpc;

	@Autowired
	public ArticleService(ArticleDao articleDao, RedisService redisService,UserServiceRpc userServiceRpc) {
		this.articleDao = articleDao;
		this.redisService = redisService;
		this.userServiceRpc = userServiceRpc;
	}

	/**
	 * 查询文章
	 * @return IPage<Article>
	 */
	public IPage<Article> findArticleByCondition(Article article, QueryVO queryVO ){
		Page<Article> pr = new Page<>(queryVO.getPageNum(),queryVO.getPageSize());
		QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
		if (StringUtils.isNotEmpty(article.getTitle())) {
			queryWrapper.lambda().like(Article::getTitle, article.getTitle());
		}
		if (article.getReviewState() != null ) {
			queryWrapper.lambda().like(Article::getReviewState, article.getReviewState());
		}
		if (StringUtils.isNotEmpty(article.getDescription())) {
			queryWrapper.lambda().like(Article::getDescription, article.getDescription());
		}
		if (queryVO.getOrderBy() != null && StringUtils.isNotEmpty(queryVO.getFieldSort())){
			// 驼峰下划线
			String fieldSort = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, queryVO.getFieldSort());
			queryWrapper.orderBy(true,queryVO.getOrderBy(),fieldSort);
		}
		IPage<Article> articleIPage = articleDao.selectPage(pr, queryWrapper);
		List<User> userList = userServiceRpc.findUser().getData().getRecords();
		articleIPage.getRecords().forEach(
				articleUser -> userList.forEach(
					user -> {
						if (user.getId().equals(articleUser.getUserId())){
							articleUser.setUserName(user.getUserName());
						}
					}
				)
		);
		return articleIPage;
	}



	/**
	 * 根据ID查询实体
	 * @param articleId 文章id
	 * @return Article
	 */
	public Article findArticleById(String articleId) {
		Object mapJson = redisService.get(RedisConstant.REDIS_KEY_ARTICLE + articleId);
		Article article;
		if(mapJson==null) {
			article = articleDao.selectById(articleId);
			redisService.set(RedisConstant.REDIS_KEY_ARTICLE+ articleId, article, CommonConst.TIME_OUT_DAY);
			return article;
		}else {
//			article = JsonUtil.jsonToPojo(mapJson.toString(), Article.class);
			article = (Article)mapJson;
		}
		return article;
	}

	/**
	 * 增加
	 * @param article 实体
	 */
	public void insertOrUpdateArticle(Map<String, String> userInfo,Article article) {
		article.setUserId(userInfo.get("id"));
		if (StringUtils.isBlank(article.getId())){
			article.setComment(0);
			article.setUpvote(0);
			article.setVisits(0);
			article.setReviewState(2);
			article.setImportance(0);
			article.setCreateAt(DateUtil.getTimestamp());
			article.setUpdateAt(DateUtil.getTimestamp());
			if (article.getIsPublic() == null){
				article.setIsPublic(0);
			}
			articleDao.insert(article);
		}else {
			redisService.del( "ARTICLE_" + article.getId());
			article.setUpdateAt(DateUtil.getTimestamp());
			articleDao.updateById(article);
		}
	}

	/**
	 * 删除
	 * @param articleIds:文章id集合
	 */
	public void deleteArticleByIds(List<String> articleIds) {
		articleDao.deleteBatchIds(articleIds);
	}

	/**
	 * 审核文章
	 * @param id
	 */
	public void examine(String id){
		articleDao.examine(id);
	}

	/**
	 * 点赞
	 * @param id 文章ID
	 */
	public int updateThumbUp(String id){
		return articleDao.updateThumbUp(id);
	}

}