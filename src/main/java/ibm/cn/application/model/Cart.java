package ibm.cn.application.model;

public class Cart {
	
	int price;

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}
	
	public Cart() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Cart(int price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "Order [price=" + price + "]";
	}

}
