/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.shmalevoz._1c8fs;

/**
 * Вспомогательные методы
 * @author shmalevoz
 */
public class Utils {
	
	/**
	 * Значение корректной подписи заголовка
	 */
	public static final int SIGN = 0x7FFFFFFF;
	
	/**
	 * Возвращает признак корректности подписи заголовка
	 * @param sign - Проверяемая подпись
	 * @return Признак корректности
	 */
	public static boolean isValidSign(int sign) {
		return sign == SIGN;
	}
	
	public static void setLogLevel(java.util.logging.Logger log, String level) {
		
	}
	
}
