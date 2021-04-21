package com.anyshare.jpa.mysql.po;

import com.anyshare.enums.DelStatus;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

/**
 * @author Eden
 * @date 2020/07/25
 */
@Getter
@Setter
@ToString
@MappedSuperclass
@NoArgsConstructor
public abstract class BasePO {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Date createTime;

    @Column(nullable = false)
    private String createUser;

    @Column(nullable = false)
    private Date updateTime;

    @Column
    private String updateUser;

    /**
     * {@link com.anyshare.enums.DelStatus}
     */
    @Column(nullable = false)
    private Integer delStatus;

    public void initBase() {
        this.setCreateTime(new Date());
        this.setCreateUser("SYSTEM");
        this.setUpdateTime(new Date());
        this.setUpdateUser("SYSTEM");
        this.setDelStatus(DelStatus.VALID.getCode());
    }

    protected abstract void init();

    public static <T extends BasePO> T createDefault(Class<T> clazz) {
        T t;
        try {
            t = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        t.initBase();
        t.init();
        return t;
    }


}
