package cn.coderap.user.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * user实体类
 */
@Data
@Table(name="tb_user")
public class User implements Serializable {

	@Id
	private String username;//用户名
	private String password;//密码，加密存储
	private String phone;//注册手机号
	private String email;//注册邮箱
	private java.util.Date created;//创建时间
	private java.util.Date updated;//修改时间
	private String sourceType;//会员来源：1:PC，2：H5，3：Android，4：IOS
	private String nickName;//昵称
	private String name;//真实姓名
	private String status;//使用状态（1正常 0非正常）
	private String headPic;//头像地址
	private String qq;//QQ号码
	private String isMobileCheck;//手机是否验证 （0否  1是）
	private String isEmailCheck;//邮箱是否检测（0否  1是）
	private String sex;//性别，1男，0女
	private Integer userLevel;//会员等级
	private Integer points;//积分
	private Integer experienceValue;//经验值
	private java.util.Date birthday;//出生年月日
	private java.util.Date lastLoginTime;//最后登录时间
}
