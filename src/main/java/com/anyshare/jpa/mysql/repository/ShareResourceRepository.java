package com.anyshare.jpa.mysql.repository;

import com.anyshare.jpa.mysql.po.ShareResourcePO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Eden
 * @date 2020/07/25
 */
public interface ShareResourceRepository extends JpaRepository<ShareResourcePO, Long> {

    List<ShareResourcePO> findTop6ByAppTagAndNameContaining(String appTag, String name);

    Optional<ShareResourcePO> findTopByAppTagAndName(String appTag, String name);
}