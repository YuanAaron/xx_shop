package cn.coderap.user.dao;

import cn.coderap.user.pojo.User;
import org.apache.ibatis.annotations.Update;
import org.springframework.data.repository.query.Param;
import tk.mybatis.mapper.common.Mapper;

public interface UserMapper extends Mapper<User> {

    /**
     * 根据用户名修改积分
     * @param username
     * @param points
     */
    @Update("update tb_user set points=points+#{points} where username=#{username}")
    int addPoints(@Param("username") String username, @Param("points") Integer points);
}
