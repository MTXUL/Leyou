package com.leyou.item.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Table(name = "tb_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    @NotEmpty(message ="用户不能为空")
    @Length(min = 4,max = 32,message = "请输入4-32位的用户名")
    private String username;// 用户名

    @JsonIgnore
    private String password;// 密码

    @Pattern(regexp ="^[1](([3|5|8][\\d])|([4][4,5,6,7,8,9])|([6][2,5,6,7])|([7][^9])|([9][1,8,9]))[\\d]{8}$")
    private String phone;// 电话

    private Date created;// 创建时间

    @JsonIgnore
    private String salt;// 密码的盐值

}