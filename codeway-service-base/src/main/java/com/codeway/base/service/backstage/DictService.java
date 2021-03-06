package com.codeway.base.service.backstage;


import com.codeway.base.dao.DictDao;
import com.codeway.base.mapper.DictMapper;
import com.codeway.exception.custom.ResourceNotFoundException;
import com.codeway.model.dto.base.DictDto;
import com.codeway.model.pojo.base.Dict;
import com.codeway.utils.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 字典接口实现
 **/
@Service
public class DictService {

	private final DictDao dictDao;
	private final DictMapper dictMapper;

	public DictService(DictDao dictDao,
	                   DictMapper dictMapper) {
		this.dictDao = dictDao;
		this.dictMapper = dictMapper;
	}

	/**
	 * 条件查询字典
	 *
	 * @param dictDto 字典实体
	 * @return List
	 */
	public Page<DictDto> findDictByCondition(DictDto dictDto, Pageable pageable) {
		Specification<Dict> condition = (root, query, builder) -> {
			List<javax.persistence.criteria.Predicate> predicates = new ArrayList<>();
			if (StringUtils.isNotEmpty(dictDto.getName())) {
				predicates.add(builder.like(root.get("name"), "%" + dictDto.getName() + "%"));
			}
			if (StringUtils.isNotEmpty(dictDto.getParentId())) {
				predicates.add(builder.equal(root.get("parentId"), dictDto.getParentId()));
			}
			return query.where(predicates.toArray(new javax.persistence.criteria.Predicate[0])).getRestriction();
		};
		return dictDao.findAll(condition, pageable).map(dictMapper::toDto);
    }

	/**
	 * 按照字典类型获取树形字典
	 *
	 * @param dictDto 字典实体
	 * @return List
	 */
	public List<DictDto> fetchDictTreeList(DictDto dictDto) {
		return dictDao.findAllByType(dictDto.getType())
				.map(dictMapper::toDto)
				.orElseThrow(ResourceNotFoundException::new);
	}

	public DictDto findDictById(String resId) {
		return dictDao.findById(resId)
				.map(dictMapper::toDto)
				.orElseThrow(ResourceNotFoundException::new);
	}

	public void saveOrUpdate(DictDto dictDto) {
		if (StringUtils.isNotBlank(dictDto.getId())) {
			Dict tempDict = dictDao.findById(dictDto.getId()).orElseThrow(ResourceNotFoundException::new);
			BeanUtil.copyProperties(tempDict, dictDto);
		}
		dictDao.save(dictMapper.toEntity(dictDto));
	}

	public void deleteBatch(List<String> resId) {
		dictDao.deleteBatch(resId);
	}

	/**
	 * 获取组字典类型，所有根节点
	 *
	 * @param dict 资源实体
	 * @return JsonData
	 */
	public List<DictDto> findIdNameTypeByParentId(DictDto dictDto) {
		return dictDao.findByParentId("0")
				.map(dictMapper::toDto)
				.orElseThrow(ResourceNotFoundException::new);
	}
}
