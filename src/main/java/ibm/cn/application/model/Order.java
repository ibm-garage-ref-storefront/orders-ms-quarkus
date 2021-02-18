package ibm.cn.application.model;

import java.util.Date;

public class Order {
	
	private String id;
	private Date date;
	private int itemId;
	private String customerId;
	private int count;
	
	public Order() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Order(String id, Date date, int itemId, String customerId, int count) {
		super();
		this.id = id;
		this.date = date;
		this.itemId = itemId;
		this.customerId = customerId;
		this.count = count;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", date=" + date + ", itemId=" + itemId + ", customerId=" + customerId + ", count="
				+ count + "]";
	}

}
