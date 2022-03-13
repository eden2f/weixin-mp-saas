package com.anyshare.jpa.mysql.po;

import com.anyshare.enums.DelStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Eden
 * @date 2020/07/25
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = "t_app_open_api")
public class AppOpenApiConfigPO extends BasePO {

    @Column(nullable = false, unique = true)
    private String appTag;

    @Column(nullable = false, unique = true)
    private String appId;

    @Column(nullable = false)
    private String secret;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private String aesKey;

    @Column(nullable = false)
    private String verifyKey;

    @Column(nullable = false)
    private String verifyValue;

    /**
     * 公众号分流开关 (0: 关; 1: 开; 默认: 1)
     */
    @Column(nullable = false)
    private Integer drainageEnable;

    @Override
    protected void init() {
        this.setAppTag("");
        this.setAppId("");
        this.setSecret("");
        this.setToken("");
        this.setAesKey("");
        this.setVerifyKey("");
        this.setVerifyValue("");
        this.setDrainageEnable(DelStatus.DELETED.getCode());
    }

    public boolean drainageEnable() {
        return null != drainageEnable && DelStatus.DELETED.getCode() == drainageEnable;
    }
}
