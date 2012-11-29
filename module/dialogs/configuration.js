function ConfigurationDialog() {}

ConfigurationDialog.prototype = {
  init: function () {
    this.dialogElement = $(DOM.loadHTML("named-entity-recognition", "dialogs/configuration.html"));
    var controls = DOM.bind(this.dialogElement);
    controls.cancel.click(this.bound("hide"));
    
    $.getJSON(NERExtension.commandPath + "services", function (services) {
      var $services = $(controls.services);
      services.forEach(function (service) {
        var settings = service.settings,
            $service = $('<fieldset/>').append($('<legend/>').text(service.name)),
            $settings = $('<ol>').appendTo($service);
        for(var settingName in settings) {
          var id = encodeURIComponent(service.name + '-' + settingName);
          $settings.append($('<li>')
              .append($('<label>', { 'for': id, text: settingName }),
                      $('<input>', { id: id, value: settings[settingName] })));
        }
        $services.append($service);
      });
    });
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
