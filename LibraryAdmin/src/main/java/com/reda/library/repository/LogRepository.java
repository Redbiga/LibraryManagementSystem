package com.reda.library.repository;

import com.read.library.entitys.Log;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 日志信息数据库操作接口
 * @author redA
 */
public interface LogRepository extends JpaRepository<Log, Integer> {
}
