package ibm.cn.application;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/micro/orders/resource")
public class OrdersController {
	
	@GET
    public String getRequest() {
        return "OrdersResource response";
    }

}
