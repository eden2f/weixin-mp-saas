package com.anyshare.jpa.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

/**
 * @author Eden
 * @date 2020/07/25
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = "t_share_resource",
        indexes = {@Index(columnList = "appTag"), @Index(columnList = "name")})
public class ShareResourcePO extends BasePO {

    @Column(nullable = false)
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
