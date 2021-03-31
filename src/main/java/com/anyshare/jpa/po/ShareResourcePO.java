package com.anyshare.jpa.po;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Eden
 * @date 2020/07/25
 */
@Entity
@Data
@Table(name = "t_share_resource")
public class ShareResourcePO extends BasePO {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String weixinPushContent;

    @Column
    private String appTag;

    @Override
    protected void init() {
        this.setName("");
        this.setContent("");
        this.setWeixinPushContent("");
        this.setAppTag("");
    }
}
