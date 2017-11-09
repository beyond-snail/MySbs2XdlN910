package com.tool.utils.utils;

////////////////////////////////////////////////////////////////////
//                          _ooOoo_                               //
//                         o8888888o                              //
//                         88" . "88                              //
//                         (| ^_^ |)                              //
//                         O\  =  /O                              //
//                      ____/`---'\____                           //
//                    .'  \\|     |//  `.                         //
//                   /  \\|||  :  |||//  \                        //
//                  /  _||||| -:- |||||-  \                       //
//                  |   | \\\  -  /// |   |                       //
//                  | \_|  ''\---/''  |   |                       //
//                  \  .-\__  `-`  ___/-. /                       //
//                ___`. .'  /--.--\  `. . ___                     //
//              ."" '<  `.___\_<|>_/___.'  >'"".                  //
//            | | :  `- \`.;`\ _ /`;.`/ - ` : | |                 //
//            \  \ `-.   \_ __\ /__ _/   .-` /  /                 //
//      ========`-.____`-.___\_____/___.-`____.-'========         //
//                           `=---='                              //
//      ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^        //
//              佛祖保佑       永无BUG     永不修改                  //
//                                                                //
//          佛曰:                                                  //
//                  写字楼里写字间，写字间里程序员；                   //
//                  程序人员写程序，又拿程序换酒钱。                   //
//                  酒醒只在网上坐，酒醉还来网下眠；                   //
//                  酒醉酒醒日复日，网上网下年复年。                   //
//                  但愿老死电脑间，不愿鞠躬老板前；                   //
//                  奔驰宝马贵者趣，公交自行程序员。                   //
//                  别人笑我忒疯癫，我笑自己命太贱；                   //
//                  不见满街漂亮妹，哪个归得程序员？                   //
////////////////////////////////////////////////////////////////////

/**********************************************************
 *                                                        *
 *                  Created by wucongpeng on 2017/6/23.        *
 **********************************************************/


import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * 保留几位小数的工具类
 * Created by xrc on 2017/3/24.
 */

public class NumberInputHelper {

    /**
     * @param editText 控制的输入框
     * @param num      保留几位小数
     */
    public static void format(final EditText editText, final int num) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || s.length() == 0) {
                    return;
                }
                String price = editText.getText().toString();
                if (price.equals("0")) {
                    return;
                }
                if (!TextUtils.isEmpty(price)) {
                    if (price.startsWith(".")) {
                        editText.setText("");
                    } else {
                        String content = s == null ? null : s.toString();
                        int size = content.length();
                        //删除两个重复点
                        if (size >= 2 && content.startsWith("0") && content.substring(1, size).contains(".")) { //判断之前有没有输入过.
                            int dex = content.indexOf(".");
                            if (size - dex > 1) {
                                if (String.valueOf(content.charAt(dex + 1)).equals(".")) {
                                    s.delete(dex + 1, size);//删除重复输入的.
                                    return;
                                }
                            }
                        }
                        //删除重复0
                        if (Float.parseFloat(price) > 0) {
                            if (size >= 2 && content.startsWith("0") && !String.valueOf(content.charAt(1)).equals("0") && !String.valueOf(content.charAt(1)).equals(".")) { //判断之前有没有输入过0
                                s.delete(0, 1);//删除重复输入的0
                            }
                        } else if (Float.parseFloat(price) == 0) {
                            if (size >= 2 && content.startsWith("0") && String.valueOf(content.charAt(1)).equals("0")) { //判断之前有没有输入过0
                                s.delete(size - 1, size);//删除重复输入的0
                            }
                        }
                        //删除多余数字
                        if (size >= 2 && content.contains(".")) { //判断之前有没有输入过.
                            int dex = content.indexOf(".");
                            if (size - dex - 1 > num) {
                                s.delete(size - 1, size);
                                return;
                            }

                        }
                    }
                }
            }
        });
    }
}
