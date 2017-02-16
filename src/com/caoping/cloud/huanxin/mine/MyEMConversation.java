package com.caoping.cloud.huanxin.mine;


import com.caoping.cloud.entiy.Member;
import com.hyphenate.chat.EMConversation;

/**
 * Created by Administrator on 2015/3/5.
 * 继承草坪云提供的EMConversation，增加emp属性，以及getter and setter 方法
 */
public class MyEMConversation {


    private Member emp;

    private EMConversation emConversation;

    public EMConversation getEmConversation() {
        return emConversation;
    }

    public void setEmConversation(EMConversation emConversation) {
        this.emConversation = emConversation;
    }

    public Member getEmp() {
        return emp;
    }

    public void setEmp(Member emp) {
        this.emp = emp;
    }
}
