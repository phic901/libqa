package com.libqa.repository;

import com.libqa.domain.UserKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by yong on 15. 2. 8..
 */
public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {
}