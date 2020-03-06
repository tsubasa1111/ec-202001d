package com.example.ecommerce_d.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerce_d.domain.Item;
import com.example.ecommerce_d.domain.Order;
import com.example.ecommerce_d.domain.OrderItem;
import com.example.ecommerce_d.domain.OrderTopping;
import com.example.ecommerce_d.form.AddItemForm;
import com.example.ecommerce_d.repository.ItemRepository;
import com.example.ecommerce_d.repository.JoinOrderRepository;
import com.example.ecommerce_d.repository.OrderItemRepository;
import com.example.ecommerce_d.repository.OrderRepository;
import com.example.ecommerce_d.repository.OrderToppingRepository;

@Service
@Transactional
public class ShoppingCartService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private OrderToppingRepository orderToppingRepository;
	
	@Autowired
	private ItemRepository itemRepository;


	@Autowired
	private JoinOrderRepository joinOrderRepository;



	/**
	 * ユーザーIDに紐づく注文履歴を表示します.
	 * 
	 * @param userId ユーザーID
	 * @return 注文情報リスト
	 */
	public Order showCartList(Integer userId) {
		List<Order> orderList = new ArrayList<>();
		// status = 4(配送完了)のリストを表示 ← 仕様に応じて要検討
		orderList = joinOrderRepository.findByUserIdAndStatus(userId, 0);
		Order order = orderList.get(0);
		
		return order;
	}

	/**
	 * 注文情報を追加します.
	 * 
	 * @param form 詳細画面のリクエストパラメータを受け取るフォーム
	 * @param userId　ユーザーID
	 */
	public void addItem(AddItemForm form, Integer userId) {
		Order order = new Order();
		order.setUserId(userId);
		order.setStatus(0);
		order.setTotalPrice(0);

		if (orderRepository.findByUserIdAndStatus(userId, 0) == null) {
			order = orderRepository.insert(order);
		} else {
			order = orderRepository.findByUserIdAndStatus(userId, 0);
		}

		OrderItem orderItem = new OrderItem();
		Integer intItemId = Integer.parseInt(form.getItemId());
		orderItem.setItemId(intItemId);
		orderItem.setOrderId(order.getId());
		orderItem.setSize(form.getSize().toCharArray()[0]);
		orderItem.setQuantity(Integer.parseInt(form.getQuantity()));
		orderItem = orderItemRepository.insert(orderItem);
		
		Item item = itemRepository.load(orderItem.getItemId());
		System.out.println("--------------------");
		System.out.println(orderItem.getSize());
		if(orderItem.getSize().equals('M')) {
			int itemTotalPrice = item.getPriceM() * orderItem.getQuantity();
			int nowTotalPrice = order.getTotalPrice();
			order.setTotalPrice(itemTotalPrice + nowTotalPrice);
			orderRepository.updateTotalPrice(order);
		}else if(orderItem.getSize().equals('L')){
			int itemTotalPrice = item.getPriceL() * orderItem.getQuantity();
			int nowTotalPrice = order.getTotalPrice();
			order.setTotalPrice(itemTotalPrice + nowTotalPrice);
			orderRepository.updateTotalPrice(order);
		}

		List<Integer> tooppingIntegerList = form.getTopping();
		if(form.getTopping() != null) {
			for (Integer toppingId : tooppingIntegerList) {
				OrderTopping orderTopping = new OrderTopping();
				orderTopping.setOrderItemId(orderItem.getId());
				orderTopping.setToppingId(toppingId);
				orderToppingRepository.insert(orderTopping);
				
				if(orderItem.getSize().equals('M')) {
					int toppingTotalPrice = tooppingIntegerList.size() * 200;
					int nowTotalPrice = order.getTotalPrice();
					order.setTotalPrice(toppingTotalPrice + nowTotalPrice);
					orderRepository.updateTotalPrice(order);
				}else if(orderItem.getSize().equals('L')) {
					int toppingTotalPrice = tooppingIntegerList.size() * 300;
					int nowTotalPrice = order.getTotalPrice();
					order.setTotalPrice(toppingTotalPrice + nowTotalPrice);
					orderRepository.updateTotalPrice(order);
				}
			}
		}
		

	}

	/**
	 * IDに紐づく注文アイテムと注文トッピングを注文から外します.
	 * 
	 * @param id ID
	 */
	public void removeOrderItemAndOrderTopping(Integer id) {
		orderItemRepository.deleteByID(id);
	}

//	public void removeOrderItem(Integer orderItemId) {
//		orderItemRepository.deleteById(orderItemId);
//		orderToppingRepository.deleteById(orderItemId);
//	}
}
