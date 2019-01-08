package com.tongbanjie.rocketmqConsole.util;

import com.tongbanjie.sso.model.TbjUser;
import com.tongbanjie.sso.utils.SSOUtils;

/**
 * 获取用户权限
 * 
 * @author LINLIN
 */
public class UserUtils {

	private static String SYSTEM_OPERATOR_NAME = "SYSTEM";

	/**
	 * 获取操作员信息
	 */
	public static TbjUser getUserInfo() {
		return SSOUtils.getUser();
	}

	/**
	 * 获取用户id
	 */
	public static Integer getUserId() {
		return SSOUtils.getUser().getId();
	}

	/**
	 * 获取操作员姓名
	 */
	public static String getUserName() {
		TbjUser user = SSOUtils.getUser();
		if (user == null) {
			return SYSTEM_OPERATOR_NAME;
		}
		return user.getUsername();
	}

	/**
	 * 获取用户真实姓名
	 */
	public static String getEmpRealName() {
		TbjUser user = SSOUtils.getUser();
		if (user == null) {
			return SYSTEM_OPERATOR_NAME;
		}
		return user.getEmpRealName();
	}

	/**
	 * 获取用户花名
	 */
	public static String getAliasName() {
		TbjUser user = SSOUtils.getUser();
		if (user == null) {
			return SYSTEM_OPERATOR_NAME;
		}
		return user.getAlias();
	}

	// 0~7 00
	// 8~15 08
	// 16~23 16
	// 24~31 24
	// 32~39 32
	// 40~47 40
	// 48~55 48
	// 56~63 56
	public static void main(String[] args) {
		int userArray[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
				30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60,
				61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91,
				92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112 };

		for (int i = 0; i < userArray.length; i++) {
			int mod = userArray[i] % 64;
			if (mod >= 0 && mod <= 7) {
				System.out.println("00库key:" + userArray[i] + "---->mod:" + mod);
			} else if (mod >= 8 && mod <= 15) {
				System.out.println("08库key:" + userArray[i] + "---->mod:" + mod);
			} else if (mod >= 16 && mod <= 23) {
				System.out.println("16库key:" + userArray[i] + "---->mod:" + mod);
			} else if (mod >= 24 && mod <= 31) {
				System.out.println("24库key:" + userArray[i] + "---->mod:" + mod);
			} else if (mod >= 32 && mod <= 39) {
				System.out.println("32库key:" + userArray[i] + "---->mod:" + mod);
			} else if (mod >= 40 && mod <= 47) {
				System.out.println("40库key:" + userArray[i] + "---->mod:" + mod);
			} else if (mod >= 48 && mod <= 55) {
				System.out.println("48库key:" + userArray[i] + "---->mod:" + mod);
			} else if (mod >= 56 && mod <= 63) {
				System.out.println("56库key:" + userArray[i] + "---->mod:" + mod);
			}

		}

		System.out.println(userArray.length + "*******************************");
	}
}
