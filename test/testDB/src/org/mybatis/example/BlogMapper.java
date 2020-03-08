package org.mybatis.example;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BlogMapper {
    Blog selectBlog(@Param("id") int id);
}
