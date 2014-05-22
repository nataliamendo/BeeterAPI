package edu.upc.eetac.dsa.nmendo.beeter.api.model;

import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import edu.upc.eetac.dsa.nmendo.beeter.api.StingResource;
import edu.upc.eetac.dsa.nmendo.beeter.api.MediaType;

public class Sting {
	@InjectLinks({
		@InjectLink(resource = StingResource.class, style = Style.ABSOLUTE, rel = "stings", title = "Latest stings", type = MediaType.BEETER_API_STING_COLLECTION),
		@InjectLink(resource = StingResource.class, style = Style.ABSOLUTE, rel = "self edit", title = "Sting", type = MediaType.BEETER_API_STING, method = "getSting", bindings = @Binding(name = "stingid", value = "${instance.id}")) })
	private List<Link> links;
	private String id;
	private String username;
	private String author;
	private String subject;
	private String content;
	private long lastModified;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
}