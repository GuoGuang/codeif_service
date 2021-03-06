package com.codeway.search.dao;

import com.codeway.search.pojo.Article;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

/**
 * 集成ElasticSearch的文章搜索系统
 *  集成SpringDataElasticSearch 之后只需要继承ElasticsearchRepository就可以,
 **/
// <Article,String> Article的主键是String类型
@Component
public interface ArticleSearchDao extends ElasticsearchRepository<Article,String> {

	/**
	 * 检索
	 * @param title :搜索的标题
	 * @param content: 搜索的内容
	 * @return Article
	 */
	//List<Article> searchArticleByCondition(String title, String content);
}
