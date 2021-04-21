package com.anyshare.jpa.mysql.po;

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
@Table(name = "t_wx_mp_news_article",
        indexes = {@Index(columnList = "appTag"), @Index(columnList = "title")})
public class WxMpNewsArticlePO extends BasePO {

    /**
     * 图文消息的封面图片素材id（必须是永久mediaID）
     */
    private String thumbMediaId;
    private String thumbUrl;
    /**
     * 作者
     */
    private String author;
    /**
     * 图文消息的标题
     */
    @Column
    private String title;
    /**
     * 图文消息的原文地址，即点击“阅读原文”后的URL
     */
    private String contentSourceUrl;
    /**
     * 图文消息的具体内容，支持HTML标签，必须少于2万字符，小于1M，且此处会去除JS
     */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "LONGTEXT")
    private String content;
    /**
     * 图文消息的摘要，仅有单图文消息才有摘要，多图文此处为空
     */
    @Lob
    @Column(columnDefinition="TEXT")
    private String digest;
    /**
     * 是否显示封面，0为false，即不显示，1为true，即显示
     */
    private boolean showCoverPic;
    /**
     * 图文页的URL，或者，当获取的列表是图片素材列表时，该字段是图片的URL
     */
    private String url;
    private Boolean needOpenComment;
    private Boolean onlyFansCanComment;
    /**
     * content的md5值用于判断是否需要更新
     */
    private String contentMd5;

    @Column
    private String appTag;

    @Override
    protected void init() {
        this.setThumbMediaId("");
        this.setThumbUrl("");
        this.setAuthor("");
        this.setTitle("");
        this.setContentSourceUrl("");
        this.setContent("");
        this.setDigest("");
        this.setShowCoverPic(false);
        this.setUrl("");
        this.setNeedOpenComment(false);
        this.setOnlyFansCanComment(false);
        this.setContentMd5("");
        this.setAppTag("");
    }
}
