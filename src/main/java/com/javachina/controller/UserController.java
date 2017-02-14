package com.javachina.controller;

import com.blade.Blade;
import com.blade.ioc.annotation.Inject;
import com.blade.jdbc.core.Take;
import com.blade.jdbc.model.Paginator;
import com.blade.kit.DateKit;
import com.blade.kit.EncrypKit;
import com.blade.kit.PatternKit;
import com.blade.kit.StringKit;
import com.blade.mvc.annotation.Controller;
import com.blade.mvc.annotation.PathParam;
import com.blade.mvc.annotation.Route;
import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.view.ModelAndView;
import com.blade.patchca.DefaultPatchca;
import com.blade.patchca.Patchca;
import com.javachina.Actions;
import com.javachina.Constant;
import com.javachina.Types;
import com.javachina.kit.SessionKit;
import com.javachina.kit.Utils;
import com.javachina.model.*;
import com.javachina.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
public class UserController extends BaseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Inject
	private SettingsService settingsService;
	
	@Inject
	private ActivecodeService activecodeService;
	
	@Inject
	private UserService userService;
	
	@Inject
	private UserinfoService userinfoService;
	
	@Inject
	private CommentService commentService;
	
	@Inject
	private NoticeService noticeService;
	
	@Inject
	private FavoriteService favoriteService;
	
	@Inject
	private TopicService topicService;
	
	@Inject
	private UserlogService userlogService;

	private Patchca patchca = new DefaultPatchca();

	/**
	 * 获取验证码
	 */
	@Route(value = "/captcha", method = HttpMethod.GET)
	public void show_captcha(Request request, Response response){
		try {
			patchca.render(request, response);
		} catch (Exception e){

		}
	}
	
	/**
	 * 登录页面
	 */
	@Route(value = "/signin", method = HttpMethod.GET)
	public ModelAndView show_signin(Request request, Response response){
		return this.getView("signin");
	}
	
	/**
	 * 登录操作
	 */
	@Route(value = "/signin", method = HttpMethod.POST)
	public ModelAndView signin(Request request, Response response){
		
		String login_name = request.query("login_name");
		String pass_word = request.query("pass_word");
		String rememberme = request.query("rememberme");
		
		if(StringKit.isBlank(login_name) || StringKit.isBlank(pass_word)){
			request.attribute(this.ERROR, "用户名和密码不能为空");
			request.attribute("login_name", login_name);
			return this.getView("signin");
		}
		
		boolean hasUser = userService.hasUser(login_name);
		if(!hasUser){
			request.attribute(this.ERROR, "该用户不存在");
			request.attribute("login_name", login_name);
			return this.getView("signin");
		}
		
		User user = userService.signin(login_name, pass_word);
		if(null == user){
			request.attribute(this.ERROR, "用户名或密码错误");
			request.attribute("login_name", login_name);
			return this.getView("signin");
		}
		
		if(user.getStatus() == 0){
			request.attribute(this.ERROR, "该用户尚未激活，请登录邮箱激活帐号后登录");
			request.attribute("login_name", login_name);
			return this.getView("signin");
		}
		
		LoginUser loginUser = userService.getLoginUser(user, null);
		SessionKit.setLoginUser(request.session(), loginUser);
		if(StringKit.isNotBlank(rememberme) && rememberme.equals("on")){
			SessionKit.setCookie(response, Constant.USER_IN_COOKIE, loginUser.getUid());
		}
		
		userlogService.save(user.getUid(), Actions.SIGNIN, login_name);
		
		String val = SessionKit.getCookie(request, Constant.JC_REFERRER_COOKIE);
		if(StringKit.isNotBlank(val)){
			response.redirect(val);
		} else {
			response.go("/");
		}
		return null;
	}
	
	/**
	 * 注册页面
	 */
	@Route(value = "/signup", method = HttpMethod.GET)
	public ModelAndView show_signup(Request request, Response response){
		Object allow_signup = Constant.SYS_INFO.get(Types.allow_signup.toString());
		if(null != allow_signup && allow_signup.toString().equals("false")){
			request.attribute(this.INFO, "暂时停止注册");
		}
		return this.getView("signup");
	}
	
	/**
	 * 注销
	 */
	@Route(value = "/logout")
	public void logout(Request request, Response response){
		SessionKit.removeUser(request.session());
		SessionKit.removeCookie(response);
		response.go("/");
	}
	
	/**
	 * 检查是否有通知
	 */
	@Route(value = "/check_notice", method = HttpMethod.GET)
	public void check_notice(Request request, Response response){
		LoginUser user = SessionKit.getLoginUser();
		if(null == user){
			response.text("0");
			return;
		}

		Integer notices = noticeService.getNotices(user.getUid());
		response.text(notices.toString());
	}
	
	/**
	 * 通知列表
	 */
	@Route(value = "/notices", method = HttpMethod.GET)
	public ModelAndView notices(Request request, Response response){
		LoginUser user = SessionKit.getLoginUser();
		if(null == user){
			response.go("/signin");
			return null;
		}
		
		Integer page = request.queryAsInt("p");
		page = null == page ? 1 : page;
		
		Paginator<Map<String, Object>> noticePage = noticeService.getNoticePage(user.getUid(), page, 10);
		request.attribute("noticePage", noticePage);
		
		// 清空我的通知
		if(null != noticePage && noticePage.getList().size() > 0){
			noticeService.read(user.getUid());
		}
		
		return this.getView("notices");
	}
	
	/**
	 * 注册操作
	 */
	@Route(value = "/signup", method = HttpMethod.POST)
	public ModelAndView signup(Request request, Response response){
		
		String login_name = request.query("login_name");
		String email = request.query("email");
		String pass_word = request.query("pass_word");
		String auth_code = request.query("auth_code");
		
		request.attribute("login_name", login_name);
		request.attribute("email", email);
		
		if(StringKit.isBlank(login_name) || StringKit.isBlank(pass_word) 
				|| StringKit.isBlank(email) || StringKit.isBlank(auth_code) ){
			request.attribute(this.ERROR, "参数不能为空");
			return this.getView("signup");
		}
		
		if(login_name.length() > 16 || login_name.length() < 4){
			request.attribute(this.ERROR, "请输入4-16位用户名");
			return this.getView("signup");
		}
		
		if(!Utils.isLegalName(login_name)){
			request.attribute(this.ERROR, "请输入只包含字母／数字／下划线的用户名");
			return this.getView("signup");
		}
		
		if(!Utils.isSignup(login_name)){
			request.attribute(this.ERROR, "您的用户名中包含禁用字符，请修改后注册");
			return this.getView("signup");
		}
		
		if(!Utils.isEmail(email)){
			request.attribute(this.ERROR, "请输入正确的邮箱");
			return this.getView("signup");
		}
		
		if(pass_word.length() > 20 || pass_word.length() < 6){
			request.attribute(this.ERROR, "请输入6-20位字符的密码");
			return this.getView("signup");
		}
		
		String patchca = request.session().attribute("patchca");
		if(StringKit.isNotBlank(patchca) && !patchca.equalsIgnoreCase(auth_code)){
			request.attribute(this.ERROR, "验证码输入错误");
			return this.getView("signup");
		}
		
		Take queryParam = new Take(User.class);
		queryParam.eq("login_name", login_name);
		queryParam.in("status", 0, 1);
		User user = userService.getUser(queryParam);
		if(null != user){
			request.attribute(this.ERROR, "该用户名已经被占用，请更换用户名");
			return this.getView("signup");
		}
		
		queryParam = new Take(User.class);
		queryParam.eq("email", email);
		queryParam.in("status", 0, 1);
		user = userService.getUser(queryParam);
		if(null != user){
			request.attribute(this.ERROR, "该邮箱已经被注册，请直接登录");
			return this.getView("signup");
		}

		try {
			User user_ = userService.signup(login_name, pass_word, email);
			if(null != user_){
				userlogService.save(user_.getUid(), Actions.SIGNUP, login_name + ":" + email);
				request.attribute(this.INFO, "注册成功，已经向您的邮箱 " + email + " 发送了一封激活申请，请注意查收！");
			} else {
				request.attribute(this.ERROR, "注册发生异常");
			}
		} catch (Exception e){
			LOGGER.error("注册失败", e);
			request.attribute(this.ERROR, "注册失败");
		}
		return this.getView("signup");
	}
	
	/**
	 * 激活账户
	 */
	@Route(value = "/active/:code", method = HttpMethod.GET)
	public ModelAndView activeAccount(@PathParam("code") String code, Request request, Response response){
		Activecode activecode = activecodeService.getActivecode(code);
		if(null == activecode){
			request.attribute(this.ERROR, "无效的激活码");
			return this.getView("info");
		}
		
		Integer expries = activecode.getExpires_time();
		if(expries < DateKit.getCurrentUnixTime()){
			request.attribute(this.ERROR, "该激活码已经过期，请重新发送");
			return this.getView("info");
		}
		
		if(activecode.getIs_use() == 1){
			request.attribute(this.ERROR, "激活码已经被使用");
			return this.getView("info");
		}
		
		// 找回密码
		if(activecode.getType().equals(Types.forgot.toString())){
			request.attribute("code", code);
			return this.getView("reset_pwd");
		}

		try {
			userService.updateStatus(activecode.getUid(), 1);
			activecodeService.useCode(code);

			request.attribute(this.INFO, "激活成功，您可以凭密码登陆");
			settingsService.updateCount(Types.user_count.toString(), +1);
			Constant.SYS_INFO = settingsService.getSystemInfo();
			Constant.VIEW_CONTEXT.set("sys_info", Constant.SYS_INFO);

		} catch (Exception e){
			LOGGER.error("激活失败", e);
			request.attribute(this.ERROR, "激活失败");
		}
		return this.getView("active");
	}
	
	/**
	 * 忘记密码页面
	 */
	@Route(value = "/forgot", method = HttpMethod.GET)
	public ModelAndView show_forgot(Request request, Response response){
		return this.getView("forgot");
	}
	
	/**
	 * 忘记密码发送链接
	 */
	@Route(value = "/forgot", method = HttpMethod.POST)
	public ModelAndView forgot(Request request, Response response){
		String email = request.query("email");
		if(StringKit.isBlank(email)){
			request.attribute(this.ERROR, "邮箱不能为空");
			return this.getView("forgot");
		}
		
		if(!PatternKit.isEmail(email)){
			request.attribute(this.ERROR, "请输入正确的邮箱");
			request.attribute("email", email);
			return this.getView("forgot");
		}
		
		User user = userService.getUser(new Take(User.class).eq("email", email));
		if(null == user){
			request.attribute(this.ERROR, "该邮箱没有注册账户,请检查您的邮箱是否正确");
			request.attribute("email", email);
			return this.getView("forgot");
		}
		if(user.getStatus() == 0){
			request.attribute(this.ERROR, "该邮箱未激活");
			request.attribute("email", email);
			return this.getView("forgot");
		}
		try {
			String code = activecodeService.save(user, "forgot");
			if(StringKit.isNotBlank(code)){
				request.attribute(this.INFO, "修改密码链接已经发送到您的邮箱，请注意查收！");
			} else {
				request.attribute(this.ERROR, "找回密码失败");
			}
		} catch (Exception e){
			LOGGER.error("找回密码失败", e);
		}
		return this.getView("forgot");
	}
	
	/**
	 * 修改新密码
	 */
	@Route(value = "/reset_pwd", method = HttpMethod.POST)
	public ModelAndView reset_pwd(Request request, Response response){
		String code = request.query("code");
		String password = request.query("pass_word");
		String re_password = request.query("re_pass_word");
		
		if(StringKit.isBlank(code) || StringKit.isBlank(password) || StringKit.isBlank(re_password)){
			return null;
		}
		
		request.attribute("code", code);
		
		if(!password.equals(re_password)){
			request.attribute(this.ERROR, "两次密码不一致，请确认后提交");
			return this.getView("reset_pwd");
		}
		
		if(password.length() > 20 || password.length() < 6){
			request.attribute(this.ERROR, "请输入6-20位字符的密码");
			return this.getView("reset_pwd");
		}
		
		Activecode activecode = activecodeService.getActivecode(code);
		if(null == activecode || !activecode.getType().equals(Types.forgot.toString())){
			request.attribute(this.ERROR, "无效的激活码");
			return this.getView("reset_pwd");
		}

		Integer expries = activecode.getExpires_time();
		if(expries < DateKit.getCurrentUnixTime()){
			request.attribute(this.ERROR, "该激活码已经过期，请重新发送");
			return this.getView("reset_pwd");
		}
		
		if(activecode.getIs_use() == 1){
			request.attribute(this.ERROR, "激活码已经被使用");
			return this.getView("reset_pwd");
		}
		
		User user = userService.getUser(activecode.getUid());
		if(null == user){
			request.attribute(this.ERROR, "激活码已经被使用");
			return this.getView("reset_pwd");
		}
		
		String new_pwd = EncrypKit.md5(user.getLogin_name() + password);

		try {
			userService.updatePwd(user.getUid(), new_pwd);
			activecodeService.useCode(code);
			request.attribute(this.INFO, "密码修改成功，您可以直接登录！");
		} catch (Exception e){
			LOGGER.error("密码修改失败", e);
			request.attribute(this.ERROR, "密码修改失败");
		}
		return this.getView("reset_pwd");
	}
	
	/**
	 * 用户主页
	 */
	@Route(value = "/member/:username")
	public ModelAndView member(@PathParam("username") String username,
			Request request, Response response){

		Take up = new Take(User.class);
		up.eq("status", 1).eq("login_name", username);
		
		User user = userService.getUser(up);
		if(null == user){
			// 不存在的用户
			response.text("not found user.");
			return null;
		}
		
		Map<String, Object> profile = userService.getUserDetail(user.getUid());
		request.attribute("profile", profile);
		
		// 是否关注了该用户
		LoginUser loginUser = SessionKit.getLoginUser();
		if(null == loginUser){
			request.attribute("is_follow", false);
			SessionKit.setCookie(response, Constant.JC_REFERRER_COOKIE, request.url());
		} else {
			boolean is_follow = favoriteService.isFavorite(Types.following.toString(), loginUser.getUid(), user.getUid());
			request.attribute("is_follow", is_follow);
		}
		
		// 最新创建的主题
		Take tp = new Take(Topic.class);
		tp.eq("status", 1).eq("uid", user.getUid()).orderby("create_time desc, update_time desc").page(1, 10);
		Paginator<Map<String, Object>> topicPage = topicService.getPageList(tp);
		request.attribute("topicPage", topicPage);
		
		// 最新发布的回复
		Take cp = new Take(Comment.class);
		cp.eq("uid", user.getUid()).orderby("create_time desc").page(1, 10);
		Paginator<Map<String, Object>> commentPage = commentService.getPageListMap(cp);
		request.attribute("commentPage", commentPage);
		
		return this.getView("member_detail");
	}
	
	/**
	 * 我收藏的帖子
	 */
	@Route(value = "/my/topics")
	public ModelAndView myTopics(Request request, Response response){
		
		LoginUser user = SessionKit.getLoginUser();
		if(null == user){
			response.go("/");
			return null;
		}
		
		Integer page = request.queryAsInt("p");
		page = null == page ? 1 : page;
		
		Paginator<Map<String, Object>> favoritesPage = favoriteService.getMyTopics(user.getUid(), page, 10);
		request.attribute("favoritesPage", favoritesPage);
		
		return this.getView("my_topics");
	}
	
	/**
	 * 我收藏的节点
	 */
	@Route(value = "/my/nodes")
	public ModelAndView myNodes(Request request, Response response){
		
		LoginUser user = SessionKit.getLoginUser();
		if(null == user){
			response.go("/");
			return null;
		}
		
		List<Map<String, Object>> nodes = favoriteService.getMyNodes(user.getUid());
		request.attribute("nodes", nodes);
		
		return this.getView("my_nodes");
	}
	
	/**
	 * 我的关注
	 */
	@Route(value = "/my/following")
	public ModelAndView following(Request request, Response response){
		
		LoginUser user = SessionKit.getLoginUser();
		if(null == user){
			response.go("/");
			return null;
		}
		
		Integer page = request.queryAsInt("p");
		page = null == page ? 1 : page;

		Paginator<Map<String, Object>> followingPage = favoriteService.getFollowing(user.getUid(), page, 10);
		request.attribute("followingPage", followingPage);
		
		return this.getView("following");
	}
	

	/**
	 * 关注／收藏／点赞／下沉帖
	 */
	@Route(value = "/favorite", method = HttpMethod.POST)
	public void favorite(Request request, Response response){
		LoginUser user = SessionKit.getLoginUser();
		if(null == user){
			this.nosignin(response);
			return;
		}
		
		// topic：帖子，node：节点，love：喜欢，following：关注
		String type = request.query("type");
		Integer event_id = request.queryAsInt("event_id");
		
		if(StringKit.isBlank(type) || null == event_id || event_id == 0){
			return;
		}
		
		Integer count = favoriteService.update(type, user.getUid(), event_id);
		
		LoginUser loginUser = userService.getLoginUser(null, user.getUid());
		SessionKit.setLoginUser(request.session(), loginUser);
		
		this.success(response, count);
	}
	
	/**
	 * 个人设置
	 */
	@Route(value = "settings", method = HttpMethod.GET)
	public ModelAndView show_settings(Request request, Response response){
		
		LoginUser user = SessionKit.getLoginUser();
		if(null == user){
			response.go("/");
			return null;
		}
		Map<String, Object> profile = userService.getUserDetail(user.getUid());
		request.attribute("profile", profile);
		return this.getView("settings");
	}
	
	/**
	 * 个人设置
	 */
	@Route(value = "settings", method = HttpMethod.POST)
	public void settings(Request request, Response response){
		
		LoginUser loginUser = SessionKit.getLoginUser();
		if(null == loginUser){
			this.nosignin(response);
			return;
		}
		
		String type = request.query("type");
		if(StringKit.isBlank(type)){
			return;
		}
		
		String avatar = request.query("avatar");
		
		// 修改头像
		if(type.equals("avatar") && StringKit.isNotBlank(avatar)){
			try {
				String avatar_path = Blade.$().webRoot() + File.separator + avatar;
				userService.updateAvatar(loginUser.getUid(), avatar_path);
				
				LoginUser loginUserTemp = userService.getLoginUser(null, loginUser.getUid());
				SessionKit.setLoginUser(request.session(), loginUserTemp);
				
				this.success(response, "");
			} catch (Exception e) {
				e.printStackTrace();
				this.error(response, "头像更换失败");
			}
			return;
		}
		
		// 修改基本信息
		if(type.equals("info")){
			String nickName = request.query("nick_name");
			String jobs = request.query("jobs");
			String webSite = request.query("web_site");
			String github = request.query("github");
			String weibo = request.query("weibo");
			String location = request.query("location");
			String signature = request.query("signature");
			String instructions = request.query("instructions");
			
			try {
				boolean flag = userinfoService.update(loginUser.getUid(), nickName, jobs, webSite, github, weibo, location, signature, instructions);
				
				if(flag){
					LoginUser loginUserTemp = userService.getLoginUser(null, loginUser.getUid());
					SessionKit.setLoginUser(request.session(), loginUserTemp);
					this.success(response, "");
				} else {
					this.error(response, "修改失败");
				}
			} catch (Exception e) {
				e.printStackTrace();
				this.error(response, "修改失败");
			}
			return;
		}
				
		// 修改密码
		if(type.equals("pwd")){
			
			Map<String, Object> profile = userService.getUserDetail(loginUser.getUid());
			request.attribute("profile", profile);
			
			String curpwd = request.query("curpwd");
			String newpwd = request.query("newpwd");
			
			if(StringKit.isBlank(curpwd) || StringKit.isBlank(newpwd)){
				this.error(response, "参数不能为空");
				return;
			}
			
			if(!EncrypKit.md5(loginUser.getUser_name() + curpwd).equals(loginUser.getPass_word())){
				this.error(response, "旧密码输入错误");
				return;
			}
			
			try {
				String new_pwd = EncrypKit.md5(loginUser.getUser_name() + newpwd);
				userService.updatePwd(loginUser.getUid(), new_pwd);
				
				LoginUser loginUserTemp = userService.getLoginUser(null, loginUser.getUid());
				SessionKit.setLoginUser(request.session(), loginUserTemp);
				userlogService.save(loginUser.getUid(), Actions.UPDATE_PWD, new_pwd);
				
				this.success(response, "");
			} catch (Exception e) {
				e.printStackTrace();
				this.error(response, "密码修改失败");
			}
			return;
		}
		
	}
	
	/**
	 * 显示markdown预览
	 */
	@Route(value = "markdown", method = HttpMethod.POST)
	public void getMarkdown(Request request, Response response){
		LoginUser user = SessionKit.getLoginUser();
		if(null == user){
			response.text("");
			return;
		}
		String content = request.query("content");
		response.text(Utils.markdown2html(content));
	}
	
}
