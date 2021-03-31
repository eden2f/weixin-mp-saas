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
@Table(name = "t_app_open_api")
public class AppOpenApiConfigPO extends BasePO {

    @Column(nullable = false, unique = true)
    private String appTag;

    @Column(nullable = false, unique = true)
    private String appid;

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


    @Override
    protected void init() {
        this.setAppTag("");
        this.setAppid("");
        this.setSecret("");
        this.setToken("");
        this.setAesKey("");
        this.setVerifyKey("");
        this.setVerifyValue("");
    }
}
