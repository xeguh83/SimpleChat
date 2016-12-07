/**
 * Created by например Андрей on 04.12.2016.
 */
// создать подключение
var socket = new WebSocket("ws://localhost:80");

// отправить сообщение из формы publish
document.forms.publish.onsubmit = function() {
    var outgoingMessage = this.message.value;

    socket.send(outgoingMessage);
    return false;
};

// обработчик входящих сообщений
socket.onmessage = function(event) {
    var incomingMessage = event.data;
    showMessage(incomingMessage);
};

// показать сообщение в div#subscribe
function showMessage(message) {
    var messageElem = document.createElement('div');
    messageElem.appendChild(document.createTextNode(message));
    document.getElementById('subscribe').appendChild(messageElem);
}
