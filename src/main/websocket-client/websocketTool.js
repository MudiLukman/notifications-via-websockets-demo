var wsocket;

function connect() {
	wsocket = new WebSocket("ws://localhost:8080/websockets/kontrol");
	wsocket.onopen = onopen;
	wsocket.onmessage = onmessage;
	wsocket.onclose = onclose;
}

function onopen() {
	console.log("Connected!");
}

function onmessage(event) {
	console.log("Notification received: " + event.data);
	var tag = document.createElement("div");
	tag.id = "message";
	var text = document.createTextNode(JSON.parse(event.data).message);
	tag.appendChild(text);
	var element = document.getElementById("events");
	element.style.color = "#009900"; //dark green
	element.appendChild(tag);
}

function onclose(e) {
	console.log("Connection closed.");
}

window.addEventListener("load", connect, false);