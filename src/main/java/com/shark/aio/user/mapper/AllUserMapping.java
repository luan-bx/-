package com.shark.aio.user.mapper;

import com.shark.aio.user.entity.PostEntity;
import com.shark.aio.user.entity.UserEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author lbx
 * @date 2023/3/28 - 14:28
 **/
public interface AllUserMapping {
    /*
     * 删除用户
     */
    @Delete("delete from `user` where user_name = #{userName}")
    void deleteUserByUserName(String userName);

    /*
     * 全部用户信息
     */
    @Select("select * from `user`;")
    List<UserEntity> getAll();

    /*
     * 20220823-thg,新用户信息
     */
    @Select("SELECT * FROM `user` WHERE `post_id`=16;")
    List<UserEntity> getNew();

    /*
     * 更新用户信息
     */
    @Update("UPDATE `user` SET `phone`=#{phone}, `email`=#{email} ,`number`=#{number}, "
            + " `post_name`=#{postName}"+ "WHERE `user_name`=#{originUserName};")
    void updateUserById(UserEntity userEntity,@Param("originUserName") String originUserName);


    @Select("select * from `post`")
    List<PostEntity> getAllPost();
}
