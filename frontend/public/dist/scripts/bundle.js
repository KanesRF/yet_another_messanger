/******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, { enumerable: true, get: getter });
/******/ 		}
/******/ 	};
/******/
/******/ 	// define __esModule on exports
/******/ 	__webpack_require__.r = function(exports) {
/******/ 		if(typeof Symbol !== 'undefined' && Symbol.toStringTag) {
/******/ 			Object.defineProperty(exports, Symbol.toStringTag, { value: 'Module' });
/******/ 		}
/******/ 		Object.defineProperty(exports, '__esModule', { value: true });
/******/ 	};
/******/
/******/ 	// create a fake namespace object
/******/ 	// mode & 1: value is a module id, require it
/******/ 	// mode & 2: merge all properties of value into the ns
/******/ 	// mode & 4: return value when already ns object
/******/ 	// mode & 8|1: behave like require
/******/ 	__webpack_require__.t = function(value, mode) {
/******/ 		if(mode & 1) value = __webpack_require__(value);
/******/ 		if(mode & 8) return value;
/******/ 		if((mode & 4) && typeof value === 'object' && value && value.__esModule) return value;
/******/ 		var ns = Object.create(null);
/******/ 		__webpack_require__.r(ns);
/******/ 		Object.defineProperty(ns, 'default', { enumerable: true, value: value });
/******/ 		if(mode & 2 && typeof value != 'string') for(var key in value) __webpack_require__.d(ns, key, function(key) { return value[key]; }.bind(null, key));
/******/ 		return ns;
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = "./src/scripts/main.js");
/******/ })
/************************************************************************/
/******/ ({

/***/ "./src/scripts/components/AddChat.js":
/*!*******************************************!*\
  !*** ./src/scripts/components/AddChat.js ***!
  \*******************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

eval("var extendConstructor = __webpack_require__(/*! ../utils/extendConstructor */ \"./src/scripts/utils/extendConstructor.js\");\nvar Eventable = __webpack_require__(/*! ../modules/Eventable */ \"./src/scripts/modules/Eventable.js\");\n\nvar ENTER_KEY_CODE = 13;\n\nvar CHATS_CHAT_INPUT_SELECTOR = '.js-chats-chat-input';\nvar CHATS_CONTACT_LIST_SELECTOR = '.js-chat-add-contact-list';\nvar HIDDEN_MODIFICATOR = \"__hidden\";\n\n/**\n * @implements {EventListener}\n * @extends {Eventable}\n * @constructor\n */\nfunction AddChatConstructor() {\n    this._input = document.querySelector(CHATS_CHAT_INPUT_SELECTOR);\n    this._input.addEventListener('keypress', this);\n    this._input.addEventListener('click', this);\n    this._checkedContacts = [];\n\n    this._chatsContactList = document.querySelector(CHATS_CONTACT_LIST_SELECTOR);\n\n    this._initEventable();\n}\n\nextendConstructor(AddChatConstructor, Eventable);\n\nvar addChatConstructorPrototype = AddChatConstructor.prototype;\n\naddChatConstructorPrototype._addItem = function () {\n    var chatInputValue = this._input.value.trim();\n\n    if (chatInputValue.length !== 0) {\n        this._input.value = '';\n    }\n\n    contactsModels = [];\n    this._checkedContacts.forEach(function(contact) {\n        contactsModels.push(contact.model);\n    });\n\n    return this.trigger('newChat', {\n        name: chatInputValue,\n        participants: contactsModels\n    });\n};\n\naddChatConstructorPrototype.handleEvent = function (e) {\n    switch (e.type) {\n        case 'keypress':\n            if (e.keyCode === ENTER_KEY_CODE) {\n                this._addItem();\n                this._checkedContacts.forEach(function(contact) {\n                    contact.uncheck();\n                });\n                this._checkedContacts = [];\n                this._chatsContactList.classList.add(HIDDEN_MODIFICATOR);\n            }\n            break;\n        case 'click':\n            this._chatsContactList.classList.remove(HIDDEN_MODIFICATOR);\n            break;\n    }\n};\n\naddChatConstructorPrototype._onContactItemChecked = function (contact) {\n    this._checkedContacts.push(contact);\n}\n\naddChatConstructorPrototype._onContactItemUnchecked = function (contact) {\n    var contactItemComponentIndex = this._checkedContacts.indexOf(contact);\n    if (contactItemComponentIndex != -1) {\n        this._checkedContacts.splice(contactItemComponentIndex, 1);\n    }\n}\n\nmodule.exports = AddChatConstructor;\n\n\n//# sourceURL=webpack:///./src/scripts/components/AddChat.js?");

/***/ }),

/***/ "./src/scripts/components/AddContact.js":
/*!**********************************************!*\
  !*** ./src/scripts/components/AddContact.js ***!
  \**********************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

eval("var extendConstructor = __webpack_require__(/*! ../utils/extendConstructor */ \"./src/scripts/utils/extendConstructor.js\");\nvar Eventable = __webpack_require__(/*! ../modules/Eventable */ \"./src/scripts/modules/Eventable.js\");\n\nvar ENTER_KEY_CODE = 13;\n\nvar CONTACT_ADD_INPUT_SELECTOR = '.js-contact-add-input';\nvar HIDDEN_MODIFICATOR = \".__hidden\";\n\n/**\n * @implements {EventListener}\n * @extends {Eventable}\n * @constructor\n */\nfunction AddContactConstructor() {\n    this._contactInput = document.querySelector(CONTACT_ADD_INPUT_SELECTOR);\n    this._contactInput.addEventListener('keypress', this);\n    this._contactInput.addEventListener('click', this);\n\n    this._initEventable();\n}\n\nextendConstructor(AddContactConstructor, Eventable);\n\nvar addContactConstructorPrototype = AddContactConstructor.prototype;\n\naddContactConstructorPrototype._addItem = function () {\n    var contactInputValue = this._contactInput.value.trim();\n\n    if (contactInputValue.length !== 0) {\n        this._contactInput.value = '';\n    }\n\n    return this.trigger('newContact', {\n        name: contactInputValue\n    });\n};\n\naddContactConstructorPrototype.handleEvent = function (e) {\n    switch (e.type) {\n        case 'keypress':\n            if (e.keyCode === ENTER_KEY_CODE) {\n                this._addItem();\n            }\n            break;\n    }\n};\n\nmodule.exports = AddContactConstructor;\n\n\n//# sourceURL=webpack:///./src/scripts/components/AddContact.js?");

/***/ }),

/***/ "./src/scripts/components/Chat.js":
/*!****************************************!*\
  !*** ./src/scripts/components/Chat.js ***!
  \****************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

eval("var Eventable = __webpack_require__(/*! ../modules/Eventable */ \"./src/scripts/modules/Eventable.js\");\nvar extendConstructor = __webpack_require__(/*! ../utils/extendConstructor */ \"./src/scripts/utils/extendConstructor.js\");\nvar Message = __webpack_require__(/*! ../components/Message */ \"./src/scripts/components/Message.js\")\nvar templatesEngine = __webpack_require__(/*! ../modules/templatesEngine */ \"./src/scripts/modules/templatesEngine.js\");\nvar FileBuffer = __webpack_require__(/*! ../components/FilesBuffer */ \"./src/scripts/components/FilesBuffer.js\");\n\nvar ENTER_KEY_CODE = 13;\n\nvar CHAT_PAGE_SELECTOR = \".js-chat-page\";\nvar FILES_TO_CHOOSE_SELECTOR = \".js-chat_files-to-choose\";\n\n/**\n * @param itemData\n * @implements {EventListener}\n * @constructor\n */\nfunction ChatConstructor(model) {\n    this._initEventable();\n\n    var templateResult = templatesEngine.chat({\n        name: model.name\n    });\n\n    this._root = templateResult.root;\n    this._name = templateResult.name;\n    this._messageInput = templateResult.messageInput;\n    this._messageList = templateResult.messageList;\n\n    this.model = model;\n\n    this._messages = [];\n    this._messagesCount = 0;\n\n    this._messageInput.addEventListener('keypress', this);\n    this._chatPage = document.querySelector(CHAT_PAGE_SELECTOR);\n    this.filesToChoose = this._root.querySelector(FILES_TO_CHOOSE_SELECTOR);\n\n    this._fileBuffer = new FileBuffer();\n    this._filesToAdd = [];\n\n    this._initEventable();\n}\n\nextendConstructor(ChatConstructor, Eventable);\n\nvar chatConstructorPrototype = ChatConstructor.prototype;\n\n/**\n * @param {HTMLElement} parent\n * @return {ChatConstructor}\n */\nchatConstructorPrototype.render = function (parent) {\n\n    this._chatPage.childNodes.forEach(function (c) {c.remove();});\n    this._chatPage.appendChild(this._root);\n    this.trigger('openChat', this.model.id);\n    return this;\n};\n\nchatConstructorPrototype._createMessage = function () {\n    var text = this._messageInput.value.trim();\n    if (text.length !== 0) {\n        this._messageInput.value = '';\n    }\n    var message = new Message({\n        id: this._messagesCount++,\n        text: text\n    });\n    message.render(this._messageList);\n    this._messageList.scrollTop = this._messageList.scrollHeight;\n}\n\nchatConstructorPrototype._parseFiles = function () {\n    var text = this._messageInput.value.trim();\n    var myRegexp = /\\[(.*?)\\]/g;\n    var match = myRegexp.exec(text);\n    while (match != null) {\n        var fileName = match[1];\n        var fileModel;\n        this._fileBuffer.getFiles().forEach(function (file) {\n            /// TODO\n        });\n        this._parsedFiles.push();\n        match = myRegexp.exec(text);\n    }\n}\n\n\n/**\n * @param {Event} e\n */\nchatConstructorPrototype.handleEvent = function (e) {\n    switch (e.type) {\n        case 'keypress':\n            switch (e.target) {\n                case this._messageInput:\n                    if (e.keyCode === ENTER_KEY_CODE) {\n                        this._createMessage();\n                    } else {\n                        this._parseFiles();\n                    }\n                    break;\n            }\n            break;\n    }\n};\n\n\n/**\n * @return {ChatConstructor}\n */\nchatConstructorPrototype.remove = function () {\n    this._root.parentNode.removeChild(this._root);\n    this.trigger('remove', this.model.id);\n    return this;\n};\n\nmodule.exports = ChatConstructor;\n\n//# sourceURL=webpack:///./src/scripts/components/Chat.js?");

/***/ }),

/***/ "./src/scripts/components/ChatList.js":
/*!********************************************!*\
  !*** ./src/scripts/components/ChatList.js ***!
  \********************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

eval("var Eventable = __webpack_require__(/*! ../modules/Eventable */ \"./src/scripts/modules/Eventable.js\");\nvar extendConstructor = __webpack_require__(/*! ../utils/extendConstructor */ \"./src/scripts/utils/extendConstructor.js\");\n\nvar ChatListItem = __webpack_require__(/*! ../components/ChatListItem */ \"./src/scripts/components/ChatListItem.js\");\n\nvar CHATS_LIST_SELECTOR = '.js-chats-list';\nvar itemsIdIterator = 0;\n\n/**\n * @extends {Eventable}\n * @constructor\n */\nfunction ChatListConstructor() {\n    /**\n     * @type {Array.<ChatsConstructor>}\n     * @private\n     */\n    this._items = [];\n    this._chatsList = document.querySelector(CHATS_LIST_SELECTOR);\n    this._initEventable();\n}\n\nextendConstructor(ChatListConstructor, Eventable);\n\nvar chatListConstructorPrototype = ChatListConstructor.prototype;\n\n/**\n * @return {Number}\n */\nchatListConstructorPrototype.getItemsCount = function () {\n    return this._items.length;\n};\n\n/**\n * @param {Object} chatsItemData\n * @return {ChatListConstructor}\n */\nchatListConstructorPrototype.createItem = function (chatsItemData) {\n    var item = new ChatListItem(Object.assign(\n        {\n            id: itemsIdIterator++,\n        },\n        chatsItemData\n    ));\n\n    this._items.push(item);\n\n    item\n        .on('remove', this._onItemRemove, this)\n        .on('openChat', this._openChat, this)\n        .render(this._chatsList)\n        .renderChatPage();\n\n    this.trigger('itemAdd', item);\n\n    return this;\n};\n\n\n/**\n * @param {Number} itemId\n * @return {ChatListItem|null}\n * @private\n */\nchatListConstructorPrototype._getItemById = function (itemId) {\n    var items = this._items;\n\n    for (var i = items.length; i-- ;) {\n        if (items[i].model.id === itemId) {\n            return items[i];\n        }\n    }\n\n    return null;\n};\n\nchatListConstructorPrototype._onItemRemove = function (itemId) {\n    var chatsItemComponent = this._getItemById(itemId);\n\n    if (chatsItemComponent) {\n        chatsItemComponent.off('remove', this._onItemRemove, this);\n        var chatsItemComponentIndex = this._items.indexOf(chatsItemComponent);\n        this._items.splice(chatsItemComponentIndex, 1);\n    }\n\n    return this;\n};\n\nchatListConstructorPrototype._openChat = function(modelId) {\n    this.trigger('openChat', modelId);\n}\n\n\nmodule.exports = ChatListConstructor;\n\n//# sourceURL=webpack:///./src/scripts/components/ChatList.js?");

/***/ }),

/***/ "./src/scripts/components/ChatListItem.js":
/*!************************************************!*\
  !*** ./src/scripts/components/ChatListItem.js ***!
  \************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

eval("var Eventable = __webpack_require__(/*! ../modules/Eventable */ \"./src/scripts/modules/Eventable.js\");\nvar Chat = __webpack_require__(/*! ../components/Chat */ \"./src/scripts/components/Chat.js\");\nvar extendConstructor = __webpack_require__(/*! ../utils/extendConstructor */ \"./src/scripts/utils/extendConstructor.js\");\nvar templatesEngine = __webpack_require__(/*! ../modules/templatesEngine */ \"./src/scripts/modules/templatesEngine.js\");\n\nvar CHAT_PAGE_SELECTOR = \".js-chat-page\";\n\n/**\n * @param itemData\n * @implements {EventListener}\n * @constructor\n */\nfunction ChatListItemConstructor(itemData) {\n    this._initEventable();\n\n    var templateResult = templatesEngine.chatItem({\n        name: itemData.name\n    });\n\n    this._root = templateResult.root;\n    this._removeAction = templateResult.removeAction;\n    this._name = templateResult.name;\n\n    this.model = {\n        id: itemData.id,\n        name: itemData.name,\n        participants: itemData.participants\n    };\n\n    this._removeAction.addEventListener('click', this);\n    this._name.addEventListener('click', this);\n\n    this._chatPage = new Chat(this.model);\n}\n\nextendConstructor(ChatListItemConstructor, Eventable);\n\nvar chatListItemConstructorPrototype = ChatListItemConstructor.prototype;\n\n/**\n * @param {HTMLElement} parent\n * @return {ChatListItemConstructor}\n */\nchatListItemConstructorPrototype.render = function (parent) {\n    parent.appendChild(this._root);\n    return this;\n};\n\nchatListItemConstructorPrototype.renderChatPage = function () {\n    this._chatPage.render();\n    this.trigger('openChat', this.model.id);\n    return this;\n};\n\n/**\n * @param {Event} e\n */\nchatListItemConstructorPrototype.handleEvent = function (e) {\n    switch (e.type) {\n        case 'click':\n            switch (e.target) {\n                case this._removeAction:\n                    this.remove();\n                    break;\n                case this._name:\n                    this.renderChatPage();\n                    break;\n\n            }\n            break;\n    }\n};\n\n/**\n * @param {String} name\n * @return {ChatListItemConstructor}\n */\nchatListItemConstructorPrototype.setText = function (name) {\n    if (this.model.name !== name) {\n        this._name.innerHTML = name;\n        this.model.name = name;\n    }\n    return this;\n};\n\n/**\n * @return {ChatListItemConstructor}\n */\nchatListItemConstructorPrototype.remove = function () {\n    this._root.parentNode.removeChild(this._root);\n    this.trigger('remove', this.model.id);\n    return this;\n};\n\nmodule.exports = ChatListItemConstructor;\n\n//# sourceURL=webpack:///./src/scripts/components/ChatListItem.js?");

/***/ }),

/***/ "./src/scripts/components/Contact.js":
/*!*******************************************!*\
  !*** ./src/scripts/components/Contact.js ***!
  \*******************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

eval("var Eventable = __webpack_require__(/*! ../modules/Eventable */ \"./src/scripts/modules/Eventable.js\");\nvar extendConstructor = __webpack_require__(/*! ../utils/extendConstructor */ \"./src/scripts/utils/extendConstructor.js\");\nvar templatesEngine = __webpack_require__(/*! ../modules/templatesEngine */ \"./src/scripts/modules/templatesEngine.js\");\n\n/**\n * @param itemData\n * @implements {EventListener}\n * @constructor\n */\nfunction ContactConstructor(itemData, type) {\n    this._initEventable();\n    switch (type) {\n        case \"main\":\n            var templateResult = templatesEngine.contactItem({name: itemData.name });\n            break;\n        case \"chat\":\n            var templateResult = templatesEngine.chatAddContactItem({name: itemData.name });\n            this._checkAction = templateResult.checkAction;\n            this._checkAction.addEventListener('change', this);\n            break;\n    }\n    this._root = templateResult.root;\n    this._removeAction = templateResult.removeAction;\n    this._name = templateResult.name;\n\n    this.model = {\n        id: itemData.id,\n        name: itemData.name\n    };\n\n    if (itemData.isReady) {\n        this._setReadyModificator(true);\n    }\n\n    this._removeAction.addEventListener('click', this);\n}\n\nextendConstructor(ContactConstructor, Eventable);\n\nvar contactListItemConstructorPrototype = ContactConstructor.prototype;\n\n/**\n * @param {HTMLElement} parent\n * @return {ContactConstructor}\n */\ncontactListItemConstructorPrototype.render = function (parent) {\n    parent.appendChild(this._root);\n    return this;\n};\n\ncontactListItemConstructorPrototype.uncheck = function() {\n    this._checkAction.checked = false;\n};\n\n/**\n * @param {Event} e\n */\ncontactListItemConstructorPrototype.handleEvent = function (e) {\n    switch (e.type) {\n        case 'click':\n            if (e.target === this._removeAction) {\n                this.remove();\n            }\n            break;\n        case 'change':\n            if (e.target === this._checkAction) {\n                if (this._checkAction.checked) {\n                    this.trigger('checked', this);\n                } else {\n                    this.trigger('unchecked', this);\n                }\n            }\n            break;\n    }\n};\n\n/**\n * @param {String} name\n * @return {ContactConstructor}\n */\ncontactListItemConstructorPrototype.setText = function (name) {\n    if (this.model.name !== name) {\n        this._name.innerHTML = name;\n        this.model.name = name;\n    }\n    return this;\n};\n\n\n/**\n * @return {ContactConstructor}\n */\ncontactListItemConstructorPrototype.remove = function () {\n    this._root.parentNode.removeChild(this._root);\n    this.trigger('remove', this.model.id);\n    return this;\n};\n\nmodule.exports = ContactConstructor;\n\n//# sourceURL=webpack:///./src/scripts/components/Contact.js?");

/***/ }),

/***/ "./src/scripts/components/ContactList.js":
/*!***********************************************!*\
  !*** ./src/scripts/components/ContactList.js ***!
  \***********************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

eval("var Eventable = __webpack_require__(/*! ../modules/Eventable */ \"./src/scripts/modules/Eventable.js\");\nvar extendConstructor = __webpack_require__(/*! ../utils/extendConstructor */ \"./src/scripts/utils/extendConstructor.js\");\n\nvar Contact = __webpack_require__(/*! ../components/Contact */ \"./src/scripts/components/Contact.js\");\n\nvar CONTACT_LIST_SELECTOR = '.js-contact-list';\nvar CHAT_ADD_CONTACT_LIST_SELECTOR = '.js-chat-add-contact-list';\nvar itemsIdIterator = 0;\n\n/**\n * @extends {Eventable}\n * @constructor\n */\nfunction ContactListConstructor() {\n    /**\n     * @type {Array.<ChatsConstructor>}\n     * @private\n     */\n    this._items = [];\n    this._itemsChat = [];\n    this._contactList = document.querySelector(CONTACT_LIST_SELECTOR);\n    this._chatAddContactList = document.querySelector(CHAT_ADD_CONTACT_LIST_SELECTOR);\n\n    this._initEventable();\n}\n\nextendConstructor(ContactListConstructor, Eventable);\n\nvar contactListConstructorPrototype = ContactListConstructor.prototype;\n\n/**\n * @return {Number}\n */\ncontactListConstructorPrototype.getItemsCount = function () {\n    return this._items.length;\n};\n\n/**\n * @param {Object} contactItemData\n * @return {ContactListConstructor}\n */\ncontactListConstructorPrototype.createItem = function (contactItemData) {\n    var itemMainContactList = new Contact(Object.assign(\n        {\n            id: itemsIdIterator,\n        },\n        contactItemData\n    ), \"main\");\n\n    var itemChatContactList = new Contact(Object.assign(\n        {\n            id: itemsIdIterator++,\n        },\n        contactItemData\n    ), \"chat\");\n\n    this._items.push(itemMainContactList);\n    this._itemsChat.push(itemChatContactList);\n\n    itemMainContactList\n        .on('remove', this._onItemMainContactListRemove, this)\n        .render(this._contactList);\n\n    itemChatContactList\n        .on('remove', this._onItemChatContactListRemove, this)\n        .on('checked', this._onItemChecked, this)\n        .on('unchecked', this._onItemChecked, this)\n        .render(this._chatAddContactList);\n\n    return this;\n};\n\n\n/**\n * @param {Number} itemId\n * @return {ChatsListItem|null}\n * @private\n */\ncontactListConstructorPrototype._getItemById = function (items, itemId) {\n    for (var i = items.length; i-- ;) {\n        if (items[i].model.id === itemId) {\n            return items[i];\n        }\n    }\n\n    return null;\n};\n\ncontactListConstructorPrototype._onItemMainContactListRemove = function (itemId) {\n    var contactItemComponent = this._getItemById(this._items, itemId);\n    var contactItemComponentChat = this._getItemById(this._itemsChat, itemId);\n\n    if (contactItemComponent) {\n        contactItemComponent.off('remove', this._onItemRemove, this);\n        var contactItemComponentIndex = this._items.indexOf(contactItemComponent);\n        this._items.splice(contactItemComponentIndex, 1);\n    }\n    if (contactItemComponentChat) {\n        contactItemComponentChat.remove();\n    }\n\n    return this;\n};\n\ncontactListConstructorPrototype._onItemChatContactListRemove = function (itemId) {\n    var contactItemComponent = this._getItemById(this._items, itemId);\n    var contactItemComponentChat = this._getItemById(this._itemsChat, itemId);\n\n    if (contactItemComponentChat) {\n        contactItemComponentChat.off('remove', this._onItemRemove, this);\n        var contactItemComponentIndex = this._items.indexOf(contactItemComponentChat);\n        this._itemsChat.splice(contactItemComponentIndex, 1);\n    }\n    if (contactItemComponent) {\n        contactItemComponent.remove();\n    }\n\n    return this;\n};\n\ncontactListConstructorPrototype._onItemChecked = function (contact) {\n    this.trigger('itemChecked', contact);\n}\n\ncontactListConstructorPrototype._onItemUnchecked = function (itemId) {\n    this.trigger('itemUnchecked', contact);\n}\n\n\nmodule.exports = ContactListConstructor;\n\n//# sourceURL=webpack:///./src/scripts/components/ContactList.js?");

/***/ }),

/***/ "./src/scripts/components/File.js":
/*!****************************************!*\
  !*** ./src/scripts/components/File.js ***!
  \****************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

eval("var Eventable = __webpack_require__(/*! ../modules/Eventable */ \"./src/scripts/modules/Eventable.js\");\nvar extendConstructor = __webpack_require__(/*! ../utils/extendConstructor */ \"./src/scripts/utils/extendConstructor.js\");\nvar templatesEngine = __webpack_require__(/*! ../modules/templatesEngine */ \"./src/scripts/modules/templatesEngine.js\");\n\n/**\n * @param itemData\n * @implements {EventListener}\n * @constructor\n */\nfunction FileConstructor(model) {\n    this._initEventable();\n\n    var templateResult = templatesEngine.file(model);\n\n    this._root = templateResult.root;\n    this._removeAction = templateResult.removeAction;\n    this._name = templateResult.name;\n\n    this.model = {\n        id: model.id,\n        name: model.name,\n    };\n\n    this._removeAction.addEventListener('click', this);\n}\n\nextendConstructor(FileConstructor, Eventable);\n\nvar fileConstructorPrototype = FileConstructor.prototype;\n\n/**\n * @param {HTMLElement} parent\n * @return {FileConstructor}\n */\nfileConstructorPrototype.render = function (parent) {\n    parent.appendChild(this._root);\n    return this;\n};\n\n\n/**\n * @param {Event} e\n */\nfileConstructorPrototype.handleEvent = function (e) {\n    switch (e.type) {\n        case 'click':\n            switch (e.target) {\n                case this._removeAction:\n                    this.remove();\n                    break;\n            }\n            break;\n    }\n};\n\n/**\n * @return {FileConstructor}\n */\nfileConstructorPrototype.remove = function () {\n    this._root.parentNode.removeChild(this._root);\n    this.trigger('remove', this.model.id);\n    return this;\n};\n\nmodule.exports = FileConstructor;\n\n//# sourceURL=webpack:///./src/scripts/components/File.js?");

/***/ }),

/***/ "./src/scripts/components/FileList.js":
/*!********************************************!*\
  !*** ./src/scripts/components/FileList.js ***!
  \********************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

eval("var Eventable = __webpack_require__(/*! ../modules/Eventable */ \"./src/scripts/modules/Eventable.js\");\nvar extendConstructor = __webpack_require__(/*! ../utils/extendConstructor */ \"./src/scripts/utils/extendConstructor.js\");\nvar FileBuffer = __webpack_require__(/*! ../components/FilesBuffer */ \"./src/scripts/components/FilesBuffer.js\");\n\nvar File = __webpack_require__(/*! ../components/File */ \"./src/scripts/components/File.js\");\n\nvar FILE_PAGE_SELECTOR = '.js-files-page';\nvar FILE_LIST_SELECTOR = '.js-file-list';\nvar FILE_INPUT_SELECTOR = '.js-file-input';\n\nvar itemsIdIterator = 0;\n\n/**\n * @extends {Eventable}\n * @constructor\n */\nfunction FileListConstructor() {\n    /**\n     * @type {Array.<ChatsConstructor>}\n     * @private\n     */\n    this._items = [];\n    this._filePage = document.querySelector(FILE_PAGE_SELECTOR);\n    this._fileList = this._filePage.querySelector(FILE_LIST_SELECTOR);\n    this._fileInput = this._filePage.querySelector(FILE_INPUT_SELECTOR);\n\n    this._fileInput.addEventListener('change', this);\n\n    this._fileBuffer = new FileBuffer();\n\n    this._initEventable();\n}\n\nextendConstructor(FileListConstructor, Eventable);\n\nvar fileListConstructorPrototype = FileListConstructor.prototype;\n\n/**\n * @return {Number}\n */\nfileListConstructorPrototype.getItemsCount = function () {\n    return this._items.length;\n};\n\n/**\n * @param {Object} fileItemData\n * @return {FileListConstructor}\n */\nfileListConstructorPrototype.createItem = function () {\n    var files = this._fileInput.files;\n    for (let i = 0; i < files.length; i++) {\n        var file = files[i];\n        var reader = new FileReader();\n        reader.readAsDataURL(file);\n        var result;\n        reader.addEventListener(\"load\", function () {\n            result = reader.result;\n        }, false);\n        var name = file.name;\n        var base64 = btoa(result);\n\n        var fileItem = new File(Object.assign(\n            {\n                id: itemsIdIterator++,\n                name: name,\n            },\n        ));\n        fileItem\n            .on('remove', this._onItemRemove, this)\n            .render(this._fileList);\n        this._items.push(fileItem);\n        this._fileBuffer.addFile(fileItem.model);\n        this.trigger('fileAdd', fileItem.model);\n    };\n    this._fileInput.value = \"\";\n    return this;\n};\n\nfileListConstructorPrototype.handleEvent = function (e) {\n    switch (e.type) {\n        case 'change':\n            this.createItem();\n            break;\n    }\n};\n\n\n/**\n * @param {Number} itemId\n * @return {ChatsListItem|null}\n * @private\n */\nfileListConstructorPrototype._getItemById = function (items, itemId) {\n    for (var i = items.length; i-- ;) {\n        if (items[i].model.id === itemId) {\n            return items[i];\n        }\n    }\n\n    return null;\n};\n\nfileListConstructorPrototype._onItemRemove = function (itemId) {\n    var item = this._getItemById(itemId);\n\n    if (item) {\n        var itemIndex = this._items.indexOf(item);\n        this._items.splice(itemIndex, 1);\n    }\n\n    return this;\n};\n\nmodule.exports = FileListConstructor;\n\n//# sourceURL=webpack:///./src/scripts/components/FileList.js?");

/***/ }),

/***/ "./src/scripts/components/FilesBuffer.js":
/*!***********************************************!*\
  !*** ./src/scripts/components/FilesBuffer.js ***!
  \***********************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

eval("var Eventable = __webpack_require__(/*! ../modules/Eventable */ \"./src/scripts/modules/Eventable.js\");\nvar extendConstructor = __webpack_require__(/*! ../utils/extendConstructor */ \"./src/scripts/utils/extendConstructor.js\");\n\nvar files = [];\n\n/**\n * @implements {EventListener}\n * @extends {Eventable}\n * @constructor\n */\nfunction FileBufferConstructor() {\n\n    this._initEventable();\n}\n\nextendConstructor(FileBufferConstructor, Eventable);\n\nvar fileBufferConstructorPrototype = FileBufferConstructor.prototype;\n\nfileBufferConstructorPrototype.addFile = function(file) {\n    files.push(file);\n}\n\nfileBufferConstructorPrototype.getFiles = function() {\n    return files;\n}\n\nfileBufferConstructorPrototype.removeFile = function(file) {\n    files.push(file);\n}\n\nmodule.exports = FileBufferConstructor;\n\n//# sourceURL=webpack:///./src/scripts/components/FilesBuffer.js?");

/***/ }),

/***/ "./src/scripts/components/Message.js":
/*!*******************************************!*\
  !*** ./src/scripts/components/Message.js ***!
  \*******************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

eval("var Eventable = __webpack_require__(/*! ../modules/Eventable */ \"./src/scripts/modules/Eventable.js\");\nvar extendConstructor = __webpack_require__(/*! ../utils/extendConstructor */ \"./src/scripts/utils/extendConstructor.js\");\nvar templatesEngine = __webpack_require__(/*! ../modules/templatesEngine */ \"./src/scripts/modules/templatesEngine.js\");\n\n/**\n * @param itemData\n * @implements {EventListener}\n * @constructor\n */\nfunction MessageConstructor(model) {\n    this._initEventable();\n\n    this.model = model;\n    var templateResult = templatesEngine.message(model);\n    this._root = templateResult.root;\n}\n\nextendConstructor(MessageConstructor, Eventable);\n\nvar messageConstructorPrototype = MessageConstructor.prototype;\n\n/**\n * @param {HTMLElement} parent\n * @return {MessageConstructor}\n */\nmessageConstructorPrototype.render = function (parent) {\n    parent.appendChild(this._root);\n    return this;\n};\n\n/**\n * @param {Event} e\n */\nmessageConstructorPrototype.handleEvent = function (e) {\n    switch (e.type) {}\n};\n\n\n/**\n * @return {MessageConstructor}\n */\nmessageConstructorPrototype.remove = function () {\n    this._root.parentNode.removeChild(this._root);\n    this.trigger('remove', this.model.id);\n    return this;\n};\n\nmodule.exports = MessageConstructor;\n\n//# sourceURL=webpack:///./src/scripts/components/Message.js?");

/***/ }),

/***/ "./src/scripts/components/Sidebar.js":
/*!*******************************************!*\
  !*** ./src/scripts/components/Sidebar.js ***!
  \*******************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

eval("var extendConstructor = __webpack_require__(/*! ../utils/extendConstructor */ \"./src/scripts/utils/extendConstructor.js\");\nvar Eventable = __webpack_require__(/*! ../modules/Eventable */ \"./src/scripts/modules/Eventable.js\");\n\nvar SIDEBAR_SELECTOR = \".js-sidebar\";\n\nvar SIDEBAR_CHATS_BUTTON_SELECTOR = \".js-sidebar-list_chats\";\nvar SIDEBAR_CONTACTS_BUTTON_SELECTOR = \".js-sidebar-list_contacts\";\nvar SIDEBAR_FILES_BUTTON_SELECTOR = \".js-sidebar-list_files\";\n\nvar CHATS_PAGE_SELECTOR = \".js-chats-list-page\";\nvar FILES_PAGE_SELECTOR = \".js-files-page\";\nvar CONTACTS_PAGE_SELECTOR = \".js-contacts-page\";\nvar CHAT_PAGE_SELECTOR = \".js-chat-page\";\n\nvar HIDDEN_MODIFICATOR = \"__hidden\";\nvar ACTIVE_MODIFICATOR = \"__active\";\n\n/**\n * @implements {EventListener}\n * @extends {Eventable}\n * @constructor\n */\nfunction SidebarConstructor() {\n    this._sidebar = document.querySelector(SIDEBAR_SELECTOR);\n\n    this._sidebar_chats_button = this._sidebar.querySelector(SIDEBAR_CHATS_BUTTON_SELECTOR);\n    this._sidebar_contacts_button = this._sidebar.querySelector(SIDEBAR_CONTACTS_BUTTON_SELECTOR);\n    this._sidebar_files_button = this._sidebar.querySelector(SIDEBAR_FILES_BUTTON_SELECTOR);\n\n    this._chats_page = document.querySelector(CHATS_PAGE_SELECTOR);\n    this._contacts_page = document.querySelector(CONTACTS_PAGE_SELECTOR);\n    this._files_page = document.querySelector(FILES_PAGE_SELECTOR);\n    this._chat_page = document.querySelector(CHAT_PAGE_SELECTOR);\n\n    this._sidebar_chats_button.addEventListener('click', this);\n    this._sidebar_contacts_button.addEventListener('click', this);\n    this._sidebar_files_button.addEventListener('click', this);\n\n    this._initEventable();\n}\n\nextendConstructor(SidebarConstructor, Eventable);\n\nvar sidebarConstructorPrototype = SidebarConstructor.prototype;\n\nsidebarConstructorPrototype.handleEvent = function (e) {\n    switch (e.type) {\n        case 'click':\n            switch (e.target) {\n                case this._sidebar_chats_button:\n                    this.setPage(\"chats\");\n                    break;\n                case this._sidebar_contacts_button:\n                    this.setPage(\"contacts\");\n                    break;\n                case this._sidebar_files_button:\n                    this.setPage(\"files\");\n                    break;\n            }\n    }\n};\n\nsidebarConstructorPrototype.setPage = function (name) {\n    this._sidebar_chats_button.classList.remove(ACTIVE_MODIFICATOR);\n    this._sidebar_contacts_button.classList.remove(ACTIVE_MODIFICATOR);\n    this._sidebar_files_button.classList.remove(ACTIVE_MODIFICATOR);\n\n    this._chats_page.classList.add(HIDDEN_MODIFICATOR);\n    this._contacts_page.classList.add(HIDDEN_MODIFICATOR);\n    this._files_page.classList.add(HIDDEN_MODIFICATOR);\n    this._chat_page.classList.add(HIDDEN_MODIFICATOR);\n\n    switch (name) {\n        case \"chats\":\n            this._sidebar_chats_button.classList.add(ACTIVE_MODIFICATOR);\n            this._chats_page.classList.remove(HIDDEN_MODIFICATOR);\n            break;\n        case \"contacts\":\n            this._sidebar_contacts_button.classList.add(ACTIVE_MODIFICATOR);\n            this._contacts_page.classList.remove(HIDDEN_MODIFICATOR);\n            break;\n        case \"files\":\n            this._sidebar_files_button.classList.add(ACTIVE_MODIFICATOR);\n            this._files_page.classList.remove(HIDDEN_MODIFICATOR);\n            break;\n        case \"chat\":\n            this._chat_page.classList.remove(HIDDEN_MODIFICATOR);\n\n    }\n}\n\nmodule.exports = SidebarConstructor;\n\n\n//# sourceURL=webpack:///./src/scripts/components/Sidebar.js?");

/***/ }),

/***/ "./src/scripts/main.js":
/*!*****************************!*\
  !*** ./src/scripts/main.js ***!
  \*****************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

eval("var ChatList = __webpack_require__(/*! ./components/ChatList */ \"./src/scripts/components/ChatList.js\");\nvar AddChat = __webpack_require__(/*! ./components/AddChat */ \"./src/scripts/components/AddChat.js\");\nvar Sidebar = __webpack_require__(/*! ./components/Sidebar */ \"./src/scripts/components/Sidebar.js\");\nvar AddContact = __webpack_require__(/*! ./components/AddContact */ \"./src/scripts/components/AddContact.js\");\nvar ContactList = __webpack_require__(/*! ./components/ContactList */ \"./src/scripts/components/ContactList.js\");\nvar FileList = __webpack_require__(/*! ./components/FileList */ \"./src/scripts/components/FileList.js\");\n\n\nfunction init() {\n    var sidebar = new Sidebar();\n\n    var addChat = new AddChat();\n    var chatList = new ChatList();\n    addChat.on('newChat', function (chatData) {\n        chatList.createItem(chatData);\n        sidebar.setPage(\"chat\");\n    });\n\n    chatList.on('openChat', function (modelId) { sidebar.setPage(\"chat\"); });\n\n    var addContact = new AddContact();\n    var contactList = new ContactList();\n\n    addContact.on('newContact',function (contactData) { contactList.createItem(contactData); });\n    contactList\n        .on('itemChecked', function(contact) {addChat._onContactItemChecked(contact); })\n        .on('itemUnchecked', function(contact) {addChat._onContactItemUnchecked(contact); });\n\n    var fileList = new FileList();\n\n}\n\ndocument.addEventListener('DOMContentLoaded', init);\n\n//# sourceURL=webpack:///./src/scripts/main.js?");

/***/ }),

/***/ "./src/scripts/modules/Eventable.js":
/*!******************************************!*\
  !*** ./src/scripts/modules/Eventable.js ***!
  \******************************************/
/*! no static exports found */
/***/ (function(module, exports) {

eval("function Eventable() {}\n\nvar eventablePrototype = Eventable.prototype;\n\neventablePrototype._initEventable = function () {\n    this._eventable_registry = {};\n};\n\nfunction getEventSubscribers(eventable, eventName, needCreate) {\n    var registry = eventable._eventable_registry;\n\n    if (eventName in registry) {\n        return registry[eventName];\n\n    } else if (needCreate) {\n        return registry[eventName] = [];\n    }\n\n    return null;\n}\n\neventablePrototype.on = function (eventName, handler, ctx) {\n    var subscribers = getEventSubscribers(this, eventName, true);\n\n    subscribers.push({\n        handler: handler,\n        ctx: ctx\n    });\n\n    return this;\n};\n\neventablePrototype.off = function (eventName, handler, ctx) {\n    var subscribers = getEventSubscribers(this, eventName);\n\n    if (subscribers) {\n        for (var i = subscribers.length; i-- ;) {\n            if ((subscribers[i].handler === handler)\n                && (subscribers[i].ctx === ctx)\n            ) {\n                subscribers.splice(i, 1);\n                return this;\n            }\n        }\n    }\n\n    return this;\n};\n\neventablePrototype.trigger = function (eventName, data) {\n    var subscribers = getEventSubscribers(this, eventName);\n\n    if (subscribers) {\n        var subscribersCopy = subscribers.slice();\n        for (var i = 0, l = subscribersCopy.length; i !== l; i += 1) {\n            subscribersCopy[i].handler.call(subscribersCopy[i].ctx, data);\n        }\n    }\n\n    return this;\n};\n\nmodule.exports = Eventable;\n\n//# sourceURL=webpack:///./src/scripts/modules/Eventable.js?");

/***/ }),

/***/ "./src/scripts/modules/templatesEngine.js":
/*!************************************************!*\
  !*** ./src/scripts/modules/templatesEngine.js ***!
  \************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

eval("\nvar div = document.createElement('div');\n\nfunction getTemplateRootNode(scriptId) {\n    var scriptTag = document.getElementById(scriptId);\n    div.innerHTML = scriptTag.innerHTML;\n    var result = div.children[0];\n    div.removeChild(result);\n    return result;\n}\n\nvar templatesEngine = {\n    chatItem: function (data) {\n        var root = getTemplateRootNode('chatsListItem');\n\n        var removeAction = root.querySelector('.js-chats-list-item_remove-action');\n        var name = root.querySelector('.js-chats-list-item_name');\n\n        if (data.name) {\n            name.innerText = data.name;\n        }\n\n        return {\n            root: root,\n            name: name,\n            removeAction: removeAction\n        };\n    },\n\n    contactItem: function (data) {\n        var root = getTemplateRootNode('contactListItem');\n\n        var removeAction = root.querySelector('.js-contact-list-item_remove-action');\n        var name = root.querySelector('.js-contact-list-item_name');\n\n        if (data.name) {\n            name.innerText = data.name;\n        }\n\n        return {\n            root: root,\n            name: name,\n            removeAction: removeAction\n        };\n    },\n\n    chatAddContactItem: function (data) {\n        var root = getTemplateRootNode('chatAddContactListItem');\n\n        var removeAction = root.querySelector('.js-chat-add-contact-list-item_remove-action');\n        var checkAction = root.querySelector(\".js-chat-add-contact-list-item_check\");\n        var name = root.querySelector('.js-chat-add-contact-list-item_name');\n\n        if (data.name) {\n            name.innerText = data.name;\n        }\n\n        return {\n            root: root,\n            name: name,\n            removeAction: removeAction,\n            checkAction: checkAction\n        };\n    },\n\n    chat: function (data) {\n        var root = getTemplateRootNode('chat');\n        var name = root.querySelector('.js-chat_name');\n        var messageList = root.querySelector('.js-chat_message-list');\n        var messageInput = root.querySelector('.js-chat_message-text');\n        if (data.name) {\n            name.innerText = data.name;\n        }\n        return {\n            root: root,\n            name: name,\n            messageList: messageList,\n            messageInput: messageInput\n        };\n    },\n\n    message: function (data) {\n        var root = getTemplateRootNode('message');\n        var text = root.querySelector('.js-message_text');\n        var creatorName = root.querySelector('.js-message_creator-name');\n        var createdAt = root.querySelector('.s-message_created-at');\n        var avatar = root.querySelector('.js-message_creator-avatar');\n\n        if (data.text) {\n            text.innerText = data.text;\n        }\n        if (data.creatorName) {\n            creatorName.innerText = data.creatorName;\n        }\n        if (data.createdAt) {\n            createdAt.innerText = data.createdAt;\n        }\n        if (data.avatar) {\n            avatar.innerText = data.avatar;\n        }\n\n        return {\n            root: root\n        };\n    },\n\n    file: function (data) {\n        var root = getTemplateRootNode('file');\n        var name = root.querySelector('.js-file_name');\n        var creatorName = root.querySelector('.js-file_creator-name');\n        var createdAt = root.querySelector('.file_created-at');\n        var removeAction = root.querySelector('.js-file_remove-action');\n\n        if (data.name) {\n            name.innerText = data.name;\n        }\n        if (data.creatorName) {\n            creatorName.innerText = data.creatorName;\n        }\n        if (data.createdAt) {\n            createdAt.innerText = data.createdAt;\n        }\n\n        return {\n            root: root,\n            removeAction: removeAction\n        };\n\n    }\n};\n\nmodule.exports = templatesEngine;\n\n//# sourceURL=webpack:///./src/scripts/modules/templatesEngine.js?");

/***/ }),

/***/ "./src/scripts/utils/extendConstructor.js":
/*!************************************************!*\
  !*** ./src/scripts/utils/extendConstructor.js ***!
  \************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

eval("/**\n * @param {Function} Extendable\n * @param {Function} Extension\n * @return {Function} Extendable\n */\nfunction extendConstructor(Extendable, Extension) {\n    var extendablePrototype = Extendable.prototype;\n    var extensionPrototype = Extension.prototype;\n\n    for (var p in extensionPrototype) {\n        extendablePrototype[p] = extensionPrototype[p];\n    }\n\n    return Extendable;\n}\n\nmodule.exports = extendConstructor;\n\n//# sourceURL=webpack:///./src/scripts/utils/extendConstructor.js?");

/***/ })

/******/ });