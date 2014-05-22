package edu.upc.eetac.dsa.nmendo.beeter.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import edu.upc.eetac.dsa.nmendo.beeter.api.model.BeeterRootAPI;

@Path("/")
// Esta indicada como /-> es la raíz.
public class BeeterRootAPIResource {
	
	@GET
	public BeeterRootAPI getRootAPI() {
		BeeterRootAPI api = new BeeterRootAPI();
		return api;
	}
}