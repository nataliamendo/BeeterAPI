package edu.upc.eetac.dsa.nmendo.beeter.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.sql.DataSource;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import edu.upc.eetac.dsa.nmendo.beeter.api.model.Sting;
import edu.upc.eetac.dsa.nmendo.beeter.api.model.StingCollection;
import edu.upc.eetac.dsa.nmendo.beeter.api.model.User;

//nueva clase para las consultas de usuario
@Path("/users/{username}")
public class UserResource {
	// punto de conexión por el que llega: getInstance()
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	
	@Context
	private SecurityContext security;

	// el primer método debe porcesar peticiones GET
	// el usuario quiere consultar perfil de otro usuario
	// le pasa nombre del usuario

	// * * * *
	// url: /users/{username}

	
	/***
	 * Para que el método sea cacheable:
	 * 	1. cogemos el código que inicialment teníamos en el método getUser que devolvía
	 * 			un usuario (le pasaba el perfil a cualquiera) y lo guardamos en otro método al que llamaremos
	 *  2. El nuevo código devuelve un Respones, seguimos los pasos que en el getStings del StingResources
	 * 
	***/
	@GET
	@Produces(MediaType.BEETER_API_USER)
	public Response getUser(@PathParam("username") String username, @Context Request request) 
	{
		// Create CacheControl
		CacheControl cc = new CacheControl();

		User user = getUserFromDatabase(username);
		// Calculate the ETag on last modified date of user resource
		EntityTag eTag = new EntityTag((buildHashCode(user)));/* hay que añadir método que devuelva el hash*/

		// Verify if it matched with etag available in http request
		//se encarga de evaluar si la petición HTTP que te llega el valor de la cabecera 'if-none-match' 
			//coincide con el eTag del recurso que está almacenado/estamos pidiendo
		Response.ResponseBuilder rb = request.evaluatePreconditions(eTag);

		// If ETag matches the rb will be non-null;
		// Use the rb to return the response without any further processing
		if (rb != null) {
			return rb.cacheControl(cc).tag(eTag).build(); //304
		}

		// If rb is null then either it is first time request; or resource is
		// modified
		// Get the updated representation and return with Etag attached to it
		rb = Response.ok(user).cacheControl(cc).tag(eTag);

		return rb.build();
	}
	
	private User getUserFromDatabase(String username) {
		// guardamos el usuario en la clase Users. Para ello creamos variable
				// Users:
				User user = new User();
				
				// hacemos la conexión a la base de datos
				Connection conn = null;
				try {
					conn = ds.getConnection();
				} catch (SQLException e) {
					throw new ServerErrorException("Could not connect to the database",
							Response.Status.SERVICE_UNAVAILABLE);
				}

				PreparedStatement stmt = null;
				try {
					String sql = buildgetUserQuery(username);
					stmt = conn.prepareStatement(sql);
					stmt.setString(1, username);
					// obtenemos la respuesta
					ResultSet rs = stmt.executeQuery();

					// long oldestTimestamp = 0;

					if (rs.next()) {
						user.setUsername(rs.getString("username"));
						user.setName(rs.getString("name"));
						user.setEmail(rs.getString("email"));
					}
					else //(no hay nada,no hay usuario con ese username
					{
						throw new NotFoundException("There's no user with username="
								+ username);
					}

				} catch (SQLException e) {
					throw new ServerErrorException(e.getMessage(),
							Response.Status.INTERNAL_SERVER_ERROR);
				} finally {
					try {
						if (stmt != null)
							stmt.close();
						conn.close();
					} catch (SQLException e) {
					}
				}
				return user;
	}

	private String buildHashCode(User user)
	{
		String s= user.getName() + " " + user.getEmail();
		return Long.toString(s.hashCode());
	}

	
	private String buildgetUserQuery(String username) {
		String query = null;
		if (username != null) {
			query = "select * from users where username= ?";
		} else {
			query = null;
		}

		return query;
	}
	

	// uri: /users/{usersname}
	// Que el usuario pueda EDITAR el perfil: hacer ¿POST o PUT? PUT!!
	//sólo puede editar su perfil. Alicia solo puede editar el perfil de alicia, no editar el perfil de blas
	@PUT
	@Consumes(MediaType.BEETER_API_USER)
	@Produces(MediaType.BEETER_API_USER)
	public User updateUserFromDatabase(@PathParam("username") String username,
			User user) {
		// User user = new User();

		/*
		 * añadir validateUser antes-> confirmar username
		 */
		validateUser(username);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		
		PreparedStatement stmt = null;
		try {
			String sql = buildUpdateUser();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, user.getName());
			stmt.setString(2, user.getEmail());
			stmt.setString(3, username);

			int rows = stmt.executeUpdate();
			if (rows == 1)
				user = getUsersFromDatabase(username);
			else {
				throw new NotFoundException("There's no user with username="
						+ username);
			}

		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

		return user;
	}

	// método para crear query de update
	private String buildUpdateUser() {
		return "update users set name=ifnull(?, name), email=ifnull(?, email) where username=?";
	}

	//método añadido para comprobar que hace la consulta esté 
	private void validateUser(String username) {
		User currentUser = getUserFromDatabase(username);
		if (!security.getUserPrincipal().getName()
				.equals(currentUser.getUsername()))
			throw new ForbiddenException(
					"You are not allowed to modify this sting.");
	}
	// método para obtener objeto Users
	private User getUsersFromDatabase(String usern) {
		User user = new User();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(buildGetUsersByIdQuery());
			stmt.setString(1, usern);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				user.setUsername(rs.getString("username"));
				user.setName(rs.getString("name"));
				user.setEmail(rs.getString("email"));
			} else {
				throw new NotFoundException("There's no user with username="
						+ usern);
			}

		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

		return user;
	}

	private String buildGetUsersByIdQuery() {
		return "select * from users where username=?";
	}

	@GET
	@Path("/stings")
	@Produces(MediaType.BEETER_API_STING_COLLECTION)
	public StingCollection getStingFromUser(
			@PathParam("username") String username,
			@QueryParam("length") int length,
			@QueryParam("before") long before) 
	{
		StingCollection stings = new StingCollection();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {

			//primero comprobamos que sea nuevo
			boolean first = true;
			long oldestTimestamp = 0;
			boolean updateFromLast = before > 0; //el parámetro que pasa es distinto de cero
			stmt = conn.prepareStatement(buildGetStingsQuery(updateFromLast));
			if (before > 0)
				stmt.setTimestamp(1, new Timestamp(before));
			else
				stmt.setTimestamp(1, null);
			length = (length <= 0) ? 5 : length;
			stmt.setInt(2, length);

			String sql = buildGetStingFromUser();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			
			// obtenemos la respuesta
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) 
			{
				Sting sting = new Sting();
				sting.setId(rs.getString("stingid"));
				sting.setUsername(rs.getString("username"));
				sting.setAuthor(rs.getString("name"));
				sting.setSubject(rs.getString("subject"));
				oldestTimestamp = rs.getTimestamp("last_modified").getTime();
				sting.setLastModified(oldestTimestamp);
				if (first) {
					first = false;
					stings.setNewestTimestamp(sting.getLastModified());
				}
				stings.addSting(sting);
			}
			stings.setOldestTimestamp(oldestTimestamp);

		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
		return stings;
	}

	private String buildGetStingFromUser() {

			return "select s.*, u.name from stings s, users u where u.username=s.username and u.username=?";
	}
	
	private String buildGetStingsQuery(boolean updateFromLast) {
			return "select s.*, u.name from stings s, users u where u.username=s.username and s.last_modified < ifnull(?, now()) order by last_modified desc limit ?";
	}


}
