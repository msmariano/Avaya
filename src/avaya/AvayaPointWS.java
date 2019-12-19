package avaya;

import java.io.Serializable;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import entity.EndPoint;
import entity.Event;



@Path("/ramal")
public class AvayaPointWS  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6174439580745715929L;

	@POST
	@Path("/eventos")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(value = MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response eventos(EndPoint endPoint) {
		
		//Log.grava(endPoint.getEvent().getType());
		//Log.grava(endPoint.getEvent().getParams().getCalledAddressName());
		
		return Response.ok().build();
		
	}

}
