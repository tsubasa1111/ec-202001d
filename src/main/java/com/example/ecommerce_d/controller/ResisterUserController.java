package com.example.ecommerce_d.controller;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.ecommerce_d.domain.User;
import com.example.ecommerce_d.form.UserResisterForm;
import com.example.ecommerce_d.service.ResisterUserService;

/**
 *ユーザー登録の情報制御をするコントローラークラスです.
 * 
 * @author sanihiro
 *
 */
@Controller
@RequestMapping("")
public class ResisterUserController {
	@ModelAttribute
	public UserResisterForm setUpForm() {
		return new UserResisterForm();
	}
	
	@Autowired
	private ResisterUserService service;
	
	/**
	 * ユーザー登録画面へ遷移します.
	 * 
	 * @return ユーザー登録画面
	 */
	@RequestMapping("/show-resister")
	public String showResister() {
		return "register_user";
	}
	
	/**
	 * 入力した値をフォームで受け取り、そこからユーザードメインを作成してDBに新規作成する.
	 * 
	 * @param form ユーザー登録のフォーム
	 * @return パスワードと確認用パスワードが一致していれば、ログイン画面。
	 *		　　そうでなければ、登録画面。
	 */
	@RequestMapping("/resister-user")
	public String resisterUser(UserResisterForm form) {
		System.out.println("------------------------");
		System.out.println(form);
		User user = new User();
		BeanUtils.copyProperties(form, user);
		System.out.println("------------------------");
		System.out.println(user);
//		パスワードと確認用パスワードが一致していなければエラーメッセージを出す記述をする
		if(!(user.getPassword().equals(form.getConfirmPassword()))) {
			return "resister_user";
		}
		service.insert(user);
		return "login";
		
	}
}
