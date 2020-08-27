package org.tikim.boot.mapper;

import org.apache.ibatis.annotations.*;
import org.tikim.boot.domain.entity.User;

@Mapper
public interface UserMapper {
    public User selectUser(@Param("id") Long id);
    public User selectUserByAccount(@Param("account") String account);
    public User selectUserByStudentEmail(@Param("student_email") String schoolEmail);
}
