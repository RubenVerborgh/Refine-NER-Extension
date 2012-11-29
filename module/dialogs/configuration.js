function ConfigurationDialog() {}

ConfigurationDialog.prototype = {
  init: function () {
    this.dialogElement = $(DOM.loadHTML("named-entity-recognition", "dialogs/configuration.html"));
    var controls = DOM.bind(this.dialogElement);
    controls.cancel.click(this.bound("hide"));
  },
  
  show: function () {
    this.init();
    this.dialogLevel = DialogSystem.showDialog(this.dialogElement);
  },
  
  hide: function () {
    DialogSystem.dismissUntil(this.dialogLevel - 1);
  },
  
  // via: http://webreflection.blogspot.be/2012/11/my-name-is-bound-method-bound.html
  bound: function (methodName) {
    var boundName = "__bound__" + methodName;
    return this[boundName] || (this[boundName] = this[methodName].bind(this));
  },
};
