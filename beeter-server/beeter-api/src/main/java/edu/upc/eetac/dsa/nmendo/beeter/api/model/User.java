package edu.upc.eetac.dsa.nmendo.beeter.api.model;

import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.linking.InjectLink.Style;

import edu.upc.eetac.dsa.nmendo.beeter.api.MediaType;
import edu.upc.eetac.dsa.nmendo.beeter.api.UserResource;

public class User {

	@InjectLinks({
			@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "collection", title = "Latest stings", type = MediaType.BEETER_API_USER_COLLECTION),
			@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "self edit", title = "Users", type = MediaType.BEETER_API_USER, method = "getUser", bindings = @Binding(name = "username", value = "${instance.username}")) })
	private List<Link> links;
	private String username;
	private String name;
	private String email;
	
	/**
	 * @return the links
	 */
	public List<Link> getLinks() {
		return links;
	}
	/**
	 * @param links the links to set
	 */
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
}
