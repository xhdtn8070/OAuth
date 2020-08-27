package org.tikim.boot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.tikim.boot.domain.entity.DisposableSalt;

@Mapper
public interface DisposableSaltMapper {
    public DisposableSalt selectDisposableSalt(@Param("id") Long id);
    public DisposableSalt selectDisposableSaltByUnique(@Param("user_id") Long userId, @Param("subject") String subject);
}
