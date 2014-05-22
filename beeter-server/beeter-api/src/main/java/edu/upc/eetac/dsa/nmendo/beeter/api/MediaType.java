package edu.upc.eetac.dsa.nmendo.beeter.api;


//Define los tipos de 'media type' que utilizaremos
//vnd-> quien es dueño, a quien le pertenece
public interface MediaType {
	public final static String BEETER_API_USER = "application/vnd.beeter.api.user+json";
	public final static String BEETER_API_USER_COLLECTION = "application/vnd.beeter.api.user.collection+json";
	public final static String BEETER_API_STING = "application/vnd.beeter.api.sting+json";
	public final static String BEETER_API_STING_COLLECTION = "application/vnd.beeter.api.sting.collection+json";
	
	//nueva media type para error:
	public final static String BEETER_API_ERROR = "application/vnd.dsa.beeter.error+json";
}