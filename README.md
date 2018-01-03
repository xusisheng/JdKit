# 京东工具包

### 开发计划
- [x] 账户密码登录
- [x] 二维码登录
- [x] 获取优惠券分类
- [x] 获取优惠券列表
- [x] 领取优惠券
- [x] 获取收货地址
- [x] 加入购物车
- [x] 选择或取消购物车中的商品
- [ ] 提交订单
- [ ] 价格保护
- [ ] 每日签到

### 使用说明
1. 初始化工具包，接入[打码平台](http://www.ysdm.net/)。
```java
KitFactory kit = new KitFactory(new Ocr("打码平台账号", "打码平台密码"));
```

2. 登录到京东。
```java
 SessionKit sessionKit = kit.getSessionKit();
 sessionKit.login("京东账号", "京东密码");
 //或者
 //sessionKit.qrcodeLogin("二维码保存位置");
```

3. 根据kit包内的方法进行想要的操作即可。

### 警告
仅供研究学习使用，请勿用于非法用途，因程序产生的一切问题与作者无关。