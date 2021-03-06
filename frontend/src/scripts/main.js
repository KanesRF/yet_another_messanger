var ChatList = require('./components/ChatList');
var AddChat = require('./components/AddChat');
var Sidebar = require('./components/Sidebar');
var AddContact = require('./components/AddContact');
var ContactList = require('./components/ContactList');
var FileList = require('./components/FileList');
var Login = require('./components/Login');
var Registration = require('./components/Registration');



function init() {
    var sidebar = new Sidebar();

    var login = new Login();
    var registration = new Registration();

    login.on('login', function () { sidebar.setPage("chats"); });
    registration.on('registration', function () { sidebar.setPage("chats"); });

    var addChat = new AddChat();
    var chatList = new ChatList();
    addChat.on('newChat', function (chatData) {
        chatList.createItem(chatData);
        sidebar.setPage("chat");
    });

    chatList.on('openChat', function (modelId) { sidebar.setPage("chat"); });

    var addContact = new AddContact();
    var contactList = new ContactList();

    addContact.on('newContact',function (contactData) { contactList.createItem(contactData); });
    contactList
        .on('itemChecked', function(contact) {addChat._onContactItemChecked(contact); })
        .on('itemUnchecked', function(contact) {addChat._onContactItemUnchecked(contact); });

    var fileList = new FileList();

}

document.addEventListener('DOMContentLoaded', init);