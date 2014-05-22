var username = 'alicia';
var password = 'alicia';
$.ajaxSetup({
    headers: { 'Authorization': "Basic "+$.base64.btoa(username+':'+password) }
});

// Necesario porque DELETE sting no devuelve nada. TODO: próximo cuatrimestre.
var stingsURL;

$(document).ready(function(){
	$('#edit-cancel').click(function(e){
		e.preventDefault();
		hideEditForm();
	});
	loadRootAPI(function(rootAPI){
		stingsURL = rootAPI.getLink('stings').href;
		loadStings(rootAPI.getLink('stings').href);
		$('#button-create-sting').click(function(e){
			e.preventDefault();
			var sting = new Object();
			sting.subject = $('#create-subject').val();
			sting.content = $('#create-content').val();
			var createStingLink = rootAPI.getLink('create-stings');
			createSting(createStingLink.href, createStingLink.type, JSON.stringify(sting), function(sting){
				showSting(sting);
			});
		});
	});
});

function loadStings(url){
	$('#stings-container').show();
	$('#sting-detail').hide();
	$('#create-subject').val('');
	$('#create-content').val('');
	var stings = getStings(url, function (stingCollection){
		$.each(stingCollection.stings, function(index,item){
			var sting = new Sting(item);
			var link = $('<a id="sting-link" href="'+sting.getLink("self").href+'">'+sting.subject +' ('+sting.author+')'+'</a>');
			link.click(function(e){
				e.preventDefault();
				loadSting($(e.target).attr('href'));
				return false;
			});
			var div = $('<div></div>')
			div.append(link);
			$('#stings-collection').append(div);
		});
	});
}

function loadSting(url){
	getSting(url, function(sting){
		showSting(sting);
	});
}

function showSting(sting){
	$('#stings-container').hide();
	$('#sting-detail').show();
	hideEditForm();
	
	var stingjs = new Sting(sting);

	var date = new Date(sting.lastModified);
	var hours = date.getHours();
	var minutes = date.getMinutes();
	var seconds = date.getSeconds();
	var day = date.getDate();
	var month = date.getMonth() + 1;
	var year = date.getFullYear();

	$('#subject').text(sting.subject);
	$('#content').text(sting.content);
	$('#author').text(sting.author);
	$('#modified').text(day+'/'+month+'/'+year+' '+hours+':'+minutes+':'+seconds);
	$('#back-to-stings').attr('href', '');
	$('#back-to-stings').unbind('click');
	$('#back-to-stings').click(function(e){
		e.preventDefault();
		$('#stings-collection').empty();
		loadStings(stingjs.getLink('stings').href);
		return false;
	});
	if(stingjs.username == username){
		$('#edit-sting').show();
		$('#delete-sting').show();

		$('#edit-sting').unbind('click');
		$('#delete-sting').unbind('click');

		$('#edit-sting').click(function(e){
			e.preventDefault();
			showEditForm(stingjs);
		});
		$('#delete-sting').click(function(e){
			e.preventDefault();
			$('#stings-collection').empty();
			deleteSting(stingjs.getLink('edit').href, function(){
				alert("Sting deleted");
				loadStings(stingsURL);
			});
		});
	}else{
		$('#edit-sting').hide();
		$('#delete-sting').hide();
	}
}

function showEditForm(sting){
	$('#edit-sting-form').show();
	$('#edit-subject').val(sting.subject);
	$('#edit-content').val(sting.content);
	$('#edit-ok').click(function(e){
		e.preventDefault();
		var stingData = new Object();
		stingData.subject = $('#edit-subject').val();
		stingData.content = $('#edit-content').val();
		var createStingLink = sting.getLink('edit');
		updateSting(createStingLink.href, createStingLink.type, JSON.stringify(stingData), function(sting){
			showSting(sting);
		});
	});
}

function hideEditForm(){
	$('#edit-sting-form').hide();
}