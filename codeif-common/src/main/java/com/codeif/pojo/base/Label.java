package com.codeif.pojo.base;

import com.codeif.pojo.BasePojo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Getter
@Setter
//@Entity
//@Table(name = "ba_dict")
public class Label extends BasePojo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("标签主键")
    @Column(name="id", unique=true, nullable=false, updatable=false, length = 20)
    private String id;

    @ApiModelProperty("标签名称")
    private String labelName;
    @ApiModelProperty("状态")
    private String state;
    @ApiModelProperty(value = "使用数量",example = "1")
    private Long count;
    @ApiModelProperty(value = "关注数",example = "1")
    private Long fans;
    @ApiModelProperty("是否推荐")
    private String recommend;

}