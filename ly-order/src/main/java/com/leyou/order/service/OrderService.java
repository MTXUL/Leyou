package com.leyou.order.service;

import com.leyou.auth.properties.UserInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.IdWorker;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Stock;
import com.leyou.order.clients.AddressClient;
import com.leyou.order.clients.GoodClients;
import com.leyou.order.dto.AddressDTO;
import com.leyou.order.dto.CartDTO;
import com.leyou.order.dto.OrderDTO;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.enums.PayState;
import com.leyou.order.interceptor.UserInterceptor;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.mapper.StockMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.OrderStatus;
import com.leyou.order.utils.PayHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private GoodClients goodClients;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderStatusMapper orderStatusMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private PayHelper payHelper;
    @Transactional
    public Long createOrder(OrderDTO orderDTO) {

        //1订单
        Order order = new Order();
//        1.1订单id
        long id = idWorker.nextId();
        order.setOrderId(id);
//         1.2订单金额
        Map <Long, Integer> map = orderDTO.getCarts().stream().collect(Collectors.toMap(CartDTO::getSkuId, CartDTO::getNum));
        Set <Long> longs = map.keySet();
        List <Sku> skus = goodClients.querySkusByIds(new ArrayList <>(longs));
        Long totalPrice=0L;
        ArrayList <OrderDetail> orderDetails = new ArrayList <>();
        for (Sku sku : skus) {
            totalPrice+=sku.getPrice()*map.get(sku.getId());
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(id);
            orderDetail.setImage(StringUtils.substringBefore(sku.getImages(),","));
            orderDetail.setNum(map.get(sku.getId()));
            orderDetail.setOwnSpec(sku.getOwnSpec());
            orderDetail.setTitle(sku.getTitle());
            orderDetail.setSkuId(sku.getId());
            orderDetail.setPrice(sku.getPrice());
            orderDetails.add(orderDetail);
        }
        order.setTotalPay(totalPrice);
        //实际支付总金额减去优惠券加上邮费
        order.setActualPay(totalPrice+order.getPostFee());
        //直飞类型
        order.setPaymentType(1);
        order.setCreateTime(new Date());
//          1.3 用户信息
        UserInfo user = UserInterceptor.getLoginUser();
        order.setBuyerNick(user.getName());
        order.setBuyerRate(false);
        order.setUserId(user.getId());
        order.setInvoiceType(0);
        //        1.4地址
        AddressDTO address = AddressClient.findById(orderDTO.getAddressId());
        order.setReceiver(address.getName());
        order.setReceiverAddress(order.getReceiverAddress());
        order.setReceiverCity(address.getCity());
        order.setReceiverDistrict(address.getDistrict());
        order.setReceiverMobile(address.getPhone());
        order.setReceiverZip(address.getZipCode());
        order.setReceiverState(address.getState());
//        订单详情（sku）
        order.setOrderDetails(orderDetails);
        int i = orderDetailMapper.insertList(orderDetails);
        if (orderDetails.size()!=i) {
            log.error("【订单详情保存失败】");
            throw new LyException(ExceptionEnum.ORDER_CREATED_FAIL);
        }
       

//        订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setCreateTime(new Date());
        orderStatus.setOrderId(id);
        orderStatus.setStatus(OrderStatusEnum.UN_PAY.getStatus());
        order.setOrderStatus(orderStatus);
        int insert = orderStatusMapper.insert(orderStatus);
        if (insert!=1) {
            log.error("【订单状态保存失败】");
            throw new LyException(ExceptionEnum.ORDER_CREATED_FAIL);
        }
        //库存
        for (Sku sku : skus) {
            Stock stock = stockMapper.selectByPrimaryKey(sku.getId());
            int i1 = stockMapper.descStock(map.get(sku.getId()), sku.getId());
            if (i1!=1){
                log.error("【订单库存保存失败】");
                throw new LyException(ExceptionEnum.ORDER_CREATED_FAIL);
            }
        }
        orderMapper.insert(order);
        return order.getOrderId();
    }

    public Order queryOrderById(Long id) {
        Order order = orderMapper.selectByPrimaryKey(id);
        if (order==null) {
            log.error("【订单未能查到】");
            throw new LyException(ExceptionEnum.Order_Select_FAIL);
        }
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(id);
        List <OrderDetail> orderDetails = orderDetailMapper.select(orderDetail);
        if (CollectionUtils.isEmpty(orderDetails)) {
            log.error("【订单详情未能查到】");
            throw new LyException(ExceptionEnum.Order_Select_FAIL);
        }
        order.setOrderDetails(orderDetails);
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(id);
        if (orderStatus==null) {
            log.error("【订单状态未能查到】");
            throw new LyException(ExceptionEnum.Order_Select_FAIL);
        }
        order.setOrderStatus(orderStatus);
        return order;
    }

    public Integer queryStateById(Long id) {
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(id);
        if (orderStatus==null){
            log.error("【订单状态未能查到】");
            throw new LyException(ExceptionEnum.Order_Select_FAIL);
        }
        System.out.println(orderStatus.getStatus());
        return orderStatus.getStatus();
    }

    public String queryUrlById(Long id) {
        Order order = queryOrderById(id);

        // 判断订单状态
        Integer status = order.getOrderStatus().getStatus();
        if (status != OrderStatusEnum.UN_PAY.getStatus()) {
            throw new LyException(ExceptionEnum.Order_Select_FAIL);
        }
        //Long actualPay = order.getActualPay();
        Long actualPay = 1L;
        String title = order.getOrderDetails().get(0).getTitle();
        return payHelper.createPayUrl(id, actualPay, title);
    }

    public void handleNotify(Map<String, String> result) {
        // 数据校验
        payHelper.isSuccess(result);
        // 校验签名
        payHelper.isValidSign(result);

        String totalFeeStr = result.get("total_fee");
        String tradeNoStr = result.get("out_trade_no");
        if (StringUtils.isBlank(tradeNoStr) || StringUtils.isBlank(totalFeeStr)) {
            throw new LyException(ExceptionEnum.INVALID_ORDER_PARAM);
        }
        Long totalFee = Long.valueOf(totalFeeStr);
        Long orderId = Long.valueOf(tradeNoStr);
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw new LyException(ExceptionEnum.INVALID_ORDER_PARAM);
        }
        if (totalFee != 1L) {
            // 金额不符
            throw new LyException(ExceptionEnum.INVALID_ORDER_PARAM);
        }
        // 修改订单状态
        OrderStatus status = new OrderStatus();
        status.setStatus(OrderStatusEnum.PAY_UN_GET.getStatus());
        status.setOrderId(orderId);
        status.setPaymentTime(new Date());
        int count = orderStatusMapper.updateByPrimaryKeySelective(status);
        if (count != 1) {
            throw new LyException(ExceptionEnum.UPDATE_ORDER_STATUS_ERROR);
        }
        log.info("[订单回调, 订单支付成功!], 订单编号:{}", orderId);
    }

    public PayState queryOrderState(Long orderId) {
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(orderId);
        Integer status = orderStatus.getStatus();
        if (status != OrderStatusEnum.UN_PAY.getStatus()) {
            // 如果已支付, 真的是已支付
            return PayState.SUCCESS;
        }
        // 如果未支付, 但其实不一定是未支付, 必须去微信查询支付状态
        return payHelper.queryPayState(orderId);
    }
}
