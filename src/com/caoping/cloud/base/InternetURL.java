package com.caoping.cloud.base;

/**
 * Created by zhanghl on 2015/1/12.
 */
public class InternetURL {
    //mob
    public static final String APP_MOB_KEY = "18c32b58b4f3a";
    public static final String APP_MOB_SCRECT = "29a1e66e03fb499097a21dd15a662548";

    public static final String WEIXIN_APPID = "wx6ce53935b8d58010";
    public static final String WEIXIN_SECRET = "85977cd5719d1aebb5dd6ccaa4fe5bff";
    public static final String WX_API_KEY = "85977cd5719d1aebb5dd6ccaa4fe5bfe";

        public static final String INTERNAL = "http://139.196.169.8:8080/";
//        public static final String INTERNAL = "http://192.168.0.225:8080/";
    //多媒体文件上传接口
    public static final String UPLOAD_FILE = INTERNAL + "uploadImage.do";
    //多媒体文件上传接口
//    public static final String UPLOAD_TOKEN = INTERNAL + "token.json";
//    public static final String QINIU_SPACE = "paopao-pic";
    //登录
    public static final String LOGIN__URL = INTERNAL + "memberLogin.do";
    //注册第一步
    public static final String REG_ONE__URL = INTERNAL + "memberRegister.do";
    //根据手机号查询用户是否存在
    public static final String GET_EMP_MOBILE = INTERNAL + "getMemberByMobile.do";
    //更新密码-通过手机号更新密码
    public static final String UPDATE_PWR_MOBILE_URL = INTERNAL + "resetPassByMobile.do";
    //更新密码--通过用户ID更新密码
    public static final String UPDATE_PWR__URL = INTERNAL + "resetPass.do";
    //更新头像
    public static final String UPDATE_INFO_COVER_URL = INTERNAL + "modifyMember.do";
    //更改性别
    public static final String UPDATE_INFO_SEX_URL = INTERNAL + "modifyMemberSex.do";
    //更新生日
    public static final String UPDATE_INFO_BIRTH_URL = INTERNAL + "modifyMemberBirth.do";
    // 跟新手机号
    public static final String UPDATE_INFO_MOBILE_URL = INTERNAL + "resetMobile.do";
    //更新支付密码
    public static final String UPDATE_INFO_PAY_PASS_URL = INTERNAL + "modifyMemberPayPass.do";

    //我的收货地址列表
    public static final String MINE_ADDRSS = INTERNAL + "listShoppingAddress.do";
    //添加收货地址
    public static final String ADD_MINE_ADDRSS = INTERNAL + "saveShoppingAddress.do";
    //更新收货地址
    public static final String UPDATE_MINE_ADDRSS = INTERNAL + "updateShoppingAddress.do";
    //删除收货地址
    public static final String DELETE_MINE_ADDRSS = INTERNAL + "deleteShoppingAddress.do";
    //获得默认收货地址
    public static final String GET_MOREN_ADDRESS = INTERNAL + "getSingleAddressByEmpId.do";
    //添加收货地址--选择省份--城市--地区
    public static final String SELECT_PROVINCE_ADDRESS = INTERNAL + "appGetProvince.do";
    public static final String SELECT_CITY_ADDRESS = INTERNAL + "appGetCity.do";
    public static final String SELECT_AREA_ADDRESS = INTERNAL + "appGetArea.do";
    //上传经纬度
    public static final String SEND_LOCATION_BYID_URL = INTERNAL + "sendLocation.do";
    //申请入驻
    public static final String EMP_APPLY_DIANPU_URL = INTERNAL + "saveManagerInfo.do";

    //获得商品分类
    public static final String GET_GOODS_TYPE_URL = INTERNAL + "appGetGoodsType.do";

    //获得附近首页商店列表
    public static final String GET_DIANPU_LISTS = INTERNAL + "appGetNearbyDianpu.do";
    //获得推荐产品
    public static final String GET_TUIJIAN_LISTS = INTERNAL + "getTuijianProduct.do";
    //查询店铺详情
    public static final String GET_DIPU_DETAIL_LISTS = INTERNAL + "appGetDianpuDetailByEmpId.do";
    //查询店铺广告轮播图
    public static final String GET_DIPU_ADS_LISTS = INTERNAL + "appGetAdEmp.do";
    //查询商品详情
    public static final String GET_GODS_DETAIL_LISTS = INTERNAL + "paopaogoods/findById.do";
    //查询商品评论
    public static final String GET_GOODS_COMMENT_LISTS = INTERNAL + "listGoodsComment.do";
    //广告轮播
    public static final String GET_AD_LIST_TYPE_LISTS = INTERNAL + "appGetAdByType.do";
    //获取商品列表
    public static final String GET_GOODS_URL = INTERNAL + "paopaogoods/listGoods.do";


    //传订单给服务端--生成订单
    public static final String SEND_ORDER_TOSERVER = INTERNAL + "orderSave.do";
    //更新订单状态
    public static final String UPDATE_ORDER_TOSERVER = INTERNAL + "orderUpdate.do";
    //查询订单列表
    public static final String MINE_ORDERS_URL = INTERNAL + "listOrders.do";
    //更新订单
    public static final String UPDATE_ORDER = INTERNAL + "updateOrder.do";
    //去付款--单个订单付款-支付宝
    public static final String SAVE_ORDER_SIGNLE = INTERNAL + "orderSaveSingle.do";

    //更新订单状态
    public static final String UPDATE_ORDER_TOSERVER_SINGLE = INTERNAL + "orderUpdateSingle.do";
    //根据地址id，查询收货地址、
    public static final String GET_ADDRESS_BYID = INTERNAL + "getSingleAddressByAddressId.do";


    //收藏商品接口
    public static final String SAVE_FAVOUR = INTERNAL + "saveGoodsFavour.do";
    //收藏商品列表
    public static final String MINE_FAVOUR = INTERNAL + "listFavour.do";
    //删除收藏的商品
    public static final String DELETE_FAVOUR = INTERNAL + "deleteFavour.do";

    //删除店铺收藏
    public static final String DELETE_DIANPU_FAVOUR_URL = INTERNAL + "deleteDianpuFavour.do";
    //收藏店铺
    public static final String SAVE_FAVOUR_URL = INTERNAL + "saveDianpuFavour.do";
    //获得我的店铺收藏列表
    public static final String APP_GET_FAVOUR_DIANPU_URL = INTERNAL + "appGetDianpuFavour.do";

    //查询用户银行卡
    public static final String APP_GET_BANK_CARDS_URL = INTERNAL + "appGetBankCards.do";
    //保存银行卡信息
    public static final String APP_SAVE_BANK_CARDS_URL = INTERNAL + "appSaveBankCards.do";
    //删除银行卡 根据银行卡id
    public static final String APP_DELETE_BANK_CARDS_URL = INTERNAL + "deleteBankCard.do";
    //查询会员钱包信息
    public static final String APP_GET_PACKAGE_URL = INTERNAL + "appGetPackage.do";
    //版本检查
    public static final String CHECK_VERSION_CODE_URL = INTERNAL + "getVersionCode.do";


    //百度推送 绑定 get方法
    public static final String UPDATE_PUSH_ID = INTERNAL + "updatePushId.do";

    //查询我的浏览记录
    public static final String GET_BROWSING_ID = INTERNAL + "appGetBrowsing.do";
    //微信支付
    public static final String SEND_ORDER_TOSERVER_WX = INTERNAL + "orderSaveWx.do";

    //添加商品评论
    public static final String PUBLISH_GOODS_COMMNENT_URL = INTERNAL + "saveGoodsComment.do";
    //获得客户电话列表
    public static final String GET_KEFU_TEL_URL = INTERNAL + "getKefuTel.do";

    //推广注册
    public static final String APP_SHARE_REG_URL = INTERNAL + "appShareReg.do";

    //去付款--单个订单付款-微信
    public static final String SAVE_ORDER_SIGNLE_WX = INTERNAL + "orderSaveSingleWx.do";

    //确认收货 生成二维码用，卖家扫一扫确认发货 这个是面对面的，所以直接订单完成
    public static final String APP_SURE_FAHUO_URL = INTERNAL + "scanOrder.do";

    //查询我的點評
    public static final String GET_MINE_GOODS_COMMENT_LISTS = INTERNAL + "listGoodsComment.do";

    //更新订单--退货
    public static final String UPDATE_ORDER_RETURN = INTERNAL + "updateOrder.do";

    //获得会员的消费记录 ，钱包-》我的金币，点击查看消费详情
    public static final String GET_CONSUMPTION_RETURN = INTERNAL + "appGetConsumption.do";
    //我的积分记录
    public static final String GET_COUNT_RECORD_RETURN = INTERNAL + "appGetCountRecord.do";
    //查询我的积分
    public static final String GET_COUNT_RETURN = INTERNAL + "appGetCount.do";

    //提交提现申请
    public static final String SAVE_BANK_APPLY_RETURN = INTERNAL + "appSaveBankApply.do";
    //猜你喜欢的
    public static final String GET_LIKES_URN = INTERNAL + "appGetLikes.do";
    //获得定向卡会员详情
    public static final String GET_CARD_EMP_BY_ID_URN = INTERNAL + "appGetCardEmp.do";
    //商家有偿消费二维码url
    public static final String GET_GET_GOODS_URN = INTERNAL + "appPayYouchang.do";
    public static final String GET_GET_DXK_GOODS_URN = INTERNAL + "appPayWuchang.do";
    //商家无偿消费二维码url
    public static final String SAVE_DXK_ORDER_URN = INTERNAL + "appSaveDxkOrder.do";


    //获得头条列表
    public static final String GET_NEWS_URL = INTERNAL + "appGetNews.do";
    //保存头条信息
    public static final String SAVE_NEWS_URL = INTERNAL + "appSaveNews.do";
    //删除头条信息
    public static final String DELETE_NEWS_URL = INTERNAL + "appDeleteNews.do";
    //查询头条详情
    public static final String DETAIL_NEWS_URL = INTERNAL + "appGetNewsById.do";

    //头条分享
    public static final String SHARE_VIDEOS = INTERNAL + "appSaveDxkOrder.do";

    //获取头条信息评论
    public static final String GET_NEWS_COMMENT = INTERNAL + "appGetNewsComment.do";
    //保存头条信息评论
    public static final String SAVE_NEWS_COMMENT = INTERNAL + "appSaveNewsComment.do";
    //删除头条信息评论
    public static final String DELETE_NEWS_COMMENT = INTERNAL + "appDeleteNewsComment.do";

    //获取头条赞列表
    public static final String GET_NEWS_FAVOUR_LIST = INTERNAL + "appGetNewsFavour.do";
    //保存头条赞
    public static final String SAVE_NEWS_FAVOUR_URL = INTERNAL + "appSaveNewsFavour.do";
    //删除头条赞
    public static final String DELETE_NEWS_FAVOUR_URL = INTERNAL + "appDeleteNewsFavour.do";

    //获得草坪规格
    public static final String GET_CP_GUIGE_URL = INTERNAL + "appGetCpGuige.do";
    //获得草坪属性
    public static final String GET_CP_TYPE_URL = INTERNAL + "appGetCpType.do";
    //获得草坪用途
    public static final String GET_CP_USE_URL = INTERNAL + "appGetCpUse.do";
    //获取草坪列表
    public static final String GET_CP_LIST_URL = INTERNAL + "appGetCpLists.do";
    //查询草坪详情
    public static final String GET_CP_DETAIL_URL = INTERNAL + "appGetCpDetail.do";
    //保存草坪对象
    public static final String SAVE_CP_OBJ_URL = INTERNAL + "appSaveCpLists.do";

    //查询公司详情
    public static final String GET_COMPANY_DETAIL_URL = INTERNAL + "appGetCompanyDetail.do";
    //保存公司详情或者更新公司详情,上传公司坐标
    public static final String SAVE_COMPANY_DETAIL_URL = INTERNAL + "appSaveCompanyDetail.do";
    //更新公司图片
    public static final String UPDATE_COMPANY_DETAIL_PIC_URL = INTERNAL + "appSaveCompanyDetailPic.do";

    //3获得所有省份
    public static final String GET_PROVINCE_URL = INTERNAL + "appGetProvince.do";
    //4获得城市
    public static final String GET_CITY_URL = INTERNAL + "appGetCity.do";
    //5获得地区
    public static final String GET_COUNTRY_URL = INTERNAL + "appGetArea.do";
    //获得名企|公司|草原（公司）列表
    public static final String GET_COMPANY_MQ_URL = INTERNAL + "appGetCompanyList.do";

    //查询最新入驻企业
    public static final String appGetCompanyNews = INTERNAL + "appGetCompanyNews.do";

    //机械
    //获取机械规格
    public static final String GET_JIXIE_GUIGE_URL = INTERNAL + "appGetJixieGuige.do";
    //获取机械用途
    public static final String GET_JIXIE_USE_URL = INTERNAL + "appGetJixieUse.do";


    //获得草种规格
    public static final String GET_CZ_GUIGE_URL = INTERNAL + "appGetCaozhongGuige.do";
    // 获得草种属性
    public static final String GET_CZ_TYPE_URL = INTERNAL + "appGetCaozhongType.do";

    //申请供应商
    public static final String SAVE_APPLY_GYS_URL = INTERNAL + "appSaveApplyGys.do";
    //查询申请供应商信息
    public static final String GET_APPLY_GYS_URL = INTERNAL + "appGetApplyGysById.do";

    //查询车型
    public static final String GET_CAR_TYPE_URL = INTERNAL + "appGetCarType.do";
    //查询车长
    public static final String GET_CAR_LENGTH_URL = INTERNAL + "appGetCarLength.do";
    //保存物流信息（找车、找货）
    public static final String SAVE_TRANSPORT_URL = INTERNAL + "appSaveTransport.do";
    //查询物流信息
    public static final String GET_TRANSPORT_URL = INTERNAL + "appGetTransport.do";

    //根据环信IDS查询用户信息（查询返回的是列表）
    public static final String GET_INVITE_CONTACT_URL = INTERNAL + "appGetMembersByIds.do";
    //查询我的好友列表
    public static final String GET_MINE_CONTACTS_URL = INTERNAL + "appGetTransport.do";
    //商品详情页
    public static final String TO_DETAIL_GOODS = INTERNAL + "toDetailCp.do";

    //获得商品详情页好评度和消费评价总数
    public static final String GET_COMMENT_ALL_URN = INTERNAL + "appGetCountComment.do";
    //获得店铺详情页好评度和消费评价总数
    public static final String GET_COMMENT_ALL_DIANPU_URN = INTERNAL + "appGetCountCommentDianpu.do";

    //零钱支付方式(购物车生成订单 付款  直接购买的时候)
    public static final String SEND_ORDER_TOSERVER_LQ = INTERNAL + "orderSaveLq.do";
    //零钱支付方式 -- 单个付款--（我的订单，没支付的订单，去付款的时候）
    public static final String SAVE_ORDER_SIGNLE_LQ =INTERNAL +  "orderSaveSingleLq.do";
    //订单评价之后 更新订单状态
    public static final String UPDATE_ORDER_COMMENT =INTERNAL +  "orderUpdateComment.do";
    //查询店铺的评论列表
    public static final String GET_DIANPU_COMMENT_LISTS = INTERNAL + "listGoodsComment.do";
    //充值零钱
    public static final String appLqPayWx = INTERNAL + "appLqPayWx.do";
    public static final String appLqPayZfb = INTERNAL + "appLqPayZfb.do";
    //app更新零钱充值
    public static final String appUpdateLqCz = INTERNAL + "appUpdateLqCz.do";

    //用户删除产品接口
    public static final String appDeleteProductById = INTERNAL + "appDeleteProductById.do";
    //首页草坪数据统计
    public static final String appGetStatistical = INTERNAL + "appGetStatistical.do";

    //个人中心页面获得加入草坪云的天数 收入  消费额度等
    public static final String getProfileMemberInfo = INTERNAL + "getProfileMemberInfo.do";



    //通过手机号重设支付密码
    public static final String FIND_PWR_PAY__URL = INTERNAL + "findPwrPayByMobile.do";

    //获得店铺评论列表
    public static final String appGetDianpuComment = INTERNAL + "appGetDianpuComment.do";
    //保存店铺评论
    public static final String saveDianpuComment = INTERNAL + "saveDianpuComment.do";
    //删除店铺评论
    public static final String deleteDianpuComment = INTERNAL + "deleteDianpuComment.do";
}
