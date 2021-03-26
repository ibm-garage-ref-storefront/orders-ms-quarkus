package ibm.cn.application;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.jboss.logging.Logger;

@Path("/micro/orders/resource")
public class OrdersController {
	
	private static final Logger LOG = Logger.getLogger(OrdersController.class);
	
	@GET
    public String getRequest() {
		LOG.info("OrdersResource response");
        return "OrdersResource response";
    }

}
