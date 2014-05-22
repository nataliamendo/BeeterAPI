var BEETER_API_HOME="http://localhost:8080/beeter-api";

function Link(rel, linkheader){
	this.rel = rel;
	this.href = decodeURIComponent(linkheader.find(rel).template().template);
	this.type = linkheader.find(rel).attr('type');
	this.title = linkheader.find(rel).attr('title');
}

function buildLinks(linkheaders){
	var links = {};
	$.each(linkheaders, function(i,linkheader){
		var linkhdr = $.linkheaders(linkheader);
		var rels = linkhdr.find().attr('rel').split(' ');
		$.each(rels, function(key,rel){
			var link = new Link(rel, linkhdr);
			links[rel] = link;
		});
	});

	return links;
}

function Sting(sting){
	this.username = sting.username;
	this.id = sting.id;
	this.author = sting.author;
	this.lastModified = sting.lastModified;
	this.subject = sting.subject;
	this.content = sting.content;
	this.links = buildLinks(sting.links);

	var instance = this;
	this.getLink = function(rel){
		return this.links[rel];
	}
}

function StingCollection(stingCollection){
	this.oldestTimestamp = stingCollection.oldestTimestamp;
	this.newestTimestamp = stingCollection.newestTimestamp;
	this.stings = stingCollection.stings;

	this.links = buildLinks(stingCollection.links);
	var instance = this;
	this.getLink = function(rel){
		return this.links[rel];
	}
}

function RootAPI(rootAPI){
	this.links = buildLinks(rootAPI.links);
	var instance = this;
	this.getLink = function(rel){
		return this.links[rel];
	}
}


function loadRootAPI(success){
	$.ajax({
		url : BEETER_API_HOME,
		type : 'GET',
		crossDomain : true,
		dataType: 'json'
	})
	.done(function (data, status, jqxhr) {
		var response = $.parseJSON(jqxhr.responseText);
		var rootAPI = new RootAPI(response);
    	success(rootAPI);
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
	
}

function getStings(url, success){
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType: 'json'
	})
	.done(function (data, status, jqxhr) {
		var response = $.parseJSON(jqxhr.responseText);
		var stingCollection = new StingCollection(response);
		//success(response.stings);
		success(stingCollection);
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}

function createSting(url, type, sting, success){
	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		contentType: type, 
		data: sting
	})
	.done(function (data, status, jqxhr) {
		var sting = $.parseJSON(jqxhr.responseText);
		success(sting);
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}

function getSting(url, success){
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType: 'json'
	}).done(function (data, status, jqxhr) {
		var sting = $.parseJSON(jqxhr.responseText);
		success(sting);
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}

function updateSting(url, type, sting, success){
	$.ajax({
		url : url,
		type : 'PUT',
		crossDomain : true,
		contentType: type, 
		data: sting
	})
	.done(function (data, status, jqxhr) {
		var sting = $.parseJSON(jqxhr.responseText);
		success(sting);
		//console.log("SUCCESS ON PUT!!");
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}

function deleteSting(url, success){
	$.ajax({
		url : url,
		type : 'DELETE',
		crossDomain : true
	})
	.done(function (data, status, jqxhr) {
		//var sting = $.parseJSON(jqxhr.responseText);
		success();
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}